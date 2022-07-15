package com.journalmetro.app.common.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

/**
 * The database serves as the single source of truth.
 * Therefore UI can receive data updates from database only.
 * Function notify UI about:
 * [Resource.Status.SUCCESS] - with data from database
 * [Resource.Status.ERROR] - if error has occurred from any source
 * [Resource.Status.LOADING]
 */
fun <T, A> resultLiveData(/*databaseQuery: () -> LiveData<T>,*/
                          networkCall: suspend () -> Resource<A>/*,
                          saveCallResult: suspend (A) -> Unit*/): LiveData<Resource<T>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading<T>())
            //val source = databaseQuery.invoke().map { Resource.success(it) }
            //emitSource(source)

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == Resource.Status.SUCCESS) {
                //saveCallResult(responseStatus.data!!)
                emit(Resource.success(responseStatus.data!! as T))
            } else if (responseStatus.status == Resource.Status.ERROR) {
                emit(Resource.error<T>(responseStatus.message!!,statusCode = responseStatus.statusCode))
                //emitSource(source)
            }
        }