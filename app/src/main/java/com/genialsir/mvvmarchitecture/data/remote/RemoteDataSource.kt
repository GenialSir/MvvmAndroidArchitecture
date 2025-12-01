package com.genialsir.mvvmarchitecture.data.remote

import androidx.paging.PagingData
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import kotlinx.coroutines.flow.Flow

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/12
 */
interface RemoteDataSource {

    /**
     * 请求 AI 新闻列表
     * @param key API key
     * @param page 当前页
     * @param num 每页数量
     */
    suspend fun requestAINews(): Flow<PagingData<AINewsItem>>
}