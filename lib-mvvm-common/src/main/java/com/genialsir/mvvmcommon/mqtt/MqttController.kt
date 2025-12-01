package com.genialsir.mvvmcommon.mqtt

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList


/**
 * @author genialsir@163.com (GenialSir) on 2025/11/18
 * 全局 MQTT 管理器
 * 支持：
 * - 自动重连 & 重订阅
 * - Fragment/Activity recreate 安全
 * - Flow & LiveData 双模式监听
 */
object MqttController {

    private var onConnected: (() -> Unit)? = null

    /** 是否已连接 */
    @Volatile
    var isConnected = false
        private set

    /** 缓存已订阅 topic（用于自动恢复） */
    private val subscribedTopics = CopyOnWriteArrayList<String>()

    /** 等待 Service 启动完成的延迟操作 */
    private val pendingOps = mutableListOf<() -> Unit>()

    /** 用于状态 Flow */
    private val scope = CoroutineScope(Dispatchers.Default)

    /** MQTT 状态 Flow */
    private val _mqttStateFlow = MutableSharedFlow<Boolean>(replay = 1)
    val mqttStateFlow = _mqttStateFlow.asSharedFlow()

    /** 启动 MQTT Service */
    fun start(context: Context, onConnected: () -> Unit = {}) {
        this.onConnected = onConnected
        MqttService.start(context)
    }

    /** Service ready */
    internal fun notifyServiceReady() {
        synchronized(pendingOps) {
            pendingOps.forEach { it.invoke() }
            pendingOps.clear()
        }
    }

    /** 连接成功回调 */
    internal fun notifyConnected() {
        isConnected = true
        emitMqttState(true)
        onConnected?.invoke()

        // 自动恢复所有订阅
        subscribedTopics.forEach { topic ->
            ServiceBridge.service?.subscribe(topic)
        }
    }

    /** 连接断开 */
    internal fun notifyDisconnected() {
        isConnected = false
        emitMqttState(false)
    }

    /**
     * 带订阅成功/失败回调的 Subscribe
     */
    fun subscribe(
        topic: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        qos: Int = 1
    ) {
        if (!subscribedTopics.contains(topic)) subscribedTopics.add(topic)

        val op: () -> Unit = {
            ServiceBridge.service?.subscribe(topic, qos, onSuccess, onError)
        }

        if (ServiceBridge.service == null) pendingOps.add(op)
        else op()
    }

    /** 发布 */
    fun publish(topic: String, msg: String) {
        val op: () -> Unit = { ServiceBridge.service?.publish(topic, msg) }

        if (ServiceBridge.service == null) {
            pendingOps.add(op)
        } else {
            op()
        }
    }

    /** LiveData 监听 */
    fun observe(topic: String): LiveData<String> = MqttEventBus.observe(topic)

    /** 停止 Service */
    fun stop(context: Context) {
        subscribedTopics.clear()
        isConnected = false
        MqttService.stop(context)
    }

    /** Flow Emit */
    private fun emitMqttState(isConnected: Boolean) {
        scope.launch {
            _mqttStateFlow.emit(isConnected)
        }
    }

    /** 取消订阅 */
    fun unsubscribe(topic: String) {
        subscribedTopics.remove(topic)

        val op: () -> Unit = { ServiceBridge.service?.unsubscribe(topic) }

        if (ServiceBridge.service == null) {
            pendingOps.add(op)
        } else {
            op()
        }

        MqttEventBus.clear(topic)
    }

}
