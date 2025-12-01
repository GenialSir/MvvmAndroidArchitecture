package com.genialsir.mvvmcommon.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/28
 * 负责封装每个接口的原始返回结构，和Resource配合使用不合并的原因是增加数据结构的灵活拓展性。
 */
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "result")
    val data: T?
)
