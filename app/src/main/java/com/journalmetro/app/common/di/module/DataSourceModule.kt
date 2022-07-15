package com.journalmetro.app.common.di.module

import com.journalmetro.app.common.data.source.rest.JournalMetroService
import com.journalmetro.app.locals.source.LocalsRemoteDataSource
import com.journalmetro.app.post.source.PostRemoteDataSource
import com.journalmetro.app.postList.source.PostListRemoteDataSource
import com.journalmetro.app.sectionList.source.SectionListRemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DataSourceModule {

    @Singleton
    @Provides
    fun providePostListRemoteDataSource(journalMetroService: JournalMetroService) =
        PostListRemoteDataSource(journalMetroService)

    @Singleton
    @Provides
    fun providePostRemoteDataSource(journalMetroService: JournalMetroService) =
        PostRemoteDataSource(journalMetroService)

    @Singleton
    @Provides
    fun provideSectionListRemoteDataSource(journalMetroService: JournalMetroService): SectionListRemoteDataSource =
        SectionListRemoteDataSource(journalMetroService)

    @Singleton
    @Provides
    fun provideLocalsRemoteDataSource(journalMetroService: JournalMetroService): LocalsRemoteDataSource =
        LocalsRemoteDataSource(journalMetroService)
}

