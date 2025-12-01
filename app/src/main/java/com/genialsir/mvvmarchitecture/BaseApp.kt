package com.genialsir.mvvmarchitecture

import android.app.Application
import com.genialsir.autosize.AutoSizeConfig
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
    }

    override fun onCreate() {
        super.onCreate()
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
    }

}
