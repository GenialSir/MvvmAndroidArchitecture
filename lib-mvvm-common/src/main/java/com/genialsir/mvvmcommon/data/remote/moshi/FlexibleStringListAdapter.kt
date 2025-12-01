package com.genialsir.mvvmcommon.data.remote.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/22
 *  * 兼容两种格式：
 *  * 1. 数组："abnormalTimeList": ["2025-11-08 18:22:38"]
 *  * 2. 字符串："abnormalTimeList": "[\"2025-11-08 18:22:38\"]"
 */
class FlexibleStringListAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): List<String>? {
        return when (reader.peek()) {

            // 1. JSON Array → 直接读
            JsonReader.Token.BEGIN_ARRAY -> {
                val list = mutableListOf<String>()
                reader.beginArray()
                while (reader.hasNext()) {
                    list.add(reader.nextString())
                }
                reader.endArray()
                list
            }

            // 2. String（可能是 YAML 风格的 "[...]"）→ 解析成 List
            JsonReader.Token.STRING -> {
                val str = reader.nextString()
                if (str.startsWith("[") && str.endsWith("]")) {
                    str.removePrefix("[").removeSuffix("]")
                        .split(",")
                        .map { it.trim().replace("\"", "") }
                } else {
                    listOf(str)
                }
            }

            // 3. null
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                emptyList()
            }

            else -> throw JsonDataException("Expected string or array")
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<String>?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()
        value.forEach { writer.value(it) }
        writer.endArray()
    }
}
