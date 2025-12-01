package com.genialsir.mvvmcommon.error.mapper

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
interface ErrorMapperSource {
    fun getErrorString(errorId: Int): String

    val errorMap: Map<Int, String>
}