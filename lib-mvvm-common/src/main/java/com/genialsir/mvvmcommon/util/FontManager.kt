package com.genialsir.mvvmcommon.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics

/**
 * @author genialsir@163.com (GenialSir) on 2025/10/15
 * 字体管理器：用于动态调整应用字体大小
 */
object FontManager {
    private const val PREFS = "settings"
    private const val KEY_FONT_SCALE = "fontScale"

    private var prefs: SharedPreferences? = null

    var scale: Float = 1.0f
        private set

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        scale = prefs?.getFloat(KEY_FONT_SCALE, 1.0f) ?: 1.0f
    }

    fun setScale(context: Context, newScale: Float) {
        if (newScale <= 0f) return
        prefs ?: init(context)
        prefs?.edit()?.putFloat(KEY_FONT_SCALE, newScale)?.apply()
        scale = newScale
    }

    /**
     * 包装一个 Context，使其带上当前字体比例
     */
    fun wrapContext(base: Context): Context {
        val config = Configuration(base.resources.configuration)
        if (config.fontScale != scale) {
            config.fontScale = scale
            return base.createConfigurationContext(config)
        }
        return base
    }

    //只需在BaseActivity中调用即可，Fragment使用的 context 都来自 Activity，所以会自动继承该字体缩放。
    fun applyNow(context: Context) {
        applyToResources(context.resources)
    }

    /**
     * 强制同步当前 scale 到 Resources
     */
    fun applyToResources(res: Resources) {
        val config = Configuration(res.configuration)
        if (config.fontScale != scale) {
            config.fontScale = scale
        }
        val metrics: DisplayMetrics = res.displayMetrics
        val expectedScaledDensity = metrics.density * scale
        @Suppress("DEPRECATION")
        if (metrics.scaledDensity != expectedScaledDensity) {
            metrics.scaledDensity = expectedScaledDensity
            res.updateConfiguration(config, metrics)
        }
    }

}