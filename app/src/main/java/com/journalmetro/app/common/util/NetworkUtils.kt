package com.journalmetro.app.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtils(val context: Context) {

    @Suppress("DEPRECATION")
    fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo
                != null
            ) {
                return networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
            }
        } else {
            val network: Network? = cm.activeNetwork
            if (network != null) {
                val networkCapabilities = cm.getNetworkCapabilities(network)
                return if (networkCapabilities != null) {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                } else {
                    false
                }
            }
        }

        return false
    }
}