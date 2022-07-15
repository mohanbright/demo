package com.journalmetro.app.common.util

import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.abs

class LatLngUtils {
    companion object {
        fun iSO6709StringToLatLng(iSO6709Representation: String?): LatLng? {
            var latLng: LatLng? = null
            if (iSO6709Representation != null) {
                val match =
                    Regex("^([-+]?\\d+\\.\\d+?)\\s*([-+]?\\d+\\.\\d+?)\$").find(
                        iSO6709Representation.toString()
                    )!!
                val (lat, lng) = match.destructured
                latLng =
                    LatLng(
                        lat.replace("+", "").toDouble(),
                        lng.replace("+", "").toDouble()
                    )
            }
            return latLng
        }

        fun formatCoordinateISO6709(lat: Double, long: Double, alt: Double? = null) = listOf(
            abs(lat) to if (lat >= 0) "+" else "-", abs(long) to if (long >= 0) "+" else "-"
        ).joinToString(" ") {
            val degrees = it.first
            "%s%f".format(Locale.US, it.second, degrees)
        } + (alt?.let { " %s%.1fm".format(Locale.US, if (alt < 0) "−" else "", abs(alt)) } ?: "")

    }
}