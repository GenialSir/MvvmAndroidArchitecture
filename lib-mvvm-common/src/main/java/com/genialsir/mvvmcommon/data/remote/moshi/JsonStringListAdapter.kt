package com.genialsir.mvvmcommon.data.remote.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types


/**
 * @author genialsir@163.com (GenialSir) on 2025/11/11
 * 仅当后端返回的是纯字符串并且内容是类似：
 * "[\"22\", \"33\"]"
 * 才会被解析成 List<String>
 *
 * 不支持真实 JSON 数组
 */
class JsonStringListAdapter {
    @FromJson
    fun fromJson(json: String): List<String> {
        return try {
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            val adapter = moshi.adapter<List<String>>(type)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @ToJson
    fun toJson(value: List<String>): String {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }
}
