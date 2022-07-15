package com.journalmetro.app.common.preferences.delegate

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by App Developer on June/2021.
 */
class ListHashMapPreference (
    private val sharedPreferences: SharedPreferences,
    private val key: String
) {

    // Get list.
    fun getValueList(): ArrayList<HashMap<String, String>> {
        val defaultList = ArrayList<HashMap<String, String>>()
        val json = sharedPreferences.getString(key, Gson().toJson(defaultList))
        val type = object : TypeToken<ArrayList<HashMap<String, String>?>?>() {}.type
        return Gson().fromJson(json, type)
    }

    // Set item in list.
    fun setValueItemInList(item: HashMap<String, String>) {
        val itemKey = item.keys.first() // Get item key.

        if (!isValueItemAvailableInList(itemKey)) { // Block for duplication.
            val list = getValueList() // Keep first list.
            list.add(item) // Add new item.
            deleteValueList() // Delete old list.
            setValueList(list) // Set updated list.
        }
    }

    // Get value of HashMap item in List.
    fun getValueItemInList(keyOfSpecificValue: String): String {
        return if (isValueItemAvailableInList(keyOfSpecificValue))
            getValueList()[getValueItemIndex(keyOfSpecificValue)!!].getValue(keyOfSpecificValue)
        else ""
    }

    // Delete HashMap item in List.
    fun deleteValueItemInList(keyOfSpecificValue: String) {
        if (isValueItemAvailableInList(keyOfSpecificValue)){ // First check this item in list or not.
            val list = getValueList() // Keep first list.
            list.removeAt(getValueItemIndex(keyOfSpecificValue)!!) // Remove item by it's key.
            deleteValueList() // Delete old list.
            if (list.size > 0) setValueList(list) // Set updated list.
        }
    }

    // Is HashMap item in List?
    fun isValueItemAvailableInList(keyOfSpecificValue: String): Boolean {
        val list = getValueList() // Get list.

        // Find item.
        for (hashMap in list) {
            if (hashMap.containsKey(keyOfSpecificValue)) return true
        }
        return false
    }

    // *****
    // Fun_Private.
    // Set list.
    private fun setValueList(list: ArrayList<HashMap<String, String>>) {
        val json = Gson().toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    // Find HashMap item index in List.
    private fun getValueItemIndex(keyOfSpecificValue: String): Int? {
        val list = getValueList() // Get list.

        for (hashMap in list) {
            if (hashMap.containsKey(keyOfSpecificValue)) return list.indexOf(hashMap)
        }
        return null
    }

    // Delete list.
    private fun deleteValueList() {
        sharedPreferences.edit().remove(key).apply()
    }
}