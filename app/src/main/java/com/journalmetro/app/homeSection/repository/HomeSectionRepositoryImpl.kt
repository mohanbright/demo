package com.journalmetro.app.homeSection.repository

import androidx.lifecycle.LiveData
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.data.resultLiveData
import com.journalmetro.app.common.repository.BaseRepository
import com.journalmetro.app.homeSection.model.HomeSection
import com.journalmetro.app.homeSection.source.HomeSectionRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by App Developer on July/2021.
 */
@Singleton
class HomeSectionRepositoryImpl @Inject constructor(
    override val dataSource: HomeSectionRemoteDataSource
) :
    BaseRepository<HomeSection>(), HomeSectionRepository {

    override fun fetchHomeSectionList(): LiveData<Resource<List<HomeSection>>> {
        return resultLiveData(
            networkCall = { dataSource.fetchHomeSectionList() }
        )
    }
}