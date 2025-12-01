package com.genialsir.mvvmcommon.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/17
 */
class CommonParamInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        //给所有请求加上通用参数
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("appVersion", "1.0.0")
            .addQueryParameter("platform", "android")
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}
