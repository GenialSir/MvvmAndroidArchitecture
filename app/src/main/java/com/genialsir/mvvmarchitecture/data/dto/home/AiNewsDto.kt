package com.genialsir.mvvmarchitecture.data.dto.home

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class AINewsDto(
    @Json(name = "allnum") val allNum: Int = 0,
    @Json(name = "curpage") val curPage: Int = 0,
    @Json(name = "newslist") val newsList: List<AINewsItem> = emptyList()
) : Parcelable

// 单条新闻
@Parcelize
@JsonClass(generateAdapter = true)
data class AINewsItem(
    @Json(name = "ctime") val ctime: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "id") val id: String = "",
    @Json(name = "picUrl") val picUrl: String = "",
    @Json(name = "source") val source: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "url") val url: String = ""
) : Parcelable


