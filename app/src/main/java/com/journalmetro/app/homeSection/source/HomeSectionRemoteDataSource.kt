package com.journalmetro.app.homeSection.source

import com.journalmetro.app.common.data.source.rest.BaseRestDataSource
import com.journalmetro.app.common.data.source.rest.JournalMetroService
import javax.inject.Inject

/**
 * Created by App Developer on July/2021.
 */
class HomeSectionRemoteDataSource @Inject constructor(
    private val service: JournalMetroService
) :
    BaseRestDataSource() {

    suspend fun fetchHomeSectionList() = getResult { service.getHomeSections() }
}