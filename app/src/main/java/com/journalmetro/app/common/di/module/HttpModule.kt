package com.journalmetro.app.common.di.module

import android.content.SharedPreferences
import com.google.gson.Gson
import com.journalmetro.app.BuildConfig
import com.journalmetro.app.common.data.source.rest.AuthInterceptor
import com.journalmetro.app.common.data.source.rest.JournalMetroService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class HttpModule {

    @Singleton
    @Provides
    fun provideJournalMetroService(
        okhttpClient: OkHttpClient, converterFactory: GsonConverterFactory
    ) = provideService(okhttpClient, converterFactory, JournalMetroService::class.java)

    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JournalMetroService.ENDPOINT)
            .client(okhttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    private fun <T> provideService(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory, clazz: Class<T>
    ): T {
        return createRetrofit(okhttpClient, converterFactory).create(clazz)
    }

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .readTimeout(30, TimeUnit.SECONDS) // Handle to timeout error.
            .connectTimeout(30, TimeUnit.SECONDS) // Handle to timeout error.
            .build()

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    fun provideAuthInterceptor(sharedPreferences: SharedPreferences) =
        AuthInterceptor(sharedPreferences)

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)
}