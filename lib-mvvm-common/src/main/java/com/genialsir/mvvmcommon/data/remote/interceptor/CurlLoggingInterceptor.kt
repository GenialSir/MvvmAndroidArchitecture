package com.genialsir.mvvmcommon.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import kotlin.let

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/23
 */
class CurlLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val curlBuilder = kotlin.text.StringBuilder("curl -X ${request.method} ")

        // Headers
        for ((name, value) in request.headers) {
            curlBuilder.append("-H \"$name: $value\" ")
        }

        // Body
        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            curlBuilder.append("-d '$bodyString' ")
        }

        // URL
        curlBuilder.append("\"${request.url}\"")

        Log.d("CurlLoggingInterceptor", curlBuilder.toString())

        return chain.proceed(request)
    }
}
