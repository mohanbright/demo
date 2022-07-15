package com.journalmetro.app.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.homeSection.model.HomePost
import com.journalmetro.app.homeSection.model.HomeSection
import com.journalmetro.app.homeSection.repository.HomeSectionRepository
import com.journalmetro.app.post.convertEntityPost
import com.journalmetro.app.post.entity.EntityPost
import com.journalmetro.app.post.repository.RepositoryPost
import com.journalmetro.app.section.model.Section
import com.journalmetro.app.sectionList.repository.SectionListRepository
import javax.inject.Inject

/**
 * Created by App Developer on July/2021.
 */
class HomeMainViewModel @Inject constructor(
    private val homeSectionRepository: HomeSectionRepository, // Api.
    private val sectionListRepository: SectionListRepository, // Api.
    private val repositoryPost: RepositoryPost // Database.
) :
    ViewModel() {

    var isLoading = MutableLiveData(true)
    val isStillLoading get() = this.isLoading.value ?: false

    // Home section list.
    var _homeSectionList = MutableLiveData<List<HomeSection>>()
    val homeSectionList: LiveData<List<HomeSection>> get() = _homeSectionList

    // Get home section list.
    fun fetchHomeSectionList(): LiveData<Resource<List<HomeSection>>> {
        return homeSectionRepository.fetchHomeSectionList()
    }

    // Edit post info.
    suspend fun getReadablePost(post: HomePost): HomePost {

        // Set readable data.
        post.isSavedByUser = isSavedBefore(post.id)

        // This is important for Entity Post.
        //post.dateReadable = post.createdAt.getReadableDateFR()

        return post
    }

    // Get section list.
    fun fetchSectionList(): LiveData<Resource<List<Section>>> {
        return sectionListRepository.fetchSectionList()
    }

    // Save item in database.
    suspend fun databaseSavePost(post: HomePost) {
        post.isSavedByUser = true
        insertPost(post.convertEntityPost())
    }

    // Delete item in database.
    suspend fun databaseUnSavePost(post: HomePost) {
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