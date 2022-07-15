package com.journalmetro.app.ui.home.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdSize
import com.journalmetro.app.R
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.util.*
import com.journalmetro.app.databinding.FragmentHomeMainBinding
import com.journalmetro.app.databinding.LayoutHomeMainBinding
import com.journalmetro.app.databinding.LayoutTopBarHomeBinding
import com.journalmetro.app.homeSection.model.HomePost
import com.journalmetro.app.homeSection.model.HomeSection
import com.journalmetro.app.post.convertPost
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.section.model.Section
import com.journalmetro.app.ui.common.adapter.ItemSelectionCallback
import com.journalmetro.app.ui.common.adapter.OmniAdapter
import com.journalmetro.app.ui.common.adapter.ViewTypeHolder
import com.journalmetro.app.ui.common.fragment.*
import com.journalmetro.app.ui.details.article.ArticleDetailFragment
import kotlinx.coroutines.launch

/**
 * Created by App Developer on July/2021.
 */
class HomeMainFragment : BaseFragment<FragmentHomeMainBinding, HomeMainViewModel>() {

    // Understand saved or unsaved posts from detail page.
    private var recyclerViewAdapters: MutableList<OmniAdapter> = ArrayList()
    private var recyclerViewAdapterPostLists: MutableList<List<HomePost>> = ArrayList()

    // *****
    // Var_Override.
    override var viewModelBindingVariable: Int = BR.viewModel
    override var layoutResId: Int = R.layout.fragment_home_main

    // *****
    // Var_Private.
    private lateinit var topBarBinding: LayoutTopBarHomeBinding // Top bar.
    private lateinit var mainLinearLayout: LayoutHomeMainBinding
    private var coreList: MutableList<HomeSection> = ArrayList() // Keep last response.

    // *****
    // Fun_Override.
    override fun provideViewModel(): HomeMainViewModel {
        return ViewModelProvider(this, viewModelFactory).get(HomeMainViewModel::class.java)
    }

    override fun setDataBindingVariables(binding: ViewDataBinding) {
        super.setDataBindingVariables(binding)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set top bar binding for buttons.
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        topBarBinding = LayoutTopBarHomeBinding.inflate(inflater)
        mainLinearLayout = LayoutHomeMainBinding.inflate(inflater)

        fetchHomeSectionList() // Get section list.
        setupObservers() // Define observers.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        homeAsUpEnabled(false)
        showActionBar()
        showBottomNavigationView()
        actionBarCustom(getActionBar()) //custom top bar
        setupListenerMessageLoadingError { onClickLoadingErrorButton() } // Setup loading error message.
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI() // Define UI.
        setupListeners() // Define listeners.
    }

    // *****
    // Fun_Private.
    private fun setupObservers() {
        setupObserverHomeSectionList()
    }

    private fun setupListeners() {
        setupListenerSwipeList()
    }

    private fun setupUI() {
        setupSwipeRefreshUI()
        setupMainContainerUI(mainLinearLayout.root)
    }

    // Get Action Bar.
    private fun getActionBar(): View {
        topBarBinding.searchButton.setOnClickListener { openSearchFragment() }
        return topBarBinding.root
    }

    // Swipe refresh progress bar colors.
    private fun setupSwipeRefreshUI() {
        layoutBinding.componentSwipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.colorAccent))

        layoutBinding.componentSwipeRefresh.setColorSchemeColors(Color.WHITE)
    }

    // Swipe listener.
    private fun setupListenerSwipeList() {
        layoutBinding.componentSwipeRefresh.setOnRefreshListener {
            // If no resource is currently in progress.
            if(!viewModel.isStillLoading) fetchHomeSectionList() // Get data.
            else completeSwipeRefresh()
        }
    }

    // Get section list.
    @SuppressLint("FragmentLiveDataObserve")
    private fun fetchHomeSectionList() {
        callProcessBar(true) // Show loading bar.

        viewModel.fetchHomeSectionList().observe(this, {
            fetchHomeSectionListResult(it)
        })
    }

    // Manage fetch data result.
    private fun fetchHomeSectionListResult(response: Resource<List<HomeSection>>) {
        when (response.status) {
            Resource.Status.SUCCESS -> {
                fetchHomeSectionListResultSuccess(response)
            }
            Resource.Status.LOADING -> {
                fetchHomeSectionListResultLoading()
            }
            Resource.Status.ERROR -> {
                fetchHomeSectionListResultError(response)
            }
        }
    }

    // Handle result success.
    private fun fetchHomeSectionListResultSuccess(response: Resource<List<HomeSection>>) {
        completeSwipeRefresh() // Disappear swipe refresh progress bar.
        callProcessBar(false)

        // Check response is new data or old?
        if (isResponseDataNew(response.data)) {
            setCoreList(response.data) // Keep response.
            viewModel._homeSectionList.postValue(response.data)
        }
    }

    // Handle result loading.
    private fun fetchHomeSectionListResultLoading() {
        callProcessBar(true)
    }

    // Handle result error.
    private fun fetchHomeSectionListResultError(response: Resource<List<HomeSection>>) {
        callProcessBar(false)
        callGeneralLoadingErrorMessageShow() // General loading error.
    }

    private fun setupObserverHomeSectionList() {
        viewModel.homeSectionList.observe(this, {
            clearUI() // Clear UI.
            setupUI() // Setup UI.
            createLayout(it) // Add views on UI.
        })
    }

    // This is fundamental UI function. Be careful when edit it.
    // Create all page UI.
    private fun createLayout(homeSectionList: List<HomeSection>) {

        // Travel in section list and create UI by related taxonomy.
        for (section in homeSectionList){

            val taxonomy = section.taxonomy // UI is creating by taxonomy type.

            when {
                taxonomy.isTaxonomyOfCard() -> createLayoutCard(section)
                taxonomy.isTaxonomyOfListGroup() -> createLayoutListGroup(section)
                taxonomy.isTaxonomyOfAdvertisement() -> createLayoutAdvertisement()
                taxonomy.isTaxonomyOfQuote() -> createLayoutCitation(section)
                taxonomy.isTaxonomyOfCarousel() -> createLayoutCarousel(section)
            }
        }
    }

    // This is fundamental UI function. Be careful when edit it.
    // According to related taxonomy, create UI as Card.
    private fun createLayoutCard(section: HomeSection) {
        val postList = getPostListFromPageSectionArray(section)
        // Create list in home page.
        if (postList.isNotEmpty()) {
            // Add item to card
            setCard(postList)
        }
    }

    private fun setCard(posts: List<HomePost>) {
        var cardView: CardView = getCardView(posts)
        cardView.setOnClickListener {
            val postData : HomePost = posts[0]
            openArticleDetailFragment(postData.convertPost()) // Show open post detail.
        }
        //Add to parent layout
        addViewOnPageLayout(cardView)
    }

    //Get Card View layout programmatically
    private fun getCardView(posts: List<HomePost>): CardView {
        return getProgrammaticallyCardView(posts)
    }

    // This is fundamental UI function. Be careful when edit it.
    // According to related taxonomy, create UI as Category Block List.
    private fun createLayoutListGroup(homeSection: HomeSection) {

        // Post list for recycler view.
        val postList = getPostListFromPageSectionArray(homeSection)

        // Create list in home page.
        if (postList.isNotEmpty()) {

            var isExplorez = false

            // Add title. First check category is not null.
            if (homeSection.category != null) {

                val subTitle = getSubtitle(homeSection) // Get subtitle to show near of title.

                setListTitleWithArrow(
                    homeSection.category.name!!,
                    subTitle,
                    homeSection.category.id.getSafeLong())

                if (homeSection.category.id.isIdEqualExplorezId()) {
                    isExplorez = true
                    ArticleDetailFragment.explorerList = postList
                }
            }

            // Add list.
            setList(postList, true, isExplorez)
        }
    }

    // Get subtitle to put near of category title.
    private fun getSubtitle(homeSection: HomeSection): String {

        // Use specific subtitle just for Explorez category.
        return if (homeSection.category!!.id.isIdEqualExplorezId()) {
            getString(R.string.subtitle_explore_list)
        }
        else homeSection.subtitle.getSafeSting() // Normal categories.
    }

    // This is fundamental UI function. Be careful when edit it.
    // According to related taxonomy, create UI as Advertisement.
    private fun createLayoutAdvertisement() {
        if (isAdsActive()) { // Ads flag.

            // Get view group with grey background.
            val linearGrey = getProgrammaticallyGreyBackgroundLayout(
                marginBottom = 10,
                marginTop = 10,
                paddingLeft = 20,
                paddingRight = 20,
                paddingTop = 20,
                paddingBottom = 20
            )

            // Define ad view.
            val ads = getProgrammaticallyAdsView(
                AdSize.MEDIUM_RECTANGLE,
                getGeneralAdsUnitId()
            )
            linearGrey.addView(ads) // Add ads view inside grey view group.
            addViewOnPageLayout(linearGrey)
        }
    }

    // This is fundamental UI function. Be careful when edit it.
    // According to related taxonomy, create UI as Citation.
    private fun createLayoutCitation(homeSection: HomeSection) {

        if (!homeSection.pageSectionItems.isNullOrEmpty()) {

            val quote = homeSection.pageSectionItems[0].quote

            if (quote != null) {
                val quoteView = getProgrammaticallyQuote(
                    getCitationTitle(quote.title.getSafeSting()),
                    quote.content.getSafeSting(),
                    quote.author.getSafeSting(),
                    quote.authorRole.getSafeSting()
                )

                // Show open post detail with click if post does not null.
                val post = homeSection.pageSectionItems[0].post
                if (post != null) quoteView.setOnClickListener { openArticleDetailFragment(post.convertPost()) }

                addViewOnPageLayout(quoteView)
            }
        }
    }

    private fun getCitationTitle(title: String): String {
        return if (title.isBlank()) "CITATION DU JOUR" else title
    }

    // This is fundamental UI function. Be careful when edit it.
    // According to related taxonomy, create UI as Carousel.
    private fun createLayoutCarousel(homeSection: HomeSection) {

        // Post list for recycler view.
        val postList = getPostListFromPageSectionArray(homeSection)

        // Create list in home page.
        if (postList.isNotEmpty()) {

            // Add title. First check category is not null.
            if (homeSection.category != null) setListTitleNormal(homeSection.category.name!!)
            else setListTitleNormal("À ne pas manquer") // Todo: Delete it when API fixed.

            // Add list.
            setList(postList, false)
        }

        // Add Carousel bottom line.
        addViewOnPageLayout(getProgrammaticallyViewLine(marginTop = 15, marginLeft = 20, marginRight = 20))
    }


    // Get post list from PageSectionItems array.
    private fun getPostListFromPageSectionArray(homeSection: HomeSection): List<HomePost> {
        val postList: MutableList<HomePost> = ArrayList()

        // If list is null or empty, return empty list.
        if (homeSection.pageSectionItems.isNullOrEmpty()) return listOf()

        // Get post list from section item.
        for (item in homeSection.pageSectionItems) {

            // Add section (pageSectionItem) taxonomy in post taxonomy.
            val post = item.post?.copy(
                taxonomy = item.taxonomy.getSafeSting(), // We can create different UI for each type post.
                order = (item.order.getSafeLong().toInt() + 1).toString().plus(".") // Item number for horizontal list.
            )

            // We are checking original model. If original model does not null, we can add copy model.
            // Because copy model is creating by original model.
            if (item.post != null) postList.add(post!!) // Add post in list.
        }
        return postList
    }

    // Add view on page.
    private fun addViewOnPageLayout(view: View) {
        mainLinearLayout.mainList.addView(view)
    }

    // Add view on parent container.
    private fun setupMainContainerUI(view: View) {

        // Clear parent of list.
        if (view.parent != null) (view.parent as ViewGroup).removeView(view)

        // Update post save status if they saved/unsaved from detail page.
        if (isNotFirstCreateOfUI()) checkSaveStatusOfPosts()

        layoutBinding.container.addView(view)
    }

    // Clear UI to refresh.
    private fun clearUI() {
        mainLinearLayout.mainList.removeAllViewsInLayout()
    }

    // Get recycler view to show list.
    private fun getRecyclerView(posts: List<HomePost>, isVertical: Boolean): RecyclerView {
        val marginRight = if (isVertical) 20 else 0
        val adapter = OmniAdapter()
        val recyclerView = getProgrammaticallyRecyclerView(isVertical, 20, marginRight)

        recyclerView.adapter = adapter

        // This is important for database check.
        lifecycleScope.launch { adapter.updateList(createList(posts)) }

        // This is important for post save status with come back from post detail page.
        keepAdaptersWithList(adapter, posts)

        return recyclerView
    }

    // Understand saved or unsaved posts from detail page.
    private fun keepAdaptersWithList(adapter: OmniAdapter, list: List<HomePost>) {
        recyclerViewAdapters.add(adapter)
        recyclerViewAdapterPostLists.add(list)
    }

    // Check post list when come back from detail page.
    private fun checkSaveStatusOfPosts() {
        for ((index, adapter) in recyclerViewAdapters.withIndex())
            lifecycleScope.launch { adapter.updateList(createList(recyclerViewAdapterPostLists[index])) }
    }

    // Create post list for adapter.
    private suspend fun createList(posts: List<HomePost>): List<ViewTypeHolder<HomePost, ItemSelectionCallback<HomePost, View>>> {
        return posts.map {
            createItemViewTypeHolder(viewModel.getReadablePost(it), it.taxonomy.isTaxonomyOfListGroupPhotoItem())
        }
    }

    // Create item view for list.
    private fun createItemViewTypeHolder(homePost: HomePost, isBigImageItem: Boolean): ViewTypeHolder<HomePost, ItemSelectionCallback<HomePost, View>> {
        return ViewTypeHolder(
            viewData = homePost,
            isBigImageItem = isBigImageItem,
            layoutResId = getItemViewTypeHolderLayoutId(homePost.taxonomy),
            callback = object : ItemSelectionCallback<HomePost, View> {
                override fun onItemSelected(item: HomePost, view: View) {

                    if (isClickSaveButton(view)) clickSaveButton(item, view) // Save button action.
                    else openArticleDetailFragment(item.convertPost()) // Show open post detail.
                }
            }
        )
    }

    // Get different item UI according to taxonomy.
    private fun getItemViewTypeHolderLayoutId(taxonomy: String): Int {
        return when {
            taxonomy.isTaxonomyOfListGroupItem() -> R.layout.item_post_home_main_no_image
            taxonomy.isTaxonomyOfCarouselItem() -> R.layout.item_post_home_main_carousel
            else -> R.layout.item_post_home_main
        }
    }

    // Create list.
    private fun setList(posts: List<HomePost>, isVertical: Boolean, isExplorezList: Boolean = false) {
        val recyclerView = getRecyclerView(posts, isVertical)

        if (isExplorezList){
            // Add list to grey layout and the add to page layout.
            val linearGrey = getProgrammaticallyGreyBackgroundLayout()
            linearGrey.addView(recyclerView)
            addViewOnPageLayout(linearGrey)
        } else {
            // Add list.
            addViewOnPageLayout(recyclerView)
        }
    }

    // Create title of list with Arrow.
    private fun setListTitleWithArrow(title: String, subTitle: String, categoryId: Long) {
        val view = getProgrammaticallyLayoutTitleWithArrow(title, subTitle, 34f, getFontAcuminProCondBlack())

        // Set click listener on title. 0L is mean category object does not have id value.
        if (categoryId != 0L) {
            view.setOnClickListener {
                clickTitleOfVerticalSection(createSectionForClickedCategory(categoryId, title))
            }
        }

        //Explorez list title with grey background
        if (categoryId == 418581780L){
            val linearGrey = getProgrammaticallyGreyBackgroundLayout(marginTop = 10, marginBottom = 0, paddingBottom = 10)
            linearGrey.addView(view)
            addViewOnPageLayout(linearGrey)
            // Add Title line.
            linearGrey.addView(getProgrammaticallyViewLine(5, marginTop = 10, marginLeft = 20, marginRight = 20))
        }else{
            // Add Title.
            addViewOnPageLayout(view)
            addViewOnPageLayout(getProgrammaticallyViewLine(5, marginTop = 10, marginLeft = 20, marginRight = 20))
        }
    }

    // Create title of list.
    private fun setListTitleNormal(text: String) {

        val view = getProgrammaticallyTextViewTitle(text, 30f, getFontAcuminProCondBlack(), marginLeft = 20)

        // Add Title.
        addViewOnPageLayout(view)

        // Add Title line.
        addViewOnPageLayout(getProgrammaticallyViewLine(5, marginTop = 10, marginLeft = 20, marginRight = 20))
    }

    // Navigate detail page with argument which is selected post.
    private fun openArticleDetailFragment(post: Post) {
        if (post.type.isTaxonomyOfVideo()) {
            // Open video detail.
            navigateTo(HomeMainFragmentDirections.actionHomeMainFragmentToVideoDetailFragment(post))
        } else {
            // Open article detail.
            navigateTo(HomeMainFragmentDirections.actionHomeMainFragmentToArticleDetailFragment(post))
        }
    }

    // Understand view created before or not.
    private fun isNotFirstCreateOfUI(): Boolean {
        return mainLinearLayout.mainList.childCount > 0
    }

    // Click list item save button.
    private fun clickSaveButton(item: HomePost, view: View) {
        actionSaveButton(item)
        changeSaveButtonUI(item.isSavedByUser, view as ImageView)
    }

    // Define Action of Save button on Action Bar.
    private fun actionSaveButton(item: HomePost){
        when(item.isSavedByUser){
            true -> lifecycleScope.launch {  viewModel.databaseUnSavePost(item) }
            false -> lifecycleScope.launch { viewModel.databaseSavePost(item) }
        }
    }

    // Define UI of Save button on Action Bar.
    private fun changeSaveButtonUI(isSaved: Boolean, imageView: ImageView) {
        when(isSaved){
            true -> imageView.setImageResource(R.drawable.ic_save_selected)
            false -> imageView.setImageResource(R.drawable.ic_save_unselected_green)
        }
    }

    // Click title of vertical list.
    private fun clickTitleOfVerticalSection(clickedSection: Section) {
        fetchSectionList(clickedSection)
    }

    // Open list page of selected category/section.
    private fun openSectionNewsListFragment(parent: Section, childId: Long) {
        navigateTo(HomeMainFragmentDirections.actionHomeMainFragmentToSectionNewsListFragment(parent, childId))
    }

    // Navigate search page.
    private fun openSearchFragment() {
        navigateTo(HomeMainFragmentDirections.actionHomeMainFragmentToSearchFragment())
    }

    // Loading error action.
    private fun onClickLoadingErrorButton() {
        fetchHomeSectionList() // Get data.
    }

    // Disappear progress bar of swipe refresh.
    private fun completeSwipeRefresh() {
        layoutBinding.componentSwipeRefresh.isRefreshing = false
    }

    // Get sections.
    private fun fetchSectionList (clickedSection: Section) {
        viewModel.fetchSectionList().observe(viewLifecycleOwner, {
            fetchSectionListResult(it, clickedSection)
        })
    }

    // Manage action for fetch result.
    private fun fetchSectionListResult(response: Resource<List<Section>>, clickedSection: Section) {
        when (response.status) {
            Resource.Status.SUCCESS -> fetchSectionListResultSuccess(response, clickedSection)
            Resource.Status.LOADING -> { /* Does not function for loading phase. */ }
            Resource.Status.ERROR -> fetchSectionListResultError()
        }
    }

    // Pass value on ViewModel.
    private fun fetchSectionListResultSuccess(list: Resource<List<Section>>, clickedSection: Section) {
        postValueForSectionList(list.data, clickedSection)
    }

    // Show toast message.
    private fun fetchSectionListResultError() {
        callGeneralLoadingErrorMessageShow() // General loading error.
    }

    // Send data for section main item list.
    private fun postValueForSectionList(sectionList: List<Section>?, clickedSection: Section) {

        var selectedSection: Section? = null // Selected category.

        if (!sectionList.isNullOrEmpty()) {

            // Find selected category / section from api response.
            for (section in sectionList) if (clickedSection.id == section.id) selectedSection = section
        }

        // If not found selected category in api response, send selected category as custom object.
        if (selectedSection == null) selectedSection = clickedSection

        // If app finds selected category in response, open page.
        openSectionNewsListFragment(selectedSection, clickedSection.id)
    }

    // Keep response. We do not want to update again and again UI. Thus, we check old and new data.
    private fun setCoreList(list: List<HomeSection>?) {
        if (!list.isNullOrEmpty()) coreList.addAll(list)
    }

    // Get kept response.
    private fun getCoreList(): List<HomeSection> {
        return coreList
    }

    // Check new response with old one.
    private fun isResponseDataNew(list: List<HomeSection>?): Boolean {
        return if (list.isNullOrEmpty()) false
        else getCoreList() != list
    }

    // Get custom Section object.
    private fun createSectionForClickedCategory(id: Long, title: String): Section {
        return Section(title, "","","","", listOf(), id)
    }

    // Call and manage visibility of loading bar.
    private fun callProcessBar(isLoading: Boolean) {
        viewModel.isLoading.postValue(isLoading)
    }
}