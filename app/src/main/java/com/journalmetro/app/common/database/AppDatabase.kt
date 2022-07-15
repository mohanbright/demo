package com.journalmetro.app.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.journalmetro.app.post.converters.ConverterContentRendered
import com.journalmetro.app.post.dao.DaoPost
import com.journalmetro.app.post.entity.EntityPost
import com.journalmetro.app.searchResult.dao.SearchResultDao
import com.journalmetro.app.searchResult.entity.SearchResultEntity
import com.journalmetro.app.section.dao.DaoSection
import com.journalmetro.app.section.entity.EntitySection

/**
 * Created by App Developer on June/2021.
 */
@Database(entities = [
    EntityPost::class,
    SearchResultEntity::class,
    EntitySection::class
 ],
version = 2)
@TypeConverters(ConverterContentRendered::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun daoPost(): DaoPost

    abstract fun searchResultsDao(): SearchResultDao

    abstract fun daoSection(): DaoSection

    companion object{
        fun getMigrations() : Array<Migration> {
            return arrayOf(migration1to2)
        }

        private val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE saved_post_table ADD COLUMN type TEXT")
            }
        }
    }

}