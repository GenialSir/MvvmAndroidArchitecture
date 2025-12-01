package com.genialsir.mvvmarchitecture.data

import androidx.paging.PagingData
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.data.dto.login.LoginRequest
import com.genialsir.mvvmarchitecture.data.dto.login.LoginResponse
import com.genialsir.mvvmcommon.data.DataResource
import kotlinx.coroutines.flow.Flow

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/12
 */
interface DataRepositorySource {

    //请求新闻数据
    suspend fun reqAiNewsPagingData(
    ): Flow<PagingData<AINewsItem>>


    suspend fun doLogin(loginRequest: LoginRequest): Flow<DataResource<LoginResponse>>

    suspend fun addToFavourites(id: String): Flow<DataResource<Boolean>>

    suspend fun getFavourites(): Flow<DataResource<Set<String>>>

    suspend fun removeFromFavourites(id: String): Flow<DataResource<Boolean>>

    suspend fun isFavourite(id: String): Flow<DataResource<Boolean>>

}
