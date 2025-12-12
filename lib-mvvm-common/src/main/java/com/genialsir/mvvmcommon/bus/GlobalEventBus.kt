package com.genialsir.mvvmcommon.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @author genialsir@163.com (GenialSir) on 2025/12/10
 */
object GlobalEventBus {
    // replay = 0 -> 不会重复发送历史事件
    private val _tokenInvalidEvent = MutableSharedFlow<Unit>(replay = 0)
    val tokenInvalidEvent = _tokenInvalidEvent.asSharedFlow()

    suspend fun sendTokenInvalid() {
        _tokenInvalidEvent.emit(Unit)
    }
}