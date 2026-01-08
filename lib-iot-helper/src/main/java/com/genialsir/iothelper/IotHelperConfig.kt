package com.genialsir.iothelper

/**
 * @author genialsir@163.com (GenialSir) on 2026/1/8
 */
object IotHelperConfig {
    //MQTT
    object Mqtt {
        // EMQX 官网：https://www.emqx.com/zh/cloud
        const val EMQX_MQTT_SERVER_URI = "tcp://broker.emqx.io:1883"
        const val EMQX_MQTT_SERVER_SSL_URI = "ssl://broker.emqx.io:8883"
        const val KEEP_ALIVE = 30
        const val CONNECTION_TIMEOUT = 10
    }

    //BLE
    object Ble {

    }

    //Wi-Fi
    object Wifi {

    }

}
