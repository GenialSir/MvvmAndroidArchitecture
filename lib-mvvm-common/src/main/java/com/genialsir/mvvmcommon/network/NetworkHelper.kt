package com.genialsir.mvvmcommon.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/18
 */
class NetworkHelper @Inject constructor(val context: Context) : NetworkConnectivity {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun getNetworkInfo(): NetworkStatus {
        val network = connectivityManager.activeNetwork ?: return NetworkStatus.DISCONNECTED
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return NetworkStatus.DISCONNECTED

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                -> NetworkStatus.WIFI

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                -> NetworkStatus.CELLULAR

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                -> NetworkStatus.ETHERNET

            else -> NetworkStatus.UNKNOWN
        }
    }

    override fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
