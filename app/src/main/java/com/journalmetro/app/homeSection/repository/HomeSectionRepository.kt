package com.journalmetro.app.homeSection.repository

import androidx.lifecycle.LiveData
import com.journalmetro.app.common.data.Resource

import com.journalmetro.app.homeSection.model.HomeSection

/**
 * Created by App Developer on July/2021.
 */
interface HomeSectionRepository {

    fun fetchHomeSectionList(): LiveData<Resource<List<HomeSection>>>
}