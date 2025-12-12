package com.genialsir.mvvmcommon.data.remote.interceptor

import com.genialsir.mvvmcommon.bus.GlobalEventBus
import com.genialsir.mvvmcommon.error.INVALID_TOKEN
import com.genialsir.mvvmcommon.util.LogUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/10
 */
class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // 情况1：HTTP 层 403
        if (response.code == 403) {
            LogUtil.d("TokenInterceptor", "检测到 HTTP 403 响应Code")
            handleTokenInvalid()
            return response
        }

        // 情况2：业务层 code = INVALID_TOKEN
        if (response.code == 200) {
            // 安全 peek body，不会消费 stream
            val responseBodyString = response.peekBody(Long.MAX_VALUE).string()

            try {
                val jsonObject = JSONObject(responseBodyString)
                val code = jsonObject.optInt("code")

                if (code == INVALID_TOKEN) {
                    LogUtil.d("TokenInterceptor", "检测到响应体中的 TOKEN 无效Code")
                    handleTokenInvalid()
                }

            } catch (e: Exception) {
                LogUtil.e("TokenInterceptor", "解析响应体失败: ${e.message}")
            }
        }

        return response
    }

    private fun handleTokenInvalid() = runBlocking {
        GlobalEventBus.sendTokenInvalid()
    }
}
