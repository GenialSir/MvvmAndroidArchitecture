package com.genialsir.iothelper.mqtt

import com.genialsir.iothelper.IotHelperConfig

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/18
 * MQTT 配置统一管理
 */
class MqttConfig private constructor() {
    var serverUri: String = DEFAULT_SERVER_URI
        private set
    var username: String = DEFAULT_USERNAME
        private set
    var password: String = DEFAULT_PASSWORD
        private set
    var keepAlive: Int = DEFAULT_KEEP_ALIVE
        private set
    var connectionTimeout: Int = DEFAULT_CONNECTION_TIMEOUT
        private set

    companion object {
        private const val DEFAULT_SERVER_URI = IotHelperConfig.Mqtt.EMQX_MQTT_SERVER_URI
        private const val DEFAULT_USERNAME = ""
        private const val DEFAULT_PASSWORD = ""
        private const val DEFAULT_KEEP_ALIVE = IotHelperConfig.Mqtt.KEEP_ALIVE
        private const val DEFAULT_CONNECTION_TIMEOUT = IotHelperConfig.Mqtt.CONNECTION_TIMEOUT

        // 实例
        private var _instance: MqttConfig? = null
        val instance: MqttConfig
            get() = _instance ?: MqttConfig().also { _instance = it }

        /**
         * 初始化配置（通常在应用启动时调用一次）
         */
        fun initialize(
            serverUri: String = DEFAULT_SERVER_URI,
            username: String = DEFAULT_USERNAME,
            password: String = DEFAULT_PASSWORD,
            keepAlive: Int = DEFAULT_KEEP_ALIVE,
            connectionTimeout: Int = DEFAULT_CONNECTION_TIMEOUT
        ) {
            (_instance ?: MqttConfig()).apply {
                this.serverUri = serverUri
                this.username = username
                this.password = password
                this.keepAlive = keepAlive
                this.connectionTimeout = connectionTimeout
                _instance = this
            }
        }
    }
}
