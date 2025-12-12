package com.genialsir.mvvmcommon.data.remote


import com.genialsir.mvvmcommon.BuildConfig
import com.genialsir.mvvmcommon.constant.BASE_URL
import com.genialsir.mvvmcommon.data.remote.interceptor.CommonParamInterceptor
import com.genialsir.mvvmcommon.data.remote.interceptor.CurlLoggingInterceptor
import com.genialsir.mvvmcommon.data.remote.interceptor.HeaderInterceptor
import com.genialsir.mvvmcommon.data.remote.interceptor.TokenInterceptor
import com.genialsir.mvvmcommon.data.remote.moshi.FlexibleStringListAdapter
import com.genialsir.mvvmcommon.data.remote.moshi.MyStandardJsonAdapters
import com.genialsir.mvvmcommon.util.LogUtil
import com.squareup.moshi.Moshi
import com.task.data.remote.moshiFactories.MyKotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/12
 */

private const val timeoutRead = 15L   //In seconds
private const val timeoutConnect = 15L   //In seconds

@Singleton
class ServiceGenerator @Inject constructor() {

    private val TAG: String = ServiceGenerator::class.java.simpleName

    // OkHttpClient 复用
    private val okHttpClient: OkHttpClient by lazy { initOkHttpClient() }
    // Retrofit 缓存，不同 baseUrl 创建不同实例
    private val retrofitCache = ConcurrentHashMap<String, Retrofit>()

    private val httpLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val logger = HttpLoggingInterceptor.Logger { message ->
                LogUtil.d(TAG, "OkHttp======Message:$message")
            }
            val loggingInterceptor = HttpLoggingInterceptor(logger)
            if (BuildConfig.DEBUG) {
                loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
            } else {
                loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.NONE }
            }
            return loggingInterceptor
        }

    private fun initOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(httpLoggingInterceptor)
        builder.addInterceptor(CurlLoggingInterceptor())
        builder.addInterceptor(HeaderInterceptor())
        builder.addInterceptor( TokenInterceptor())
        builder.addInterceptor(CommonParamInterceptor())
        builder.connectTimeout(timeoutConnect, TimeUnit.SECONDS)
        builder.readTimeout(timeoutRead, TimeUnit.SECONDS)
        return builder.build()
    }


    fun <S> createService(serviceClass: Class<S>, baseUrl: String? = null): S {
        val url = baseUrl ?: BASE_URL
        return getRetrofit(url).create(serviceClass)
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        // 双重检查锁 + 缓存保证线程安全
        return retrofitCache[baseUrl] ?: synchronized(this) {
            retrofitCache[baseUrl] ?: Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
                .build()
                .also { retrofitCache[baseUrl] = it }
        }
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .add(FlexibleStringListAdapter())// 兼容数组 + 字符串两种结构
//            .add(JsonStringListAdapter())
            .add(MyKotlinJsonAdapterFactory())
            .add(MyStandardJsonAdapters.FACTORY)
            .build()
    }

}