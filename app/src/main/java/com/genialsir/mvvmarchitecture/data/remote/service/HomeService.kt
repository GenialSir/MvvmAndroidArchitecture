package com.genialsir.mvvmarchitecture.data.remote.service

import com.genialsir.mvvmarchitecture.data.dto.home.AINewsDto
import com.genialsir.mvvmcommon.data.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/1
 */
interface HomeService {

    // 请求 AI 咨询接口，表单提交
    @FormUrlEncoded
    @POST("/ai/index")
    suspend fun reqAiIndexData(
        @FieldMap param: Map<String, @JvmSuppressWildcards Any>
    ): Response<BaseResponse<AINewsDto>>

    // 请求 AI 咨询接口，表单提交
//    @FormUrlEncoded
//    @POST("/ai/index")
//    suspend fun reqAiIndexData(
//        @FieldMap param: Map<String, @JvmSuppressWildcards Any>
//    ): Response<AiNewsResponse>
}