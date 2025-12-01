package com.genialsir.mvvmcommon.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter
import com.task.data.remote.moshiFactories.MyKotlinJsonAdapterFactory

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/17
 * Moshi 通用工具类
 * - 兼容 Moshi 1.15+（避免 public inline 调用 internal API 报错）
 * - 支持任意对象 <-> JSON
 */
object MoshiUtil {

    // 单例 Moshi
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(MyKotlinJsonAdapterFactory()) // 自定义的Factory
            .build()
    }

    /**
     * 将 JSON 字符串解析为对象
     * @param json JSON 字符串
     * @param clazz 对象的 Class
     * @return 对象，解析失败返回 null
     */
    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return try {
            moshi.adapter(clazz).fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将对象转为 JSON 字符串
     * @param obj 对象
     * @param clazz 对象的 Class
     * @return JSON 字符串
     */
    fun <T> toJson(obj: T, clazz: Class<T>): String {
        return moshi.adapter(clazz).toJson(obj)
    }

    /**
     * 获取 JsonAdapter（可自定义使用）
     */
    fun <T> adapter(clazz: Class<T>): JsonAdapter<T> {
        return moshi.adapter(clazz)
    }
}
