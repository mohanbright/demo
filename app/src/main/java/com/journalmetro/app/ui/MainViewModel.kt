package com.journalmetro.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.journalmetro.app.common.data.Resource
import com.journalmetro.app.common.util.tagAndroid
import com.journalmetro.app.common.util.tagFID
import com.journalmetro.app.common.util.tagPlatform
import com.journalmetro.app.common.util.tagToken
import com.journalmetro.app.notifications.repository.NotificationRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor(val repository: NotificationRepository) :
    ViewModel() {
    var _isFullScreenMode = MutableLiveData<Boolean>()
    val isFullScreenMode : LiveData<Boolean> get() = _isFullScreenMode

    fun sendPushTokenToServer(fid: String, token: String, isUpdate : Boolean) : LiveData<Resource<Void>> {
        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put(tagFID, fid)
        jsonObject.put(tagToken, token)
        jsonObject.put(tagPlatform, tagAndroid)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        return if (isUpdate){
            repository.updatePushTokenOnServer(requestBody)// Update push notification
        } else
            repository.sendPushTokenToServer(requestBody) // Send push notification for the first time
    }
}