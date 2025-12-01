package com.genialsir.mvvmcommon.network

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/18
 */
interface NetworkConnectivity {
    fun getNetworkInfo(): NetworkStatus
    fun isConnected(): Boolean
}