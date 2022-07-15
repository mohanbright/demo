package com.journalmetro.app.common.di.module

import android.app.Application
import androidx.room.Room
import com.journalmetro.app.common.database.AppDatabase
import com.journalmetro.app.post.dao.DaoPost
import com.journalmetro.app.searchResult.dao.SearchResultDao
import com.journalmetro.app.section.dao.DaoSection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by App Developer on June/2021.
 */
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDaoPost(appDatabase: AppDatabase): DaoPost {
        return appDatabase.daoPost()
    }

    @Singleton
    @Provides
    fun provideSearchResultDao(appDatabase: AppDatabase): SearchResultDao =
        appDatabase.searchResultsDao()

    @Singleton
    @Provides
    fun provideDaoSection(appDatabase: AppDatabase): DaoSection {
        return appDatabase.daoSection()
    }

    @Singleton
    @Provides
    fun databaseModuleDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .addMigrations(*AppDatabase.getMigrations())
            .build()
    }
}