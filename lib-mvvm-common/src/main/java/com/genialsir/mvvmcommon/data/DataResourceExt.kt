package com.genialsir.mvvmcommon.data

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/29
 */
// 透传：有时调用 processCall 已经返回 DataResource<T>，直接返回即可
inline fun <reified T> DataResource<T>.toResource(): DataResource<T> {
    return when (this) {
        is DataResource.Success -> this
        is DataResource.Error -> this
        is DataResource.Loading -> DataResource.Loading(data)
    }
}

// 映射：把 DataResource<A> 转成 DataResource<B>
inline fun <A, B> DataResource<A>.mapToResource(transform: (A) -> B): DataResource<B> {
    return when (this) {
        is DataResource.Success -> DataResource.Success(transform(this.data!!))
        is DataResource.Error -> DataResource.Error(this.errorCode ?: -1)
        is DataResource.Loading -> DataResource.Loading()
    }
}
