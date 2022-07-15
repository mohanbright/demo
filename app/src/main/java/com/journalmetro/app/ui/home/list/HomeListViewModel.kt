package com.journalmetro.app.ui.home.list

import androidx.lifecycle.*
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.util.getReadableDateFR
import com.journalmetro.app.post.convertEntityPost
import com.journalmetro.app.post.entity.EntityPost
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.post.repository.RepositoryPost
import com.journalmetro.app.postList.model.PostList
import com.journalmetro.app.postList.repository.PostListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeListViewModel @Inject constructor(
    private val postListRepository: PostListRepository, // Api.
    private val repositoryPost: RepositoryPost // Database.
) :
    ViewModel() {

    var isLoading = MutableLiveData(true)
    val isStillLoading get() = this.isLoading.value ?: false

    var showSaveUI: Boolean = true
        private set

    // Infinite scroll.
    var _postList = MutableLiveData<PostList>()
    val postList: LiveData<PostList> get() = _postList

    // Swipe refresh.
    var _postListRefresh = MutableLiveData<PostList>()
    val postListRefresh: LiveData<PostList> get() = _postListRefresh

    // Get post list.
    fun fetchPostList(
        maxResultCount: Int,
        skipCount: Int,
        categoryId: String,
        isAdsActive: Boolean
    ): LiveData<Resource<PostList>> {
        return postListRepository.fetchPostList("", maxResultCount, skipCount, categoryId, isIncludeAds = isAdsActive)
    }

    // Edit post info.
    suspend fun getReadablePost(post: Post): Post {

        // Set readable data.
        post.isSavedByUser = isSavedBefore(post.id)

        // This is important for Entity Post.
        post.dateReadable = post.updatedAt.getReadableDateFR()

        return post
    }

    // Save item in database.
    suspend fun databaseSavePost(post: Post) {
        post.isSavedByUser = true
        insertPost(post.convertEntityPost())
    }

    // Delete item in database.
    suspend fun databaseUnSavePost(post: Post) {
        post.isSavedByUser = false
        deletePost(post.convertEntityPost())
    }

    // *****
    // Database jobs.

    // Database insert item.
    private suspend fun insertPost(entityPost: EntityPost) {
        repositoryPost.setPost(entityPost)
    }

    // Database delete item.
    private suspend fun deletePost(entityPost: EntityPost) {
        repositoryPost.deletePost(entityPost)
    }

    // Database check item.
    private suspend fun isSavedBefore(id: Long): Boolean {
        return repositoryPost.hasPost(id)
    }
}