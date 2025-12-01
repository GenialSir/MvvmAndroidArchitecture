package com.genialsir.mvvmarchitecture.data

import androidx.paging.PagingData
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.data.dto.login.LoginRequest
import com.genialsir.mvvmarchitecture.data.dto.login.LoginResponse
import com.genialsir.mvvmarchitecture.data.local.LocalData
import com.genialsir.mvvmarchitecture.data.remote.RemoteData
import com.genialsir.mvvmcommon.data.DataResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext
) : DataRepositorySource {

    override suspend fun reqAiNewsPagingData(
    ): Flow<PagingData<AINewsItem>> {
        return remoteRepository.requestAINews()
    }



    override suspend fun doLogin(loginRequest: LoginRequest): Flow<DataResource<LoginResponse>> {
        return flow {
            emit(localRepository.doLogin(loginRequest))
        }.flowOn(ioDispatcher)
    }

    //添加收藏
    override suspend fun addToFavourites(id: String): Flow<DataResource<Boolean>> {
        return flow {
            try{
                localRepository.addFavourite(id)
                emit(DataResource.Success(true))
            }catch (e: Exception){
                emit(DataResource.Error(-1))
            }

        }.flowOn(ioDispatcher)
    }

    //获取收藏列表
    override suspend fun getFavourites(): Flow<DataResource<Set<String>>> {
        return localRepository.getCachedFavourites().flowOn(ioDispatcher)
    }


    override suspend fun removeFromFavourites(id: String): Flow<DataResource<Boolean>> {
        return flow {
            emit(localRepository.removeFromFavourites(id))
        }.flowOn(ioDispatcher)
    }

    override suspend fun isFavourite(id: String): Flow<DataResource<Boolean>> {
        return localRepository.isFavourite(id)
            .flowOn(ioDispatcher)
    }

}