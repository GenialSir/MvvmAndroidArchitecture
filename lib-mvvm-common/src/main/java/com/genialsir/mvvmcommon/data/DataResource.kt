package com.genialsir.mvvmcommon.data

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/12
 */
sealed class DataResource<T>(val data: T? = null, val errorCode: Int? = null) {
    class Success<T>(data: T) : DataResource<T>(data)
    class Loading<T>(data: T? = null) : DataResource<T>(data)
    class Error<T>(errorCode: Int) : DataResource<T>(null, errorCode)

    override fun toString(): String {
        return when (this){
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorCode"
            is Loading<T> -> "Loading"
        }
    }
}