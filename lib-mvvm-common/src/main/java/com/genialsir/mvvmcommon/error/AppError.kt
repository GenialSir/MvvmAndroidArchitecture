package com.genialsir.mvvmcommon.error

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
class Error(val code: Int, val description: String) {
    constructor(exception: Exception) : this(
        code = DEFAULT_ERROR, description = exception.message
            ?: ""
    )
}

const val NO_INTERNET_CONNECTION = -1
const val NETWORK_ERROR = -2
const val DEFAULT_ERROR = -3
const val EMPTY_RESPONSE = -4
const val TIMEOUT_ERROR = -5
const val PARSE_ERROR = -6
const val SSL_ERROR = -7
const val UNKNOWN_HOST = -8
const val CONNECT_ERROR = -9
const val UNKNOWN_ERROR = -10
const val PASS_WORD_ERROR = -101
const val USER_NAME_ERROR = -102
const val CHECK_YOUR_FIELDS = -103
const val SEARCH_ERROR = -104
//无效Token
const val INVALID_TOKEN = 412