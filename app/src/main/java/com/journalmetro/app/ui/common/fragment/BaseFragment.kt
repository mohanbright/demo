package com.journalmetro.app.ui.common.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.dailymotion.android.player.sdk.PlayerWebView
import com.dailymotion.android.player.sdk.events.ApiReadyEvent
import com.dailymotion.android.player.sdk.events.PlaybackReadyEvent
import com.dailymotion.android.player.sdk.events.StartEvent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.journalmetro.app.BuildConfig
import com.journalmetro.app.R
import com.journalmetro.app.common.util.*
import com.journalmetro.app.homeSection.model.HomePost
import com.journalmetro.app.post.model.ContentRendered
import com.journalmetro.app.ui.MainViewModel
import com.journalmetro.app.ui.common.adapter.GalleryPagerAdapter
import com.journalmetro.app.ui.common.view.AlertHelper
import com.journalmetro.app.ui.common.viewmodel.ViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


/**
 * Abstract base class for fragment creation.
 *
 * Base implementation of the basic needs and actions of a fragment class
 *
 * @param LayoutBinding Reference to the fragment's layout binding class
 * @param VM Reference to the fragment's ViewModel class
 */

abstract class BaseFragment<LayoutBinding : ViewDataBinding, VM : ViewModel> :
    Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected lateinit var viewModel: VM
    abstract var viewModelBindingVariable: Int
    protected abstract var layoutResId: Int

    private var _binding: LayoutBinding? = null
    protected val layoutBinding get() = _binding!!

    protected lateinit var mainViewModel: MainViewModel

    var spotifyPlaylistWebView: WebView? = null

    /**
     * List of programmatically created player web views to release on clean up.
     */
    private val createdVideoPlayers = mutableListOf<PlayerWebView>()

    abstract fun provideViewModel(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel()
        mainViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MainViewModel::class.java
        )
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataBindingVariables(layoutBinding)
        layoutBinding.lifecycleOwner = this
        layoutBinding.executePendingBindings()
    }

    protected open fun setDataBindingVariables(binding: ViewDataBinding) {
        binding.setVariable(viewModelBindingVariable, viewModel)
    }

    override fun onDestroyView() {
        // Clean up player web views.
        this.createdVideoPlayers.forEach(PlayerWebView::release)
        super.onDestroyView()
        _binding = null
    }

    private fun performDependencyInjection() = run { AndroidSupportInjection.inject(this) }

    protected fun showToastMessageShort(message: String) {
        context?.let { it -> AlertHelper.showToastShort(it, message) }
    }

    protected fun showToastMessageShort(message: Int) {
        context?.let { it -> AlertHelper.showToastShort(it, getString(message)) }
    }

    protected fun showToastMessageLong(message: String) {
        context?.let { it -> AlertHelper.showToastLong(it, message) }
    }

    protected fun showToastMessageLong(message: Int) {
        context?.let { it -> AlertHelper.showToastLong(it, getString(message)) }
    }

    // Get programmatically TextView.
    // Developer added new line "\n" for each content.
    protected fun getProgrammaticallyTextView(
        text: String = "", size: Float, textStyle: String = "", font: Typeface? = null,
        paddingLeft: Int = 0,
        paddingTop: Int = 0,
        paddingRight: Int = 0,
        paddingBottom: Int = 0,
        colorId: Int = R.color.colorTextGrey_3,
        lineSpace: Float = 1.4f,
        span: Spannable? = null
    ): TextView {

        val textViewDynamic = TextView(context)
        textViewDynamic.textSize = size

        // This check for Blockquote.
        if (span == null) {
            textViewDynamic.typeface = font ?: getTextStyle(textStyle)
            textViewDynamic.text = "\n$text"
        } else textViewDynamic.setText(span, TextView.BufferType.SPANNABLE)

        textViewDynamic.setLineSpacing(0f, lineSpace)
        textViewDynamic.setTextColor(ContextCompat.getColor(requireContext(), colorId))
        textViewDynamic.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.mainBackground
            )
        )

        // Define params.
        val params = getParams(true, false)

        textViewDynamic.layoutParams = params
        textViewDynamic.setPadding(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        ) // Set margin.

        return textViewDynamic
    }

    // Get programmatically TextView for Bullet Points.
    protected fun getProgrammaticallyTextViewForBulletPoint(
        text: String = "", size: Float, textStyle: String = "", font: Typeface? = null,
        paddingLeft: Int = 0,
        paddingTop: Int = 0,
        paddingRight: Int = 0,
        paddingBottom: Int = 0,
        colorId: Int = R.color.colorTextGrey_3,
        lineSpace: Float = 1.2f,
        span: Spannable? = null
    ): TextView {

        val textViewDynamic = TextView(context)
        textViewDynamic.textSize = size

        textViewDynamic.setText(span, TextView.BufferType.SPANNABLE)

        textViewDynamic.setLineSpacing(0f, lineSpace)
        textViewDynamic.setTextColor(ContextCompat.getColor(requireContext(), colorId))
        textViewDynamic.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackground))

        // Define params.
        val params = getParams(true, false)

        textViewDynamic.layoutParams = params
        textViewDynamic.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom) // Set margin.

        return textViewDynamic
    }

    // Get programmatically TextView for Titles.
    protected fun getProgrammaticallyTextViewTitle(
        text: String,
        size: Float,
        font: Typeface,
        colorId: Int = R.color.colorTextGrey_3,
        marginLeft: Int = 0
    ): TextView {

        val params = getParams(false, false)
        params.setMargins(dpToPx(marginLeft), 0, 0, 0)

        val textViewDynamic = TextView(context)
        textViewDynamic.textSize = size
        textViewDynamic.typeface = font
        textViewDynamic.text = "\n$text" // <-- Developer added to see result.
        textViewDynamic.setTextColor(ContextCompat.getColor(requireContext(), colorId))

        textViewDynamic.layoutParams = params
        return textViewDynamic
    }

    // Get programmatically Layout already have Title and Arrow.
    protected fun getProgrammaticallyLayoutTitleWithArrow(
        title: String,
        subtitle: String,
        size: Float,
        font: Typeface
    ): LinearLayout {

        // Title.
        val textViewDynamicTitle = getProgrammaticallyTextViewTitle(title, size, font)

        // Arrow.
        val imageView = getProgrammaticallyImageView(imageResId = R.drawable.ic_arrow_right)
        val paramsImageView = getParams(false, false)
        paramsImageView.setMargins(dpToPx(14), 0, 0, dpToPx(4))
        paramsImageView.height = dpToPx(24)
        paramsImageView.gravity = Gravity.BOTTOM
        imageView.layoutParams = paramsImageView

        // Subtitle.
        val textViewDynamicSubtitle = getProgrammaticallyTextViewTitle(
            subtitle,
            15f,
            getFontAcuminProCondBold(),
            R.color.colorTextGrey_1
        )
        val paramsSubtitle = getParams(true, false)
        paramsSubtitle.setMargins(0, 0, 0, dpToPx(2))
        paramsSubtitle.gravity = Gravity.BOTTOM
        textViewDynamicSubtitle.layoutParams = paramsSubtitle
        textViewDynamicSubtitle.gravity = Gravity.END

        // Horizontal layout.
        val layout = LinearLayout(context)
        val paramsLayout = getParams(true, false)
        paramsLayout.setMargins(dpToPx(20), 0, dpToPx(20), 0)
        layout.layoutParams = paramsLayout
        layout.orientation = LinearLayout.HORIZONTAL

        // Create view.
        layout.addView(textViewDynamicTitle) // Title.
        layout.addView(imageView) // Arrow.
        layout.addView(textViewDynamicSubtitle) // Subtitle.

        return layout
    }

    // Get programmatically quote or citation view.
    protected fun getProgrammaticallyQuote(
        title: String,
        content: String,
        author: String,
        role: String
    ): LinearLayout {

        // Main layout.
        val layoutMain = LinearLayout(context)
        val paramsLayoutMain = getParams(true, false)
        paramsLayoutMain.setMargins(0, dpToPx(28), 0, 0)
        layoutMain.orientation = LinearLayout.VERTICAL
        layoutMain.layoutParams = paramsLayoutMain
        layoutMain.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorQuoteTheme
            )
        )

        // Title layout.
        val layoutTitle = LinearLayout(context)
        val paramsLayoutTitle = getParams(false, false)
        paramsLayoutTitle.gravity = Gravity.CENTER_HORIZONTAL
        layoutTitle.layoutParams = paramsLayoutTitle
        layoutTitle.orientation = LinearLayout.VERTICAL

        // Title.
        val titleView = getProgrammaticallyTextView(
            text = title,
            size = 28f,
            font = getFontAcuminProExtraCondItalic(),
            lineSpace = 1.2f
        )
        val paramsTitleView = getParams(false, false)
        titleView.layoutParams = paramsTitleView
        paramsTitleView.gravity = Gravity.CENTER_HORIZONTAL
        titleView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorQuoteTheme
            )
        )
        titleView.isAllCaps = true

        // Title line.
        val titleLineView = getProgrammaticallyViewLine()

        // Content.
        val unicodeCharFward = 0x00BB.toChar() // U+20D6 unicode for «
        val unicodeCharBWard = 0x00AB.toChar()  // U+00AB unicode for »
        val contentWithQuotes =
            StringBuilder().append(unicodeCharBWard).append(content).append(unicodeCharFward)
                .toString()

        val contentView = getProgrammaticallyTextView(
            text = contentWithQuotes,
            size = 34f,
            font = getFontAcuminProCondBold(),
            paddingLeft = dpToPx(40),
            paddingRight = dpToPx(40),
            paddingTop = dpToPx(-20),
            lineSpace = 1.2f
        )
        val paramsContentView = getParams(false, false)
        contentView.layoutParams = paramsContentView
        contentView.gravity = Gravity.CENTER_HORIZONTAL
        paramsContentView.gravity = Gravity.CENTER_HORIZONTAL
        contentView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorQuoteTheme
            )
        )

        // Author.
        val authorView = getProgrammaticallyTextView(
            text = author,
            size = 22f,
            font = getFontAcuminProCondBold(),
            paddingTop = dpToPx(-10),
            lineSpace = 1.2f
        )
        val paramsAuthorView = getParams(false, false)
        paramsAuthorView.setMargins(dpToPx(12), 0, dpToPx(12), 0)
        authorView.layoutParams = paramsAuthorView
        authorView.gravity = Gravity.CENTER_HORIZONTAL
        paramsAuthorView.gravity = Gravity.CENTER_HORIZONTAL
        authorView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorQuoteTheme
            )
        )
        authorView.isAllCaps = true

        // Role.
        val roleView = getProgrammaticallyTextView(
            text = role,
            size = 18f,
            font = getFontAcuminProLight(),
            paddingTop = dpToPx(-20),
            paddingBottom = dpToPx(36),
            lineSpace = 1.2f
        )
        val paramsRoleView = getParams(false, false)
        roleView.layoutParams = paramsRoleView
        roleView.gravity = Gravity.CENTER_HORIZONTAL
        paramsRoleView.gravity = Gravity.CENTER_HORIZONTAL
        roleView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorQuoteTheme
            )
        )

        // Create view for title.
        layoutTitle.addView(titleView)
        layoutTitle.addView(titleLineView)

        // Create main view.
        layoutMain.addView(layoutTitle)
        layoutMain.addView(contentView)
        layoutMain.addView(authorView)
        layoutMain.addView(roleView)

        return layoutMain
    }

    // Get text style.
    private fun getTextStyle(type: String): Typeface {
        return when {
            type.isTaxonomyOfCite() -> getFontAcuminProExtraCondItalic()
            type.isTaxonomyOfHs() -> getFontAcuminProBold()
            type.isTaxonomyOfP() -> getFontAcuminProRegular()
            else -> getFontAcuminProRegular()
        }
    }

    // Get programmatically View for separation line between items.
    protected fun getProgrammaticallyViewLine(
        size: Int = 3,
        marginTop: Int = 0,
        marginBottom: Int = 0,
        marginLeft: Int = 0,
        marginRight: Int = 0
    ): View {
        val viewLineDynamic = View(context)
        val params = getParams(true, false)
        params.setMargins(
            dpToPx(marginLeft),
            dpToPx(marginTop),
            dpToPx(marginRight),
            dpToPx(marginBottom)
        )
        viewLineDynamic.layoutParams = params
        viewLineDynamic.layoutParams.height = size
        viewLineDynamic.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.quantum_black_100
            )
        )
        return viewLineDynamic
    }

    // Get programmatically View for blockquote.
    protected fun getProgrammaticallyBlockquote(): LinearLayout {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlockquote))

        val params = getParams(true, false)
        params.setMargins(0, 24, 0, 0)
        layout.layoutParams = params

        layout.setPadding(18, 0, 0, 0)

        return layout
    }

    // Get programmatically View for BulletPoint.
    protected fun getProgrammaticallyBulletPoint(): LinearLayout {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val params = getParams(true, false)
        params.setMargins(0, 24, 0, 0)
        layout.layoutParams = params

        layout.setPadding(18, 0, 0, 0)

        return layout
    }

    // Get programmatically View for thematic break.
    protected fun getProgrammaticallyThematicBreak(): LinearLayout {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER
        val params = getParams(true, false)
        params.setMargins(0, 24, 0, 0)
        layout.layoutParams = params

        layout.setPadding(18, 108, 18, 0)

        return layout
    }

    // Get programmatically View for advertisement.
    // AdSize parameter is not using for now because we have size list. However, I do not remove it,
    // because we need to use specific size in future.
    protected fun getProgrammaticallyAdsView(
        adSize: AdSize, // For now, not using.
        adsId: String,
        marginLeft: Int = 0,
        marginTop: Int = 0,
        marginRight: Int = 0,
        marginBottom: Int = 0,
    ): AdManagerAdView {

        val adView = AdManagerAdView(requireContext()) // Create Ad view.

        // Create params for Ad View.
        val params = getParams(true, false)

        params.setMargins(marginLeft, marginTop, marginRight, marginBottom) // Set margin.
        params.gravity = Gravity.CENTER_HORIZONTAL // Set gravity.
        adView.layoutParams = params // Set params.

        //adView.adSize = adSize // ads size. // It closed for now. Size list is using.
        adView.setAdSizes(
            AdSize(300, 250),
            AdSize(300, 600),
            AdSize(320, 50),
        )
        adView.adUnitId = adsId // Id of ads.
        adView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorDividerBorough
            )
        )
        adView.loadAd(getAdRequest()) // Load ads in ads view. This is important.

        // Add listener to follow errors.
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)

                // If you need to see error detail, you can use below method. Just open it.
                //showAdsLoadingErrors(error) // See ads loading error.
            }
        }

        return adView
    }

    // Get programmatically Dailymotion Video View.
    // Returns container(Relative Layout) having video player and progress bar.
    protected fun getProgrammaticallyDailyMotionPlayer(videoLink: String): ViewGroup {

        val container = RelativeLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val player = PlayerWebView(context)


        val playerHeightDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            context?.resources?.getDimension(R.dimen.player_default_height) ?: 230f,
            resources.displayMetrics
        ).toInt()
        val params =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeightDp)
        player.layoutParams = params
        player.setBackgroundResource(R.color.colorVideoBackground)
        container.addView(player)

        player.setIsWebContentsDebuggingEnabled(BuildConfig.DEBUG) // Enable debug mode in debug.
        player.playWhenReady = false //disable auto play of videos.
        player.mute() //mute value initially

        val videoId = videoLink.getVideoIdForDailyMotionVideoPlayer()
        val playerParams = mapOf(
            "video" to videoId,
            "autoplay" to "false",
            "ui-highlight" to "1D443E", // This is color value.
            "ui-logo" to "false",
            "queue-enable" to "false",
            "queue-autoplay-next" to "false"
        )
        player.load(playerParams)
        player.pause() //prevents the video from playing, until user interaction.

        val progressBar = container.createAndAddProgressBar(
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply { addRule(RelativeLayout.CENTER_IN_PARENT) }, videoId
        )

        player.setEventListener { event ->
            when (event) {
                is ApiReadyEvent, is StartEvent -> progressBar.visibility = View.VISIBLE
                is PlaybackReadyEvent -> progressBar.visibility = View.GONE
                else -> {}
            }
        }

        this.createdVideoPlayers.add(player)
        return container
    }


    // Get iframe html code with video url for web view video player.
    protected fun getIframeHtmlForWebViewDailyMotionVideo(url: String): String {
        val iFrameHtml = StringBuilder()
        iFrameHtml.append("<html>")
            .append("<body>")
            .append("<iframe ")
            .append("src=\"$url\"")
            .append("></iframe>")
            .append("</body>")
            .append("</html>")
        return iFrameHtml.toString()
    }

    // Get programmatically Image View.
    protected fun getProgrammaticallyImageView(
        isWidthMatchParent: Boolean = true,
        isHeightMatchParent: Boolean = true,
        imageLink: String = "",
        imageResId: Int = 0
    ): ImageView {
        val imageView = ImageView(context)

        val params = getParams(isWidthMatchParent, isHeightMatchParent)
        params.setMargins(0, dpToPx(5), 0, 0)
        imageView.layoutParams = params

        if (imageResId == 0)
            Glide.with(this).load(imageLink).into(imageView) // Load image in view.
        else
            imageView.setImageResource(imageResId) // Set image resource.

        return imageView
    }


    // Get programmatically ImageView view Pager Slider.
    protected fun getProgrammaticallyImageViewPager(
        isWidthMatchParent: Boolean = true,
        isHeightMatchParent: Boolean = true,
        contentRenderedChildItems: List<ContentRendered>,
        callback: GalleryPagerAdapter.ImageSelectionCallback
    ): LinearLayout {
        val context = requireContext()

        //Parent layout
        val parentLinearLayout = LinearLayout(context)
        parentLinearLayout.orientation = LinearLayout.VERTICAL

        val paramsLL = getParams(isWidthMatchParent, isHeightMatchParent)
        paramsLL.height = dpToPx(230)
        paramsLL.setMargins(0, dpToPx(10), 0, dpToPx(10))
        parentLinearLayout.layoutParams = paramsLL
        parentLinearLayout.gravity = Gravity.CENTER

        //Create Pager Layout
        val galleryViewPager = ViewPager(context)
        galleryViewPager.adapter =
            GalleryPagerAdapter(requireContext(), contentRenderedChildItems, callback, true)

        //Create count text params
        val paramsCountText = getParams(false, false)
        paramsCountText.gravity = Gravity.RIGHT
        paramsCountText.setMargins(0, dpToPx(-30), dpToPx(10), 0)

        //Create a count text and set layout params
        val countTextView = getProgrammaticallyTextViewTitle(
            "1/${contentRenderedChildItems.size}",
            12f,
            getFontAcuminProBold()
        )
        countTextView.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_gallery_count)
        countTextView.setTextColor(Color.WHITE)
        countTextView.gravity = Gravity.CENTER
        countTextView.setPadding(dpToPx(10), dpToPx(2), dpToPx(10), dpToPx(2))
        countTextView.layoutParams = paramsCountText
        countTextView.text = "1/${contentRenderedChildItems.size}"

        //Change count text on pager change
        galleryViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(p0: Int) {
                val newCount = p0 + 1
                countTextView.text = "$newCount/${contentRenderedChildItems.size}"
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageScrollStateChanged(p0: Int) {

            }
        })

        //Add items to parent layout
        parentLinearLayout.addView(galleryViewPager)
        parentLinearLayout.addView(countTextView)
        return parentLinearLayout
    }

    // Get programmatically Recycler View.
    protected fun getProgrammaticallyRecyclerView(
        isVertical: Boolean = true,
        marginLeft: Int = 0,
        marginRight: Int = 0
    ): RecyclerView {
        val mContext = requireContext()
        val recyclerView = RecyclerView(mContext)

        val params = getParams(true, false)
        params.setMargins(dpToPx(marginLeft), 0, dpToPx(marginRight), 0)

        recyclerView.layoutParams = params
        recyclerView.layoutManager = LinearLayoutManager(
            mContext,
            if (isVertical) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
            false
        )
        return recyclerView
    }

    // Get programmatically Card View.
    protected fun getProgrammaticallyCardView(posts: List<HomePost>): CardView {
        val mContext = requireContext()
        val postData: HomePost = posts[0]

        val parentLinearLayout = LinearLayout(mContext)
        parentLinearLayout.orientation = LinearLayout.VERTICAL
        val padding = dpToPx(18)

        val params = getParams(true, false)
        parentLinearLayout.layoutParams = params

        val cardView = CardView(mContext)
        cardView.setCardBackgroundColor(
            ContextCompat.getColor(
                mContext,
                R.color.colorHomeCardBackground
            )
        )
        cardView.radius = 0f
        cardView.layoutParams = params
        cardView.cardElevation = 0f

        //Image on card
        // Define image view if image url resource is not null.
        val imageView: ImageView = if (postData.featuredMedia == null) ImageView(requireContext())
        else getProgrammaticallyImageView(imageLink = postData.featuredMedia.sourceUrl.getSafeSting())
        imageView.layoutParams = params

        //Title Text
        val heading =
            getProgrammaticallyTextViewTitle(postData.getParsedTitle(), 28f, getFontAcuminProBold())
        //Content Text
        val title = getProgrammaticallyTextViewTitle(
            postData.getParsedExcerpt(),
            16f,
            getFontAcuminProRegular()
        )

        //A La Une text
        val smallText = getProgrammaticallyTextViewTitle(
            mContext.resources.getString(R.string.in_news),
            19f,
            getFontAcuminProExtraCondItalic()
        )
        smallText.setBackgroundColor(Color.WHITE)
        smallText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorHomeCardBackground
            )
        )
        smallText.setPadding(0, 5, 0, 5)

        //TextViewCompat.setAutoSizeTextTypeWithDefaults(smallText, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        smallText.gravity = Gravity.CENTER
        smallText.height = dpToPx(40)
        smallText.width = dpToPx(70)
        smallText.isSingleLine = true

        //Set text in center
        val llParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParams.gravity = Gravity.CENTER
        llParams.setMargins(0, dpToPx(-15), 0, 0)
        smallText.layoutParams = llParams

        heading.gravity = Gravity.CENTER
        heading.setTextColor(Color.WHITE)
        heading.setPadding(padding, 0, padding, 0)

        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.setPadding(padding, 0, padding, dpToPx(10))

        //add views to LinearLayout.
        parentLinearLayout.addView(imageView)
        parentLinearLayout.addView(smallText)
        parentLinearLayout.addView(heading)
        parentLinearLayout.addView(title)

        //add linear layout to cardview
        cardView.addView(parentLinearLayout)
        return cardView
    }

    //Get grey background view
    protected fun getProgrammaticallyGreyBackgroundLayout(
        marginTop: Int = 0,
        marginBottom: Int = 0,
        paddingTop: Int = 0,
        paddingBottom: Int = 0,
        paddingLeft: Int = 0,
        paddingRight: Int = 0,
    ): LinearLayout {
        val linearLayout = LinearLayout(requireContext())
        val params = getParams(true, false)
        linearLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorDividerBorough
            )
        )
        params.gravity = Gravity.CENTER_HORIZONTAL
        linearLayout.orientation = LinearLayout.VERTICAL
        // Set margin.
        params.setMargins(0, dpToPx(marginTop), 0, dpToPx(marginBottom))
        linearLayout.setPadding(
            dpToPx(paddingLeft),
            dpToPx(paddingTop),
            dpToPx(paddingRight),
            dpToPx(paddingBottom)
        )
        linearLayout.layoutParams = params // Set params.
        return linearLayout
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProBlack(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_black)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_black) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProBold(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_bold)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_bold) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProCondBlack(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_cond_black)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_cond_black) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProCondBold(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_cond_bold)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_cond_bold) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProCondMedium(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_cond_medium)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_cond_medium) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProCondRegular(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_cond_regular)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_cond_regular) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProExtraCondItalic(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_extra_cond_italic)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_extra_cond_italic) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProLight(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_light)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_light) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProRegular(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_regular)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_regular) }!!
    }

    // Get Acumin type font as Typeface for Text View, according to Build Version Code.
    protected fun getFontAcuminProSemiBold(): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.acumin_pro_semibold)
        else context?.let { ResourcesCompat.getFont(it, R.font.acumin_pro_semibold) }!!
    }

    // Get params for programmatically creating views.
    protected fun getParams(
        isWidthMatch: Boolean,
        isHeightMatch: Boolean
    ): LinearLayout.LayoutParams {
        val width =
            if (isWidthMatch) LinearLayout.LayoutParams.MATCH_PARENT
            else LinearLayout.LayoutParams.WRAP_CONTENT

        val height =
            if (isHeightMatch) LinearLayout.LayoutParams.MATCH_PARENT
            else LinearLayout.LayoutParams.WRAP_CONTENT

        return LinearLayout.LayoutParams(width, height)
    }

    // Convert dp to px.
    protected fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // Open device browser.
    fun openDeviceBrowser(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    // Get iFrame for Facebook.
    private fun getIframeForFacebook(urlFacebook: String, isVideo: Boolean): String {

        // To show facebook links, we need to cover facebook post link.
        val url = "https://www.facebook.com/post.php?href=$urlFacebook&amp;show_text=true"

        return "<html><body><iframe src=\"$url&height=300\" width=\"100%\" height=\"300\" style=\"border:none;overflow:hidden\" scrolling=\"no\" frameborder=\"0\" allowfullscreen=\"true\" allow=\"autoplay; clipboard-write; encrypted-media; picture-in-picture; web-share\"></iframe></body></html>"
    }

    // Get iFrame for Twitter.
    private fun getIframeForTwitter(urlTwitter: String): String {
        return "<html><body><blockquote class=\"twitter-tweet\"><p lang=\"en\" dir=\"ltr\"><a href=\"$urlTwitter\"></a></blockquote><script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script></body></html>"
    }

    // Get iFrame for Instagram.
    private fun getIframeForInstagram(urlInstagram: String): String {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>%@</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        html, body, div {\n" +
                "            width: 100vw;\n" +
                "            height: 100%;\n" +
                "        }\n" +
                "        body {\n" +
                "            background-color: white;\n" +
                "        }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div>\n" +
                "      <div>\n" +
                "          <blockquote class=\"instagram-media\" data-instgrm-captioned data-instgrm-permalink=\"$urlInstagram\" data-instgrm-version=\"13\" style=\" background:#FFF; border:0; border-radius:3px; box-shadow:0 0 1px 0 rgba(0,0,0,0.5),0 1px 10px 0 rgba(0,0,0,0.15); margin: 1px; max-width:540px; min-width:326px; padding:0; width:99.375%; width:-webkit-calc(100% - 2px); width:calc(100% - 2px);\"><div style=\"padding:16px;\"> " +
                "           <a href=\"$urlInstagram\" style=\" background:#FFFFFF; line-height:0; padding:0 0; text-align:center; text-decoration:none; width:100%;\" target=\"_blank\"> <div style=\" display: flex; flex-direction: row; align-items: center;\"> <div style=\"background-color: #F4F4F4; border-radius: 50%; flex-grow: 0; height: 40px; margin-right: 14px; width: 40px;\"></div> <div style=\"display: flex; flex-direction: column; flex-grow: 1; justify-content: center;\"> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; margin-bottom: 6px; width: 100px;\"></div> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; width: 60px;\"></div></div></div><div style=\"padding: 19% 0;\"></div> <div style=\"display:block; height:50px; margin:0 auto 12px; width:50px;\"><svg width=\"50px\" height=\"50px\" viewBox=\"0 0 60 60\" version=\"1.1\" xmlns=\"https://www.w3.org/2000/svg\" xmlns:xlink=\"https://www.w3.org/1999/xlink\"><g stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\"><g transform=\"translate(-511.000000, -20.000000)\" fill=\"#000000\"><g><path d=\"M556.869,30.41 C554.814,30.41 553.148,32.076 553.148,34.131 C553.148,36.186 554.814,37.852 556.869,37.852 C558.924,37.852 560.59,36.186 560.59,34.131 C560.59,32.076 558.924,30.41 556.869,30.41 M541,60.657 C535.114,60.657 530.342,55.887 530.342,50 C530.342,44.114 535.114,39.342 541,39.342 C546.887,39.342 551.658,44.114 551.658,50 C551.658,55.887 546.887,60.657 541,60.657 M541,33.886 C532.1,33.886 524.886,41.1 524.886,50 C524.886,58.899 532.1,66.113 541,66.113 C549.9,66.113 557.115,58.899 557.115,50 C557.115,41.1 549.9,33.886 541,33.886 M565.378,62.101 C565.244,65.022 564.756,66.606 564.346,67.663 C563.803,69.06 563.154,70.057 562.106,71.106 C561.058,72.155 560.06,72.803 558.662,73.347 C557.607,73.757 556.021,74.244 553.102,74.378 C549.944,74.521 548.997,74.552 541,74.552 C533.003,74.552 532.056,74.521 528.898,74.378 C525.979,74.244 524.393,73.757 523.338,73.347 C521.94,72.803 520.942,72.155 519.894,71.106 C518.846,70.057 518.197,69.06 517.654,67.663 C517.244,66.606 516.755,65.022 516.623,62.101 C516.479,58.943 516.448,57.996 516.448,50 C516.448,42.003 516.479,41.056 516.623,37.899 C516.755,34.978 517.244,33.391 517.654,32.338 C518.197,30.938 518.846,29.942 519.894,28.894 C520.942,27.846 521.94,27.196 523.338,26.654 C524.393,26.244 525.979,25.756 528.898,25.623 C532.057,25.479 533.004,25.448 541,25.448 C548.997,25.448 549.943,25.479 553.102,25.623 C556.021,25.756 557.607,26.244 558.662,26.654 C560.06,27.196 561.058,27.846 562.106,28.894 C563.154,29.942 563.803,30.938 564.346,32.338 C564.756,33.391 565.244,34.978 565.378,37.899 C565.522,41.056 565.552,42.003 565.552,50 C565.552,57.996 565.522,58.943 565.378,62.101 M570.82,37.631 C570.674,34.438 570.167,32.258 569.425,30.349 C568.659,28.377 567.633,26.702 565.965,25.035 C564.297,23.368 562.623,22.342 560.652,21.575 C558.743,20.834 556.562,20.326 553.369,20.18 C550.169,20.033 549.148,20 541,20 C532.853,20 531.831,20.033 528.631,20.18 C525.438,20.326 523.257,20.834 521.349,21.575 C519.376,22.342 517.703,23.368 516.035,25.035 C514.368,26.702 513.342,28.377 512.574,30.349 C511.834,32.258 511.326,34.438 511.181,37.631 C511.035,40.831 511,41.851 511,50 C511,58.147 511.035,59.17 511.181,62.369 C511.326,65.562 511.834,67.743 512.574,69.651 C513.342,71.625 514.368,73.296 516.035,74.965 C517.703,76.634 519.376,77.658 521.349,78.425 C523.257,79.167 525.438,79.673 528.631,79.82 C531.831,79.965 532.853,80.001 541,80.001 C549.148,80.001 550.169,79.965 553.369,79.82 C556.562,79.673 558.743,79.167 560.652,78.425 C562.623,77.658 564.297,76.634 565.965,74.965 C567.633,73.296 568.659,71.625 569.425,69.651 C570.167,67.743 570.674,65.562 570.82,62.369 C570.966,59.17 571,58.147 571,50 C571,41.851 570.966,40.831 570.82,37.631\"></path></g></g></g></svg></div><div style=\"padding-top: 8px;\"> <div style=\" color:#3897f0; font-family:Arial,sans-serif; font-size:14px; font-style:normal; font-weight:550; line-height:18px;\"> View this post on Instagram</div></div><div style=\"padding: 12.5% 0;\"></div> <div style=\"display: flex; flex-direction: row; margin-bottom: 14px; align-items: center;\"><div> <div style=\"background-color: #F4F4F4; border-radius: 50%; height: 12.5px; width: 12.5px; transform: translateX(0px) translateY(7px);\"></div> <div style=\"background-color: #F4F4F4; height: 12.5px; transform: rotate(-45deg) translateX(3px) translateY(1px); width: 12.5px; flex-grow: 0; margin-right: 14px; margin-left: 2px;\"></div> <div style=\"background-color: #F4F4F4; border-radius: 50%; height: 12.5px; width: 12.5px; transform: translateX(9px) translateY(-18px);\"></div></div><div style=\"margin-left: 8px;\"> <div style=\" background-color: #F4F4F4; border-radius: 50%; flex-grow: 0; height: 20px; width: 20px;\"></div> <div style=\" width: 0; height: 0; border-top: 2px solid transparent; border-left: 6px solid #f4f4f4; border-bottom: 2px solid transparent; transform: translateX(16px) translateY(-4px) rotate(30deg)\"></div></div><div style=\"margin-left: auto;\"> <div style=\" width: 0px; border-top: 8px solid #F4F4F4; border-right: 8px solid transparent; transform: translateY(16px);\"></div> <div style=\" background-color: #F4F4F4; flex-grow: 0; height: 12px; width: 16px; transform: translateY(-4px);\"></div> <div style=\" width: 0; height: 0; border-top: 8px solid #F4F4F4; border-left: 8px solid transparent; transform: translateY(-4px) translateX(8px);\"></div></div></div> <div style=\"display: flex; flex-direction: column; flex-grow: 1; justify-content: center; margin-bottom: 24px;\"> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; margin-bottom: 6px; width: 224px;\"></div> <div style=\" background-color: #F4F4F4; border-radius: 4px; flex-grow: 0; height: 14px; width: 144px;\"></div></div></a><p style=\" color:#c9c8cd; font-family:Arial,sans-serif; font-size:14px; line-height:17px; margin-bottom:0; margin-top:8px; overflow:hidden; padding:8px 0 7px; text-align:center; text-overflow:ellipsis; white-space:nowrap;\">" +
                "           <a href=\"$urlInstagram\" style=\" color:#c9c8cd; font-family:Arial,sans-serif; font-size:14px; font-style:normal; font-weight:normal; line-height:17px; text-decoration:none;\" target=\"_blank\"></a></p></div></blockquote> <script async src=\"https://www.instagram.com/embed.js\"></script>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n"
    }


    // Get iFrame for SoundCloud.
    // Demo link: "https://api.soundcloud.com/tracks/229414725"
    private fun getIframeForSoundCloud(urlSoundCloud: String): String {
        return "<html><body><iframe src=\"https://w.soundcloud.com/player/?url=$urlSoundCloud\" " +
                "width=\"100%\" height=\"200\" frameborder=\"0\" allowtransparency=\"true\" " +
                "allow=\"encrypted-media\"></iframe></body></html>"
    }

    // Only for test and find bug when loading ads.
    private fun showAdsLoadingErrors(e: LoadAdError) {
        val domain = e.domain
        val code = e.code
        val message = e.message
        val info = e.responseInfo
        val cause = e.cause

        Log.e(
            "key",
            "\n Domain: $domain \n Code: $code \n Message: $message \n Info: $info \n Cause: $cause"
        )
    }
}