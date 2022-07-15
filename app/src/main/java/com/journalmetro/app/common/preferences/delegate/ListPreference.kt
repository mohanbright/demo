package com.journalmetro.app.common.preferences.delegate

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by App Developer on June/2021.
 */
class ListPreference(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val list: java.util.ArrayList<String>? = null
) : ReadWriteProperty<Any, java.util.ArrayList<String?>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): java.util.ArrayList<String?> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: java.util.ArrayList<String?>
    ) {
        val json = Gson().toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }
}