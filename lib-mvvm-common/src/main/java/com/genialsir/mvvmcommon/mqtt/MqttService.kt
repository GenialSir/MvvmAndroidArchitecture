package com.genialsir.mvvmcommon.mqtt

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.genialsir.mvvmcommon.R
import com.genialsir.mvvmcommon.util.LogUtil
import org.eclipse.paho.client.mqttv3.*

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/18
 * MqttAsyncClient的封装Service
 */
class MqttService : Service() {

    private val TAG = MqttService::class.java.toString()

    companion object {
        private const val CHANNEL_ID = "mqtt_service_channel"
        private const val NOTIFICATION_ID = 2001

        fun start(context: Context) {
            val intent = Intent(context, MqttService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else
                context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MqttService::class.java))
        }
    }

    private var mqttClient: MqttAsyncClient? = null
    private val clientId = "android_client_${System.currentTimeMillis()}"

    override fun onCreate() {
        super.onCreate()
        ServiceBridge.service = this

        initNotification()
        startForeground(NOTIFICATION_ID, buildNotification())

        MqttController.notifyServiceReady()

        connectMqtt()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        ServiceBridge.service = null
        mqttClient?.disconnect()
        mqttClient = null
    }

    private fun connectMqtt() {
        mqttClient = MqttAsyncClient(MqttConfig.SERVER_URI, clientId, null)
        val options = MqttConnectOptions().apply {
            userName = MqttConfig.USERNAME
            password = MqttConfig.PASSWORD.toCharArray()
            isAutomaticReconnect = true
            keepAliveInterval = MqttConfig.KEEP_ALIVE
            connectionTimeout = MqttConfig.CONNECTION_TIMEOUT
            isCleanSession = true
        }

        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                LogUtil.e(TAG, "连接断开：${cause?.message}")
                MqttController.notifyDisconnected()
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.v(TAG, "topic $topic, messageArrived message: $message")

                val msg = message?.toString() ?: return
                MqttEventBus.post(topic ?: return, msg)

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        mqttClient?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                LogUtil.i(TAG, "连接成功")
                MqttController.notifyConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                LogUtil.e(TAG, "连接失败：${exception?.message}")
                MqttController.notifyDisconnected()
            }
        })
    }

    fun publish(topic: String, msg: String, qos: Int = 1) {
        val message = MqttMessage(msg.toByteArray()).apply { this.qos = qos }
        mqttClient?.publish(topic, message)
    }

    /**
     * 带订阅成功/失败回调
     */
    fun subscribe(
        topic: String,
        qos: Int = 1,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        try {
            if (mqttClient?.isConnected != true) {
                val e = IllegalStateException("MQTT 未连接，无法订阅")
                onError?.invoke(e)
                LogUtil.e(TAG, e.message ?: "")
                return
            }

            mqttClient?.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    LogUtil.i(TAG, "订阅成功：$topic")
                    onSuccess?.invoke()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    LogUtil.e(TAG, "订阅失败：$topic -> ${exception?.message}")
                    onError?.invoke(exception ?: Exception("Unknown subscribe error"))
                }
            })
        } catch (e: Exception) {
            LogUtil.e(TAG, "Subscribe 调用崩溃：${e.message}")
            onError?.invoke(e)
        }
    }


    fun unsubscribe(topic: String) {
        try {
            mqttClient?.unsubscribe(topic)
            LogUtil.i(TAG, "已取消订阅：$topic")
        } catch (e: Exception) {
            LogUtil.e(TAG, "取消订阅失败：$topic -> ${e.message}")
        }
    }

    // ----------------- Foreground ------------------
    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MQTT Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MQTT 已连接")
            .setContentText("后台运行中")
            .setSmallIcon(R.drawable.ic_logo_smart_care)
            .setOngoing(true)
            .build()
    }
}
