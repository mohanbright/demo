package com.journalmetro.app.common.data.model

import androidx.databinding.BaseObservable
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


abstract class Model : BaseObservable() {

    fun removeNullValues(): Map<String?, Any?>? {
        val gson = GsonBuilder().addSerializationExclusionStrategy(strategy)
            .addDeserializationExclusionStrategy(strategy).create()
        return Gson().fromJson(
            gson.toJson(this),
            object : TypeToken<HashMap<String?, Any?>?>() {}.type
        )
    }

    private val strategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }

        override fun shouldSkipField(field: FieldAttributes): Boolean {
            return field.getAnnotation(ExcludeModelField::class.java) != null
        }
    }


}