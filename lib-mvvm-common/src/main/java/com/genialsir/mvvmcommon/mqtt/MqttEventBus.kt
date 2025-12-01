package com.genialsir.mvvmcommon.mqtt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentHashMap

/**
 * @author genialsir@163.com (GenialSir) on 2025/10/27
 *  MQTT LiveData 事件总线
 *  使用 LiveData 监听 MQTT 消息，UI 层直接 observe
 */
object MqttEventBus {

    // 线程安全的 topic -> MutableLiveData 映射
    private val topicMap = ConcurrentHashMap<String, MutableLiveData<String>>()

    /**
     * 订阅 / 观察 topic
     * @param topic MQTT topic
     * @return LiveData，可直接 observe
     */
    fun observe(topic: String): LiveData<String> {
        return topicMap.getOrPut(topic) { MutableLiveData() }
    }

    /**
     * 发布消息到指定 topic
     * @param topic MQTT topic
     * @param message 消息内容
     */
    fun post(topic: String, message: String) {
        topicMap[topic]?.postValue(message)
    }

    /**
     * 清理指定 topic 的 LiveData（可选）
     */
    fun clear(topic: String) {
        topicMap.remove(topic)
    }

    /**
     * 清理所有 topic
     */
    fun clearAll() {
        topicMap.clear()
    }
}
