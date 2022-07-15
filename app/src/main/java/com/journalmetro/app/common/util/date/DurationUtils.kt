package com.journalmetro.app.common.util.date

import org.joda.time.Period

object DurationUtils {

    fun parseMinutesFromPeriod(durationString: String?) : String? {
        return try {
            Period.parse(durationString).toStandardMinutes().minutes.toString()
        } catch (e: Exception) {
            null
        }

    }
}