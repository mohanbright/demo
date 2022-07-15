package com.journalmetro.app.common.data.source.rest

import com.journalmetro.app.common.data.DataSource
import com.journalmetro.app.common.data.Resource
import retrofit2.Response

abstract class BaseRestDataSource : DataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null) Resource.success(body) else Resource.error("",statusCode = response.code())
            }
            return error(" ${response.code()} ${response.message()}", "Line 16 (try)")
        } catch (e: Exception) {
            return error(e.message ?: e.toString(), "Line 18 (catch)")
        }
    }

    private fun <T> error(message: String, from: String): Resource<T> {
        return Resource.error("$from - Network call has failed for a following reason: $message")
    }
}