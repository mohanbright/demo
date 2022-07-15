package com.journalmetro.app.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.journalmetro.app.BR
import com.journalmetro.app.R
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.preferences.AppPreferences
import com.journalmetro.app.common.util.*
import com.journalmetro.app.databinding.ActivityMainBinding
import com.journalmetro.app.post.model.ContentRendered
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.ui.common.activity.BaseActivity
import com.journalmetro.app.ui.common.view.AlertHelper
import com.journalmetro.app.ui.expandView.ExpandGalleryViewBottomSheetDialog


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    // ************************
    // ADVERTISEMENT MANAGEMENT
    // When you change this ad unit id, do NOT forget update ad unit id these XML files:
    // item_post.xml & item_local_post.xml & item_video_post.xml.
    //private val adsUnitId = "/6499/example/banner" // General Ads Unit Id.
    private val adsUnitId = "/21658289790,22389335471/journalmetro_app/accueil" // General Ads Unit Id.
    private val isAdsActive = true // Manage ads on here. False: Close, True: Open.
    // ************************

    override var layoutId: Int = R.layout.activity_main
    override var viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    override var viewModelBindingVariable: Int = BR.viewModel

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var adRequest: AdRequest? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null

    // Update list when adding new fragment on bottom bar.
    private val bottomNavigationFragmentList = arrayListOf(
        R.id.homeMainFragment,
        R.id.myArticleListFragment,
        R.id.localFragment,
        R.id.videoListFragment,
        R.id.sectionsFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Initialize Ad Request.
        adRequest = AdManagerAdRequest.Builder().build()

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        // Define navigation host.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(layoutBinding.bottomNavView, navController)
        appBarConfiguration = AppBarConfiguration.Builder().build()
        appBarConfiguration.topLevelDestinations.addAll(bottomNavigationFragmentList)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()
        setUpFirebase()
        setupObservers()
        setupListeners()
    }

    //get FID from firebase and save in prefs
    private fun setUpFirebase() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: ${task.result}")
                saveFirebaseInstallationIDDefault(task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }

        //get user notification token provided by firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("token_failed", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            sendFirebaseTokenToServer(token)
        })
    }


    //Send push token on server along with installationId
    private fun sendFirebaseTokenToServer(refreshToken: String) {
        val fid : String? = prefs.getString(AppPreferences.FID, "")
        val serverToken : String? = prefs.getString(AppPreferences.REFRESH_TOKEN_SENT_ON_SERVER, "")

        //Remove installation id from token if exists
        when {
            serverToken.isNullOrEmpty() -> {
                //POST call - api call for the first time
                viewModel.sendPushTokenToServer(fid!!, refreshToken, isUpdate = false).observe(this, {
                    pushTokenSendResult(refreshToken, it)
                })
            }
            serverToken != refreshToken -> {
                //PUT call - update token on server
                viewModel.sendPushTokenToServer(fid!!, refreshToken, isUpdate = true).observe(this, {
                    pushTokenSendResult(refreshToken, it)
                })
            }
            else -> {
                //No need to call api - token already stored on server
            }
        }
    }


    // Manage action for post and update token result.
    private fun pushTokenSendResult(refreshToken: String, response: Resource<Void>) {
        if (response.statusCode == 200) {
            saveFirebaseTokenDefault(refreshToken)//when token successfully sent on server - save it to prefs.
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveFirebaseInstallationIDDefault(fid: String) {
        if (fid.isNotEmpty()) {
            prefs.edit().putString(AppPreferences.FID, fid).commit()
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveFirebaseTokenDefault(token: String) {
        if (token.isNotEmpty()) {
            prefs.edit().putString(AppPreferences.REFRESH_TOKEN_SENT_ON_SERVER, token).commit()//save token to prefs
        }
    }

    private fun setupObservers() {
        setupFullScreenObserver()//This is using for observing full screen button on player
    }

    private fun setupListeners() {
        layoutBinding.bottomNavView.setOnNavigationItemSelectedListener(
            onNavigationItemSelectedListener
        )
    }

    override fun onStop() {
        super.onStop()
        removeListeners()
        removeObservers()
    }

    private fun removeObservers() {

    }

    private fun removeListeners() {
        layoutBinding.bottomNavView.setOnNavigationItemSelectedListener(null)
    }

    // Get firebase analytics safely.
    private fun getSafeFirebaseAnalytics(): FirebaseAnalytics {
        if (firebaseAnalytics == null) firebaseAnalytics = Firebase.analytics
        return firebaseAnalytics!!
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar?.title = title
    }

    fun setActionBarCustom(restId: Int?) {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        restId?.let {
            supportActionBar?.let { actionBar ->
                actionBar.setCustomView(it)
                val parent = actionBar.customView.parent as Toolbar
                parent.setContentInsetsAbsolute(0, 0) // Remove spaces.
            }
        }
    }

    fun setActionBarCustom(view: View?) {
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        view?.let {
            supportActionBar?.let { actionBar ->
                actionBar.setCustomView(view, params)
                val parent = view.parent as Toolbar
                parent.setContentInsetsAbsolute(0, 0) // Remove spaces.
            }
        }
    }

    fun hideActionBar() {
        supportActionBar?.hide()
    }

    fun showActionBar() {
        supportActionBar?.show()
    }

    fun hideTitleBar() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun homeAsUpEnabled(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
    }

    fun getSupportBar(): ActionBar? {
        return supportActionBar
    }

    fun getAdRequest(): AdRequest {
        if (adRequest == null) adRequest = AdManagerAdRequest.Builder().build()
        return adRequest!!
    }

    fun hideBottomNavigationView() {
        layoutBinding.bottomNavView.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        layoutBinding.bottomNavView.visibility = View.VISIBLE
    }

    fun showSharingDialog(text: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, getString(R.string.title_sharing)))
    }

    fun showSoftKeyboard(view: View) {
        val imm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            navigateToBottomFragment(item.itemId)
        }

    private fun navigateToBottomFragment(navGraphFragmentId: Int) : Boolean {
        return if (bottomNavigationFragmentList.contains(navGraphFragmentId)) {

            callMessageLoadingError(false) // Disappear error message with travelling.

            val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, true).build()
            navController.navigate(
                navGraphFragmentId,
                getBundleBottomNavigationFragment(navGraphFragmentId),
                navOptions
            )
            true
        }
        else false
    }

    // Get bundle for passing data which is Safe-Args.
    private fun getBundleBottomNavigationFragment(navGraphFragmentId: Int) : Bundle? {
        return when (navGraphFragmentId) {
            R.id.homeMainFragment -> {
                null // Does not have safe-args.
            }
            R.id.localFragment -> {
                null // Does not have safe-args.
            }
            R.id.myArticleListFragment -> {
                null // Does not have safe-args.
            }
            R.id.videoListFragment -> {
                null // Does not have safe-args.
            }
            else -> null
        }
    }

    // Top Bar Icon Clicks.
    // Top Bar Back.
    fun onClickTopBarBackButton(view: View) {
        onBackPressed()
        hideTheKeyboard()
    }

    // Send email.
    fun sendEmail(text: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {

            // Only email apps should handle this.
            data = Uri.parse("mailto:")

            // Define message.
            val message = if (text.isNotEmpty()) text else getString(R.string.editor_email)

            // Put extras.
            putExtra(Intent.EXTRA_EMAIL, arrayOf(message))
        }
        try {
            startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            AlertHelper.showToastLong(this, getString(R.string.info_not_found_email_app))
        }
    }

    /* Exit full screen mode if is active. */
    override fun onBackPressed() {
        if (viewModel.isFullScreenMode.value == true){
            viewModel._isFullScreenMode.postValue(false)
        } else {
            super.onBackPressed()
        }
    }

    //To manage player full screen toggle manually
    fun handleVisibilityOtherViews(playerFullscreen: Boolean) {
        viewModel._isFullScreenMode.postValue(playerFullscreen)
    }

    //To listen full screen toggle
    private fun setupFullScreenObserver() {
        viewModel.isFullScreenMode.observe(this, {
            changeScreenOrientationMode(it)
        })
    }

    //manage view control visibility when orientation change
    private fun changeScreenOrientationMode(setLandscapeMode : Boolean) {
        requestedOrientation = if(setLandscapeMode) {
            hideSystemUI() //hide top status bar and system controls in landscape model
            hideBottomNavigationView() //hide bottom navigation bar in landscape mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE //change full screen orientation in landscape.
        } else {
            showSystemUI()//show top status bar and system controls in portrait model
            showBottomNavigationView()//show bottom navigation bar in portrait mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT  //change screen orientation to portrait
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, layoutBinding.frameLayout).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        //WindowInsetsCompat.Type.systemBars have issue in hiding version >= R
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            hideActionBar()
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, layoutBinding.frameLayout).show(WindowInsetsCompat.Type.systemBars())

        //WindowInsetsCompat.Type.systemBars have issue in hiding version >= R
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            showActionBar()
    }

    // General loading error message. It disappears error message with click.
    // You can add your custom function which works with try again button click.
    fun setupListenerMessageLoadingError(onClickLoadingErrorTryAgainButton: () -> Unit) {
        layoutBinding.messageLoadingErrorLayout.buttonTryAgain.setOnClickListener {

            onClickLoadingErrorTryAgainButton() // Custom function for your fragment work.

            callMessageLoadingError(false) // Do not need show after click.
        }

        callMessageLoadingError(false)
    }

    // General loading error message showing. Show loading error if api has error.
    fun showMessageLoadingError() {
        callMessageLoadingError(true)
    }

    // General loading error message removing. Remove loading error when page changes.
    fun removeMessageLoadingError() {
        callMessageLoadingError(false)
    }

    // General loading error message.
    private fun callMessageLoadingError(isVisible: Boolean) {
        layoutBinding.messageLoadingError.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Send section id to google analytics.
    fun sendLogsToGoogleSectionId(sectionId: Long) {
        sendLogsToGoogleAnalytics(tagSectionEvent, tagSectionContentType, tagSectionId, sectionId)
    }

    // Send post data to google analytics.
    fun sendLogsToGooglePostData(post: Post) {
        sendLogsToGooglePostId(post.id.getSafeLong())
        sendLogsToGooglePostLink(post.link.getSafeSting())
        sendLogsToGooglePostTitle(post.title.getSafeSting())
    }

    // Send post id to google analytics.
    private fun sendLogsToGooglePostId(postId: Long) {
        sendLogsToGoogleAnalytics(tagDetailsEvent, tagDetailsContentType, tagPostId, postId)
    }

    // Send post link to google analytics.
    private fun sendLogsToGooglePostLink(postLink: String) {
        sendLogsToGoogleAnalytics(tagDetailsEvent, tagDetailsContentType, tagPostLink, postLink)
    }

    // Send post title to google analytics.
    private fun sendLogsToGooglePostTitle(postTitle: String) {
        sendLogsToGoogleAnalytics(tagDetailsEvent, tagDetailsContentType, tagPostTitle, postTitle)
    }

    // Send logs on firebase analytics with String value.
    private fun sendLogsToGoogleAnalytics(
        eventType: String, contentType: String, key: String, value: String
    ) {

        // Create bundle and put parameters.
        val parameters: Bundle = Bundle().apply {
            this.putString(key, value)
            this.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }

        // Get firebase analytics safely and send log if value is not blank.
        if (value.isNotBlank()) getSafeFirebaseAnalytics().logEvent(eventType, parameters)
    }

    // Send logs on firebase analytics with Long value.
    private fun sendLogsToGoogleAnalytics(
        eventType: String, contentType: String, key: String, value: Long
    ) {

        // Create bundle and put parameters.
        val parameters: Bundle = Bundle().apply {
            this.putLong(key, value)
            this.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }

        // Get firebase analytics safely and send log if value is not blank.
        if (value != 0L) getSafeFirebaseAnalytics().logEvent(eventType, parameters)
    }


    // Check Ads available ?
    fun isAdsAvailable(): Boolean {
        return isAdsActive
    }

    // Get general ads unit id.
    fun getAdsUnitId(): String {
        return adsUnitId
    }

    // open images in expand view with list and content need to show on selected post.
    fun openGalleryExpandViewBottomSheetFragmentDialog(post: Post, list :List<ContentRendered>) {
        ExpandGalleryViewBottomSheetDialog.post = post
        ExpandGalleryViewBottomSheetDialog.list = list
        ExpandGalleryViewBottomSheetDialog().show(supportFragmentManager, ExpandGalleryViewBottomSheetDialog.TAG)
    }
}
