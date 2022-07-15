package com.journalmetro.app.common.util.date

import android.content.Context
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.DateFormat

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {
    companion object {
        fun isoDateTimeNoMillis(date: Date): String {
            val locale: Locale = Locale.getDefault()
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX", locale).format(date)
        }

        /**
         * Convert Json date to readable date.
         * return date, such as 20 May 2021.
         */
        fun parseIsoDateTimeDayNumberMonthNameYearNumber(dateString: String, locale: Locale): String {
            for (format in listOfSimpleDateFormat()) {
                try {
                    val date = SimpleDateFormat(format).parse(dateString)
                    return SimpleDateFormat("dd MMMM yyyy", locale).format(date)
                }
                catch (e: ParseException) { /* Not implemented.*/ }
            }
            return dateString
        }

        /**
         * Convert Json date to readable date.
         * return date, such as 3 Jun 2021 - 15:44.
         */
        fun parseIsoDateTimeDayNumberMonthNameYearNumberHourMinute(
            dateString: String, locale: Locale)
        : String {
            for (format in listOfSimpleDateFormat()) {
                try {
                    val date = SimpleDateFormat(format).parse(dateString)
                    return SimpleDateFormat("dd MMMM yyyy '-' HH:mm", locale).format(date)
                }
                catch (e: ParseException) { /* Not implemented.*/ }
            }
            return dateString
        }

        fun parseIsoDateTimeNoMillis(dateString: String): Date? {

            for (format in listOfSimpleDateFormat()) {
                try {
                    return SimpleDateFormat(format).parse(dateString)
                } catch (e: ParseException) {
                }

            }
            return null
        }

        /**
         * This is using to get unique time key as Long type number.
         * Now, it is using for item saving.
         */
        fun getCurrentDateAsUniqueNumber(): Long {
            val sdf = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CANADA_FRENCH)
            return sdf.format(Date()).toLong()
        }

        fun isValidDate(dateString: String): Boolean {

            for (format in listOfSimpleDateFormat()) {
                try {
                    SimpleDateFormat(format).parse(dateString)
                    return true
                } catch (e: ParseException) {
                }

            }
            return false
        }
        fun parseDateToMilliSecond(dateString: String): Long? {
            try {

                return SimpleDateFormat("d MMMM yyyy 'at' HH:mm").parse(dateString).time
            } catch (e: ParseException) {
            }
            return null
        }

        fun isoStringToDateToFormeDateTimeNoMillisWithSecond(dateString: String): String {
            val locale: Locale = Locale.getDefault()
            return try {
                var date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX", locale).parse(dateString)
                SimpleDateFormat("dd MMM,yyyy HH:mm:ss", locale).format(date)
            } catch (e: ParseException) {
                ""
            }

        }

        private fun is24HourModeEnabled(context: Context): Boolean {
            return android.text.format.DateFormat.is24HourFormat(context)
        }

        fun isoDateTimeNoMillisWithoutSecond(date: Date, context: Context): String {
            if (Locale.getDefault().toString().contains("en")) {
                if (is24HourModeEnabled(context)) {
                    return SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(
                        date
                    )
                } else {
                    return SimpleDateFormat(
                        "MMM dd, yyyy 'at' hh:mm aaa",
                        Locale.getDefault()
                    ).format(date)
                        .replace("a.m.", "AM")?.replace("p.m.", "PM")
                }
            } else {
                return DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM, DateFormat.SHORT,
                    Locale.getDefault()
                ).format(date)
            }
        }

        fun isoDateTimeNoMillisWithSecondByRegion(date: Date, context: Context): String {
            if (Locale.getDefault().toString().contains("en")) {
                if (is24HourModeEnabled(context)) {
                    return SimpleDateFormat(
                        "MMM dd, yyyy 'at' HH:mm:ss",
                        Locale.getDefault()
                    ).format(date)
                } else {
                    return SimpleDateFormat(
                        "MMM dd, yyyy 'at' hh:mm:ss aaa",
                        Locale.getDefault()
                    ).format(date)
                        .replace("a.m.", "AM")?.replace("p.m.", "PM")
                }
            } else {
                return DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM, DateFormat.SHORT,
                    Locale.getDefault()
                ).format(date)
            }
        }

        fun isoDateTimeNoMillisWithSecond(date: Date): String {
            val locale: Locale = Locale.getDefault()
            return SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss aaa", locale).format(date)
        }

        fun isoDateTimeNoMillisWithoutPMAM(date: Date): String {
            val locale: Locale = Locale.getDefault()
            return try {
                SimpleDateFormat("dd MMM yyyy HH:mm:ss", locale).format(date)
            } catch (e: Exception) {
                ""
            }

        }

        @JvmStatic
        fun formatDateTimeToLongDateShortTime(dateTime: DateTime?): String? {
            return dateTime?.toString(DateTimeFormat.forPattern("MMMM d, yyyy  hh:mm a"))
                ?.replace("a.m.", "AM")?.replace("p.m.", "PM")
                ?: ""
        }

        @JvmStatic
        fun formatDateTimeToLongDateShortTime(is24hChosen: Boolean, dateTime: DateTime?): String? {
            if (is24hChosen){
                return dateTime?.toString(DateTimeFormat.forPattern("MMMM d, yyyy 'at' HH:mm"))
                    ?: ""
            }else {
                return dateTime?.toString(DateTimeFormat.forPattern("MMMM d, yyyy 'at' hh:mm a"))
                    ?.replace("a.m.", "AM")?.replace("p.m.", "PM")
                    ?: ""
            }
        }

        @JvmStatic
        fun formatDateTimeToLongDateTime(dateTime: DateTime?): String? {
            return dateTime?.toString(DateTimeFormat.forPattern("d MMMM yyyy  hh:mm a"))
                ?.replace("a.m.", "AM")?.replace("p.m.", "PM")
                ?: ""
        }

        @JvmStatic
        fun formatDateTimeToLongDateTimeWithoutZone(dateTime: DateTime?): String? {
            return dateTime?.toString(DateTimeFormat.forPattern("d MMMM yyyy  HH:mm"))

        }

        fun listOfSimpleDateFormat(): List<String> {
            var list = listOf<String>(
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.XXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.Z",
                "yyyyMMdd'T'HHmmssSSSZ",
                "yyyyMMdd'T'HHmmssZ",
                "yyyyMMdd'T'HHmmssSSSXXX",
                "yyyyMMdd'T'HHmmssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "d MMMM yyyy 'at' hh:mm a",
                "MMMM d, yyyy 'at' hh:mm a",
                "d MMMM yyyy 'at' HH:mm",
                "EEE MMM dd hh:mm:ss  yyyy"
            )
            return list
        }
    }

}