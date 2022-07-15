package com.journalmetro.app.ui.home.list

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.journalmetro.app.R
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.databinding.FragmentHomeListBinding
import com.journalmetro.app.databinding.LayoutTopBarHomeListBinding
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.postList.model.PostList
import com.journalmetro.app.ui.common.adapter.ItemSelectionCallback
import com.journalmetro.app.ui.common.adapter.OmniAdapter
import com.journalmetro.app.ui.common.adapter.ViewTypeHolder
import com.journalmetro.app.ui.common.fragment.*
import com.journalmetro.app.ui.common.view.addInfiniteScrollListener
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class HomeListFragment : BaseFragment<FragmentHomeListBinding, HomeListViewModel>() {

    // *****
    // Var_Override.
    override var viewModelBindingVariable: Int = BR.viewModel
    override var layoutResId: Int = R.layout.fragment_home_list

    // *****
    // Var_Private.
    private lateinit var topBarBinding: LayoutTopBarHomeListBinding // Top bar.
    private var selectedCategoryId by Delegates.notNull<String>()
    private var selectedCategoryName by Delegates.notNull<String>()
    private var firstItemId by Delegates.notNull<Long>()
    private var isFirstItemCreated = false

    // Keep last request for loading error message action.
    private var lastRequestMaxResultCount: Int = 0
    private var lastRequestSkipCount: Int = 0
    private var lastRequestIsRefresh: Boolean = false
    private var lastRequestIsSearch: Boolean = false
    private var lastRequestIsOnCreate: Boolean = false

    // *****
    // Val_Private.
    private val adapter by lazy { OmniAdapter() }
    private val postListIncrementSize = 24

    // *****
    // Fun_Override.
    override fun provideViewModel(): HomeListViewModel {
        return ViewModelProvider(this, viewModelFactory).get(HomeListViewModel::class.java)
    }

    override fun setDataBindingVariables(binding: ViewDataBinding) {
        super.setDataBindingVariables(binding)
        layoutBinding.setVariable(BR.adapter, adapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set top bar binding for buttons.
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        topBarBinding = LayoutTopBarHomeListBinding.inflate(inflater)

        // Get arguments to pass data from other fragments.
        val args: HomeListFragmentArgs by navArgs()
        selectedCategoryId = args.selectedCategoryId.toString() // Fetch category posts.
        selectedCategoryName = args.selectedCategoryName // Show category title.

        // Show progress because category base fetching can take time a little.
        callLoadingProgressBar(true)

        fetchPostList(postListIncrementSize, 0, isOnCreate = true) // Get posts with init.
        setupObservers() // Define observers.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeAsUpEnabled(false)
        showActionBar()
        showBottomNavigationView()
        actionBarCustom(getActionBar(selectedCategoryName)) //custom top bar
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
    private fun setupListeners() {
        setupListenerSwipeList()
        setupListenerPostListScroll()
    }

    private fun setupObservers() {
        setupObserverPostList()
        setupObserverPostRefreshedList()
    }

    private fun setupUI() {
        setupSwipeRefreshUI()
    }

    // Get Action Bar.
    private fun getActionBar(title: String): View {
        topBarBinding.topBarTitle.text = title
        topBarBinding.topBarButtonSearch.setOnClickListener { openSearchFragment() }
        return topBarBinding.root
    }

    // Swipe refresh progress bar colors.
    private fun setupSwipeRefreshUI() {
        layoutBinding.listPosts.componentSwipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.colorAccent))

        layoutBinding.listPosts.componentSwipeRefresh.setColorSchemeColors(Color.WHITE)
    }

    // Observe posts for infinite scroll.
    private fun setupObserverPostList() {
        viewModel.postList.observe(this, {
            it.let { response ->
                lifecycleScope.launch {
                    adapter.addNewItemsOnList(createList(response.items))
                }
            }
        })
    }

    // Observe posts for swipe refresh.
    private fun setupObserverPostRefreshedList() {
        viewModel.postListRefresh.observe(this, {
            it.let { response ->
                lifecycleScope.launch {
                    adapter.updateList(createList(response.items))
                }
            }
        })
    }

    // Swipe listener.
    private fun setupListenerSwipeList() {
        layoutBinding.listPosts.componentSwipeRefresh.setOnRefreshListener {
            // If no resource is currently in progress.
            if(!viewModel.isStillLoading){
                // Get refreshed data.
                fetchPostList(postListIncrementSize, 0, true)
            }
            else{
                completeSwipeRefresh()
            }
        }
    }

    // Scroll listener.
    private fun setupListenerPostListScroll() {
        layoutBinding.listPosts.componentList.addInfiniteScrollListener(
            scrolling = { currentListSize: Int ->  postListScrollUpAction(currentListSize) },
            comeEnd = { }
        )
    }

    // Infinite scroll action.
    private fun postListScrollUpAction(listSize: Int) {
        // Avoid redundant calls.
        if (!viewModel.isStillLoading) {
            fetchPostList(postListIncrementSize, listSize)
        }
    }

    // Get posts.
    @SuppressLint("FragmentLiveDataObserve")
    private fun fetchPostList(
        maxResultCount: Int,
        skipCount: Int,
        isRefresh: Boolean = false,
        isSearch: Boolean = false,
        isOnCreate: Boolean = false,
    ) {
        // Keep last request data.
        setLastRequestPost(maxResultCount, skipCount, isRefresh, isSearch, isOnCreate)

        // This is normal working type.
        // Category id defined by HomeMainFragment. It cannot be change as parameter.
        viewModel.fetchPostList(maxResultCount, skipCount, selectedCategoryId, isAdsActive()).observe(this, {
            fetchPostListResult(it, isRefresh || isSearch, isOnCreate)
        })
    }

    // Manage action for fetch result.
    private fun fetchPostListResult(response: Resource<PostList>, isRefresh: Boolean, isOnCreate: Boolean) {
        when (response.status) {
            Resource.Status.SUCCESS -> {
                fetchPostListResultSuccess(response, isRefresh, isOnCreate)
            }
            Resource.Status.LOADING -> {
                fetchPostListResultLoading()
            }
            Resource.Status.ERROR -> {
                fetchPostListResultError(response)
            }
        }
    }

    /**
     * Pass value on ViewModel.
     * isRefresh: true is SwipeRefreshLayout, false is infinite scroll of RecyclerView.
     */
    private fun fetchPostListResultSuccess(list: Resource<PostList>, isRefresh: Boolean, isOnCreate: Boolean) {

        if (isRefresh || isOnCreate) {
            // Reset keeping first item id.
            resetFirstItemId()

            // For refresh list.
            postValueForPostRefreshList(list.data!!)
            completeSwipeRefresh()
        }
        else {
            // Infinite scroll.
            postValueForPostList(list.data!!)
        }

        // Close loading bar.
        callLoadingProgressBar(false)
    }

    // Handle result loading.
    private fun fetchPostListResultLoading() {
        callLoadingProgressBar(true)
    }

    // Handle result error.
    private fun fetchPostListResultError(list: Resource<PostList>) {
        //hide loading or swipe refresh loading if there
        callLoadingProgressBar(false)
        completeSwipeRefresh()
        callGeneralLoadingErrorMessageShow() // General loading error.
    }

    // Send data for post list.
    private fun postValueForPostList(postList: PostList) {
        viewModel._postList.postValue(postList)
    }

    // Send data for refresh post list.
    private fun postValueForPostRefreshList(postList: PostList) {
        viewModel._postListRefresh.postValue(postList)
    }

    private suspend fun createList(
        posts: List<Post>
    ): List<ViewTypeHolder<Post, ItemSelectionCallback<Post, View>>> {
        return posts.map {
            val index = posts.indexOf(it)
            createItemViewTypeHolder(viewModel.getReadablePost(it), isFirstItem(index, it.id))
        }
    }

    private fun createItemViewTypeHolder(post: Post, isFirstItem: Boolean): ViewTypeHolder<Post, ItemSelectionCallback<Post, View>> {
        return ViewTypeHolder(
            viewData = post.copy(dateTimeReadable = ""),
            isBigImageItem = isFirstItem,
            layoutResId = R.layout.item_post,
            callback = object : ItemSelectionCallback<Post, View> {
                override fun onItemSelected(item: Post, view: View) {
                    if (isClickSaveButton(view)) clickSaveButton(item, view) // Save button action.
                    else openArticleDetailFragment(item) // Show open post detail.
                }
            }
        )
    }

    // Understand first item to define specific UI for it.
    private fun isFirstItem(i: Int, id: Long): Boolean{
        if(i == 0 && !isFirstItemCreated) {
            firstItemId = id
            isFirstItemCreated = true
        }
        return firstItemId == id
    }

    // Reset keeping first item id for swipe refresh.
    private fun resetFirstItemId(){
        isFirstItemCreated = false
    }

    // Click list item save button.
    private fun clickSaveButton(item: Post, view: View) {
        actionSaveButton(item)
        changeSaveButtonUI(item.isSavedByUser, view as ImageView)
    }

    // Define Action of Save button on Action Bar.
    private fun actionSaveButton(item: Post){
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

    // Manage progress bar.
    private fun callLoadingProgressBar(isLoading: Boolean) {
        viewModel.isLoading.postValue(isLoading)
    }

    // Disappear progress bar of swipe refresh.
    private fun completeSwipeRefresh() {
        layoutBinding.listPosts.componentSwipeRefresh.isRefreshing = false
    }

    // Navigate detail page with argument which is selected post.
    private fun openArticleDetailFragment(post: Post) {
        navigateTo(HomeListFragmentDirections.actionHomeListFragmentToArticleDetailFragment(post))
    }

    // Navigate search page.
    private fun openSearchFragment() {
        navigateTo(HomeListFragmentDirections.actionHomeListFragmentToSearchFragment())
    }

    // Loading error action.
    private fun onClickLoadingErrorButton() {
        fetchPostList(
            lastRequestMaxResultCount,
            lastRequestSkipCount,
            lastRequestIsRefresh,
            lastRequestIsSearch,
            lastRequestIsOnCreate
        )
    }

    // Set last request.
    private fun setLastRequestPost(
        maxResultCount: Int, skipCount: Int, isRefresh: Boolean, isSearch: Boolean, isOnCreate: Boolean
    ) {
        lastRequestMaxResultCount = maxResultCount
        lastRequestSkipCount = skipCount
        lastRequestIsRefresh = isRefresh
        lastRequestIsSearch = isSearch
        lastRequestIsOnCreate = isOnCreate
    }
}