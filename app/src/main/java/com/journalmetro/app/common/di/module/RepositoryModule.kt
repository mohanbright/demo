package com.journalmetro.app.common.di.module

import com.journalmetro.app.homeSection.repository.HomeSectionRepository
import com.journalmetro.app.homeSection.repository.HomeSectionRepositoryImpl
import com.journalmetro.app.homeSection.source.HomeSectionRemoteDataSource
import com.journalmetro.app.locals.repository.LocalsRepository
import com.journalmetro.app.locals.repository.LocalsRepositoryImpl
import com.journalmetro.app.locals.source.LocalsRemoteDataSource
import com.journalmetro.app.notifications.repository.NotificationRepository
import com.journalmetro.app.notifications.repository.NotificationRepositoryImpl
import com.journalmetro.app.notifications.source.NotificationDataSource
import com.journalmetro.app.post.dao.DaoPost
import com.journalmetro.app.post.repository.PostRepository
import com.journalmetro.app.post.repository.PostRepositoryImpl
import com.journalmetro.app.post.repository.RepositoryPost
import com.journalmetro.app.post.source.PostRemoteDataSource
import com.journalmetro.app.postList.repository.PostListRepository
import com.journalmetro.app.postList.repository.PostListRepositoryImpl
import com.journalmetro.app.postList.source.PostListRemoteDataSource
import com.journalmetro.app.searchResult.dao.SearchResultDao
import com.journalmetro.app.searchResult.repository.SearchResultRepository
import com.journalmetro.app.section.dao.DaoSection
import com.journalmetro.app.section.repository.RepositorySection
import com.journalmetro.app.sectionList.repository.SectionListRepository
import com.journalmetro.app.sectionList.repository.SectionListRepositoryImpl
import com.journalmetro.app.sectionList.source.SectionListRemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providePostListRepository(
        dataSource: PostListRemoteDataSource
    ): PostListRepository = PostListRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun providePostRepository(
        dataSource: PostRemoteDataSource
    ): PostRepository = PostRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideRepositoryPost(
        dataSource: DaoPost
    ): RepositoryPost = RepositoryPost(dataSource)

    @Provides
    @Singleton
    fun provideSearchResultRepository(
        dataSource: SearchResultDao
    ): SearchResultRepository = SearchResultRepository(dataSource)

    @Provides
    @Singleton
    fun provideSectionListRepository(
        dataSource: SectionListRemoteDataSource
    ): SectionListRepository = SectionListRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideRepositorySection(
        dataSource: DaoSection
    ): RepositorySection = RepositorySection(dataSource)

    @Provides
    @Singleton
    fun provideLocalsRepository(
        dataSource: LocalsRemoteDataSource
    ): LocalsRepository = LocalsRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideHomeSectionRepository(
        dataSource: HomeSectionRemoteDataSource
    ): HomeSectionRepository = HomeSectionRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun providePushTokenRepository(
        dataSource: NotificationDataSource
    ): NotificationRepository = NotificationRepositoryImpl(dataSource)
}