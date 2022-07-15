package com.journalmetro.app.common.data

/**
 * A generic class that holds a value with its loading status.
 *
 * Result is usually created by the Repository classes where they return
 * `LiveData<Result<T>>` to pass back the latest data to the UI with its fetch status.
 */

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val statusCode:Int = 0
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null,statusCode: Int = 0): Resource<T> {
            return Resource(Status.ERROR, data, message,statusCode)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}