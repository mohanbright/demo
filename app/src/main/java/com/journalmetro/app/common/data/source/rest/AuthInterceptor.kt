package com.journalmetro.app.common.data.source.rest

import android.content.SharedPreferences
import com.journalmetro.app.common.preferences.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * A {@see RequestInterceptor} that adds an auth token to requests
 */
class AuthInterceptor(private val prefs: SharedPreferences) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = prefs.getString(AppPreferences.ACCESS_TOKEN, "")
        val request = if (accessToken.isNullOrEmpty()) {
            chain.request().newBuilder()
                .build()
        } else {
            chain.request().newBuilder().addHeader(
                "Authorization", "Bearer $accessToken"
            ).build()
        }
        return chain.proceed(request)
    }
}
