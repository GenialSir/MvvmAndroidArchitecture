package com.genialsir.mvvmcommon.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/17
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("Content-Type", "application/json")
            .method(original.method, original.body)
            .build()

        return chain.proceed(request)
    }
}
