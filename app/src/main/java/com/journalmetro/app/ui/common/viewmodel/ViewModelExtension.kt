package com.journalmetro.app.ui.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.data.Resource.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun isResourceLoading(resource: Resource<*>): Boolean {
    return resource.status == Status.LOADING
}

fun <T : LiveData<Resource<G>>, G : Any> T.resourceLoadingLiveData(): LiveData<Boolean> {
    return Transformations.map(this, ::isResourceLoading)
}


//Extension for debounce effect
fun <T> LiveData<T>.debounce(duration: Long = 1000L, coroutineScope: CoroutineScope) =
    MediatorLiveData<T>().also { mld ->
        val source = this
        var job: Job? = null
        mld.addSource(source) {
            job?.cancel()
            job = coroutineScope.launch {
                delay(duration)
                mld.value = source.value
            }
        }
    }