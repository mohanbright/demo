package com.journalmetro.app.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import kotlin.math.cos
import kotlin.math.pow


class DeviceUtils(val context: Context) {

    @SuppressLint("HardwareIds")
    fun getMobileDeviceUuid(): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getPixelsPerMeter(lat: Double, zoom: Double): Double {
        val pixelsPerTile =
            256 * ((context.resources.displayMetrics.densityDpi).toDouble() / 160)
        val numTiles = 2.0.pow(zoom)
        val metersPerTile: Double =
            cos(Math.toRadians(lat)) * 40075000 / numTiles
        return pixelsPerTile / metersPerTile
    }

    fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }
}