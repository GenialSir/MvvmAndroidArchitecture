package com.genialsir.mvvmarchitecture.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.genialsir.mvvmarchitecture.data.dto.home.AINewsItem
import com.genialsir.mvvmarchitecture.data.remote.paging.AiNewsPagingSource
import com.genialsir.mvvmarchitecture.data.remote.service.HomeService
import com.genialsir.mvvmcommon.constant.BASE_URL
import com.genialsir.mvvmcommon.network.NetworkConnectivity
import com.genialsir.mvvmcommon.data.DataResource
import com.genialsir.mvvmcommon.data.remote.ServiceGenerator
import com.genialsir.mvvmcommon.error.EMPTY_RESPONSE
import com.genialsir.mvvmcommon.error.NETWORK_ERROR
import com.genialsir.mvvmcommon.error.NO_INTERNET_CONNECTION
import com.genialsir.mvvmcommon.util.LogUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/11
 */
class RemoteData @Inject constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity
): RemoteDataSource {

    //请求AI新闻数据
    override suspend fun requestAINews(): Flow<PagingData<AINewsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, //每条页数据数量
                enablePlaceholders = false,
//                prefetchDistance = 5,  // 离底部多少条开始加载下一页
//                initialLoadSize = 20   // 首次加载数量
            ),
            pagingSourceFactory = {
                AiNewsPagingSource(
                    serviceGenerator.createService(
                        HomeService::class.java,
                        BASE_URL
                    )
                )
            }
        ).flow
    }

    /**
     * 消除函数调用开销
     * 编译器会把 processCall 的函数体直接复制到调用点，避免函数调用的性能开销。
     * 对于协程里频繁调用的网络函数，性能开销可以稍微优化。
     *
     * 允许使用 reified 泛型
     * 在 Kotlin 中，普通泛型在运行时是 被擦除的（type erasure），你无法获取 T 的类型。
     * 只有内联函数，才可以在运行时使用 reified T，让泛型类型保留下来。
     *
     * 支持 lambda 参数
     * 内联函数可以把传入的 lambda也内联到调用点，避免额外的对象创建和闭包开销。
     * 传的 responseCall: suspend () -> Response<T> 就会内联，性能更好。
     */
    private suspend inline fun <reified T> processCall(
        responseCall: suspend () -> Response<T>
    ): DataResource<T> {
        if (!networkConnectivity.isConnected()) {
            return DataResource.Error(NO_INTERNET_CONNECTION)
        }

        return try {
            val response = responseCall.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    DataResource.Success(body)
                } else {
                    DataResource.Error(EMPTY_RESPONSE)
                }
            } else {
                DataResource.Error(response.code())
            }
        } catch (e: Exception) {
            LogUtil.e("RemoteData", e.toString())
            DataResource.Error(NETWORK_ERROR)
        }
    }

//    private suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
//        if(!networkConnectivity.isConnected()){
//            return NO_INTERNET_CONNECTION
//        }
//        return try{
//            val response = responseCall.invoke()
//            val responseCode = response.code()
//            if(response.isSuccessful){
//                response.body()
//            }else{
//                responseCode
//            }
//        }catch (e: IOException){
//            NETWORK_ERROR
//        }
//
//    }

}
