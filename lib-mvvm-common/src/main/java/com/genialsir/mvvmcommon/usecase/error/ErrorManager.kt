package com.genialsir.mvvmcommon.usecase.error

import com.genialsir.mvvmcommon.error.Error
import com.genialsir.mvvmcommon.error.mapper.ErrorMapper
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
class ErrorManager @Inject constructor(private val errorMapper: ErrorMapper): ErrorUseCase {
    override fun getError(errorCode: Int): Error {
        return Error(
            code = errorCode,
            description = errorMapper.errorMap.getValue(errorCode)
        )
    }
}
