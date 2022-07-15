package com.journalmetro.app.common.data.source.rest

import com.journalmetro.app.BuildConfig
import com.journalmetro.app.homeSection.model.HomeSection
import com.journalmetro.app.locals.model.Local
import com.journalmetro.app.post.model.Post
import com.journalmetro.app.postList.model.PostList
import com.journalmetro.app.section.model.Section
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface JournalMetroService {

    companion object {
        const val ENDPOINT = BuildConfig.SERVER_ENDPOINT
    }

    // Post list.
    @GET("v1/Posts")
    suspend fun getPosts(
        @Query("key") filter: String,
        @Query("key") maxResultCount: Int,
        @Query("key", encoded = true) categoryIds: String,
        @Query("key") type: String,
        @Query("key") includeAds: Boolean = true
    ): Response<PostList>

    // Post item.
    @GET("v1/Posts/{key}")
    suspend fun getPost(
        @Path("key") postId: String
    ): Response<Post>

    // Section list without parameter.
    @GET("key")
    suspend fun getSections(): Response<List<Section>>

    // Borough list.
    @GET("v1/Locakeyls")
    suspend fun getLocals(): Response<Local>

    // Home page section list.
    @GET("v1/key")
    suspend fun getHomeSections(): Response<List<HomeSection>>

    // Post notification
    @POST("v1/key")
    suspend fun sendPushTokenToServer(@Body requestBody: RequestBody): Response<Void>

    // Update notification
    @PUT("v1/key")
    suspend fun updatePushTokenOnServer(@Body requestBody: RequestBody): Response<Void>

    // Get Daily Recap notification
    @GET("v1/Posts/key")
    suspend fun getDailyRecapNotifications(
        @Query("key") filter: String,
        @Query("key") maxResultCount: Int,
        @Query("key") skipCount: Int,
    ): Response<PostList>
}
