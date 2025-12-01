package com.genialsir.mvvmcommon.error.mapper

import android.content.Context
import com.genialsir.mvvmcommon.R
import com.genialsir.mvvmcommon.error.CHECK_YOUR_FIELDS
import com.genialsir.mvvmcommon.error.NETWORK_ERROR
import com.genialsir.mvvmcommon.error.NO_INTERNET_CONNECTION
import com.genialsir.mvvmcommon.error.PASS_WORD_ERROR
import com.genialsir.mvvmcommon.error.SEARCH_ERROR
import com.genialsir.mvvmcommon.error.USER_NAME_ERROR
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
class ErrorMapper @Inject constructor(@ApplicationContext val context: Context) : ErrorMapperSource {
    override fun getErrorString(errorId: Int): String {
        return context.getString(errorId)
    }

    override val errorMap: Map<Int, String>
        get() = mapOf(
            Pair(NO_INTERNET_CONNECTION, getErrorString(R.string.no_internet)),
            Pair(NETWORK_ERROR, getErrorString(R.string.network_error)),
            Pair(PASS_WORD_ERROR, getErrorString(R.string.invalid_password)),
            Pair(USER_NAME_ERROR, getErrorString(R.string.invalid_username)),
            Pair(CHECK_YOUR_FIELDS, getErrorString(R.string.invalid_username_and_password)),
            Pair(SEARCH_ERROR, getErrorString(R.string.search_error))
            ).withDefault { getErrorString(R.string.network_error) }

}

