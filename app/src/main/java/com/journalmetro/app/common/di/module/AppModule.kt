package com.journalmetro.app.common.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.journalmetro.app.searchResult.dao.SearchResultDao
import com.journalmetro.app.common.util.DeviceUtils
import com.journalmetro.app.common.util.NetworkUtils
import dagger.Module
import dagger.Provides
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton


@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDateFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    @Singleton
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideDeviceUtils(context: Context): DeviceUtils {
        return DeviceUtils(context)
    }

    @Singleton
    @Provides
    fun provideNetworkUtils(context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}

