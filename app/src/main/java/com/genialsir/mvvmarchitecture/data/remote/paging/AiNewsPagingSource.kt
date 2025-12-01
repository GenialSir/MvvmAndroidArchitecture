package com.genialsir.mvvmarchitecture.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.data.remote.service.HomeService
import com.genialsir.mvvmcommon.constant.API_KEY
import kotlin.let
import kotlin.to

/**
 * @author genialsir@163.com (GenialSir) on 2025/10/20
 */
class AiNewsPagingSource(
    private val homeService: HomeService
) :
    PagingSource<Int, AINewsItem>() {
    override fun getRefreshKey(state: PagingState<Int, AINewsItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AINewsItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val response = homeService.reqAiIndexData(
                mapOf(
                    "key" to API_KEY,
                    "page" to page,
                    "num" to pageSize,
                )
            )
            val alarmItems = response.body()!!.data?.newsList
            val nextKey = if (alarmItems!!.isEmpty()) {
                null
            } else {
                page + 1
            }
            LoadResult.Page(
                data = alarmItems,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
