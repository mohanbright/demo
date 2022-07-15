package com.journalmetro.app.common.preferences

import android.content.SharedPreferences
import com.journalmetro.app.common.preferences.delegate.*
import java.util.ArrayList
import kotlin.properties.ReadWriteProperty

fun SharedPreferences.int(key: String, defaultValue: Int = 0): ReadWriteProperty<Any, Int> =
    IntPreference(this, key, defaultValue)

fun SharedPreferences.boolean(
    key: String,
    defaultValue: Boolean = false
): ReadWriteProperty<Any, Boolean> =
    BooleanPreference(this, key, defaultValue)

fun SharedPreferences.string(
    key: String,
    defaultValue: String? = null
): ReadWriteProperty<Any, String?> =
    StringPreference(this, key, defaultValue)

fun SharedPreferences.float(
    key: String,
    defaultValue: Float = 0.0f
): ReadWriteProperty<Any, Float> =
    FloatPreference(this, key, defaultValue)

// *****
// Working with list.
fun SharedPreferences.arrayList(
    key: String
) = ListPreference(this, key)

fun SharedPreferences.arrayListWithHashMap(
    key: String
) = ListHashMapPreference(this, key)


// *****
// Working with delete/remove/clear issues.
/**
 * This is for delete specific data with key.
 */
fun SharedPreferences.delete(key: String) {
    this.edit().remove(key).apply()
}

/**
 * Be careful about it because it is deleting all SharedPreferences data.
 * Like, reset or remove all data.
 */
fun SharedPreferences.deleteAll() {
    this.edit().clear().apply()
}