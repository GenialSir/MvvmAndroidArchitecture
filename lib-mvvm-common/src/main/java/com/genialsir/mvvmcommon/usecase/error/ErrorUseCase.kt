package com.genialsir.mvvmcommon.usecase.error

import com.genialsir.mvvmcommon.error.Error

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}