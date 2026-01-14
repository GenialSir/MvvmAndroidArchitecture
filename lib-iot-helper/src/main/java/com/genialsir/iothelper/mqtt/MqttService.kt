package com.genialsir.iothelper.mqtt

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import org.eclipse.paho.client.mqttv3.*
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/18
 * MqttAsyncClient的封装Service
 * 添加订阅/发布等待队列，连接成功后统一处理，避免竞态
 * 避免重复 connect 调用（isConnecting）
 * 更稳健的断开/重连处理与资源释放
 */
class MqttService : Service() {

    private val TAG = MqttService::class.java.toString()

    companion object {
        private const val CHANNEL_ID = "mqtt_service_channel"
        private const val NOTIFICATION_ID = 2001
        private const val RECONNECT_DELAY_MS = 5_000L

        fun start(context: Context) {
            val intent = Intent(context, MqttService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MqttService::class.java))
        }
    }

    private var mqttClient: MqttAsyncClient? = null
    private val clientId = "android_client_${System.currentTimeMillis()}"

    // pending subscription / publish entry types
    private data class PendingSub(
        val topic: String,
        val qos: Int,
        val onSuccess: (() -> Unit)?,
        val onError: ((Throwable) -> Unit)?
    )

    private data class PendingPub(
        val topic: String,
        val msg: String,
        val qos: Int
    )

    // Queues for actions when client not yet connected
    private val pendingSubs = ArrayDeque<PendingSub>()
    private val pendingPubs = ArrayDeque<PendingPub>()

    // synchronization & state
    private val lock = Any()
    private val isConnecting = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        ServiceBridge.service = this

        initNotification()
        startForeground(NOTIFICATION_ID, buildNotification())

        MqttController.notifyServiceReady()

        connectMqtt()
    }

    override fun onBind(intent: Intent?): IBinder? = null


    private fun connectMqtt() {
        // Avoid concurrent connects
        if (isConnecting.get()) {
            Log.w(TAG, "已在连接中，忽略重复 connect 调用")
            return
        }

        synchronized(lock) {
            if (mqttClient == null) {
                try {
                    mqttClient = MqttAsyncClient(MqttConfig.instance.serverUri, clientId, null)
                } catch (e: Exception) {
                    Log.e(TAG, "创建 MqttAsyncClient 失败：${e.message}")
                    scheduleReconnect()
                    return
                }
            }
        }

        // If already connected, just return and let consumers proceed
        try {
            if (mqttClient?.isConnected == true) {
                Log.i(TAG, "MQTT 已连接，忽略 connect")
                return
            }
        } catch (e: Exception) {
            Log.w(TAG, "检查连接状态异常：${e.message}")
        }

        isConnecting.set(true)
        val options = MqttConnectOptions().apply {
            userName = MqttConfig.instance.username
            password = MqttConfig.instance.password.toCharArray()
            isAutomaticReconnect = true
            keepAliveInterval = MqttConfig.instance.keepAlive
            connectionTimeout = MqttConfig.instance.connectionTimeout
            // 推荐使用 false 以便服务端在重连后保留订阅（若需要持久订阅）
            isCleanSession = false
        }

        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.e(TAG, "连接断开：${cause?.message}")
                MqttController.notifyDisconnected()
                // 客户端自动重连时会触发 onSuccess，因此这里只做日志与状态
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.v(TAG, "topic $topic, messageArrived message: $message")
                val msg = message?.toString() ?: return
                MqttEventBus.post(topic ?: return, msg)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        try {
            mqttClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "连接成功")
                    isConnecting.set(false)
                    MqttController.notifyConnected()
                    processPendingSubscriptions()
                    processPendingPublishes()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "连接失败：${exception?.message}")
                    isConnecting.set(false)
                    MqttController.notifyDisconnected()
                    scheduleReconnect()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "connect 调用异常：${e.message}")
            isConnecting.set(false)
            scheduleReconnect()
        }
    }

    private fun scheduleReconnect() {
        // 如果启用了自动重连，可能不需要此项，但保底加入延迟重试
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            Log.i(TAG, "尝试重新连接 MQTT")
            connectMqtt()
        }, RECONNECT_DELAY_MS)
    }

    private fun processPendingSubscriptions() {
        val list = mutableListOf<PendingSub>()
        synchronized(lock) {
            while (pendingSubs.isNotEmpty()) {
                pendingSubs.poll()?.let { list.add(it) }

            }
        }
        list.forEach { pending ->
            try {
                mqttClient?.subscribe(
                    pending.topic,
                    pending.qos,
                    null,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.i(TAG, "订阅成功（队列处理）：${pending.topic}")
                            pending.onSuccess?.invoke()
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.e(
                                TAG,
                                "订阅失败（队列处理）：${pending.topic} -> ${exception?.message}"
                            )
                            pending.onError?.invoke(
                                exception ?: Exception("Unknown subscribe error")
                            )
                        }
                    })
            } catch (e: Exception) {
                Log.e(TAG, "队列订阅异常：${e.message}")
                pending.onError?.invoke(e)
            }
        }
    }

    private fun processPendingPublishes() {
        val list = mutableListOf<PendingPub>()
        synchronized(lock) {
            while (pendingPubs.isNotEmpty()) {
                pendingPubs.poll()?.let { list.add(it) }
            }
        }
        list.forEach { p ->
            try {
                val message = MqttMessage(p.msg.toByteArray()).apply { qos = p.qos }
                mqttClient?.publish(p.topic, message)
                Log.i(TAG, "发布（队列处理）: ${p.topic}")
            } catch (e: Exception) {
                Log.e(TAG, "队列发布异常：${e.message}")
                // 如果发布失败，可以决定是否重新入队；此处直接丢弃并记录
            }
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1) {
        try {
            if (mqttClient?.isConnected == true) {
                val message = MqttMessage(msg.toByteArray()).apply { this.qos = qos }
                mqttClient?.publish(topic, message)
            } else {
                // 入队等待连接
                synchronized(lock) {
                    pendingPubs.add(PendingPub(topic, msg, qos))
                }
                Log.i(TAG, "MQTT 未连接，发布已加入等待队列：$topic")
                connectMqtt()
            }
        } catch (e: Exception) {
            Log.e(TAG, "publish 异常：${e.message}")
        }
    }

    /**
     * 带订阅成功/失败回调
     * 如果当前未连接，改为放入等待队列（连接成功后自动订阅）
     */
    fun subscribe(
        topic: String,
        qos: Int = 1,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        try {
            if (mqttClient?.isConnected != true) {
                synchronized(lock) {
                    pendingSubs.add(PendingSub(topic, qos, onSuccess, onError))
                }
                Log.i(TAG, "MQTT 未连接，已加入等待队列：$topic")
                // 若未在连接中，触发连接
                connectMqtt()
                return
            }

            mqttClient?.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "订阅成功：$topic")
                    onSuccess?.invoke()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "订阅失败：$topic -> ${exception?.message}")
                    onError?.invoke(exception ?: Exception("Unknown subscribe error"))
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Subscribe 调用崩溃：${e.message}")
            onError?.invoke(e)
        }
    }

    fun unsubscribe(topic: String) {
        try {
            if (mqttClient?.isConnected == true) {
                mqttClient?.unsubscribe(topic)
                Log.i(TAG, "已取消订阅：$topic")
            } else {
                // 若尚未连接，则从待订阅队列中移除对应项
                synchronized(lock) {
                    val iter = pendingSubs.iterator()
                    while (iter.hasNext()) {
                        if (iter.next().topic == topic) {
                            iter.remove()
                        }
                    }
                }
                Log.i(TAG, "MQTT 未连接，已从等待队列移除订阅：$topic")
            }
        } catch (e: Exception) {
            Log.e(TAG, "取消订阅失败：$topic -> ${e.message}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        ServiceBridge.service = null
        try {
            mqttClient?.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "disconnect error: ${e.message}")
        }
        try {
            mqttClient?.close()
        } catch (e: Exception) {
            Log.w(TAG, "close client error: ${e.message}")
        }
        mqttClient = null
        synchronized(lock) {
            pendingSubs.clear()
            pendingPubs.clear()
        }
        handler.removeCallbacksAndMessages(null)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancel(
            NOTIFICATION_ID
        )
    }


    // ----------------- Foreground ------------------
    private fun initNotification() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "MQTT Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MQTT 已连接")
            .setContentText("后台运行中")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build()
    }
}
