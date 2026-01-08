package com.genialsir.mvvmarchitecture

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.genialsir.autosize.AutoSizeConfig
import com.genialsir.iothelper.IotHelperConfig
import com.genialsir.iothelper.mqtt.MqttConfig
import com.genialsir.mvvmcommon.util.LogHelper
import com.genialsir.mvvmcommon.CommonCore
import com.genialsir.mvvmcommon.util.AppCrashHandler
import com.genialsir.mvvmcommon.util.FontManager
import dagger.hilt.android.HiltAndroidApp

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
@HiltAndroidApp
class BaseApp : Application() {

    //增加上下文的对外获取
    companion object {
        lateinit var context: Application
            private set
        var currentActivity: Activity? = null
    }


    override fun onCreate() {
        super.onCreate()
        context = this
        // 注册生命周期回调
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)

        //Java全局异常捕获
        AppCrashHandler.getInstance().init(this, true, "MvvmArchitecture")
        //初始化CommonCore
        CommonCore.init(this)
        //初始化日志
        LogHelper.init(this)
        //初始化字体切换工具类
        FontManager.init(this)
        //让屏幕适配逻辑忽略系统字体缩放设置
        AutoSizeConfig.getInstance().setExcludeFontScale(true)
        //初始化Mqtt的配置信息
        MqttConfig.initialize(
            IotHelperConfig.Mqtt.EMQX_MQTT_SERVER_URI,
            "",
            ""
        )
    }

    //生命周期回调
    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {

        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?
        ) {
            currentActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }
        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) {}

        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity == activity) currentActivity = null
        }
    }

}
