package com.genialsir.mvvmcommon.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import kotlin.text.isNullOrEmpty

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/18
 */
object ToastUtil {

    private var toast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 显示短Toast
     */
    fun showShort(context: Context, msg: String?) {
        show(context, msg, Toast.LENGTH_SHORT)
    }

    /**
     * 显示长Toast
     */
    fun showLong(context: Context, msg: String?) {
        show(context, msg, Toast.LENGTH_LONG)
    }

    /**
     * 显示Toast（自动切主线程 + 避免多个Toast叠加）
     */
    fun show(context: Context, msg: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (msg.isNullOrEmpty()) return

        mainHandler.post {
            // 复用 Toast，避免重复弹出
            if (toast == null) {
                toast = Toast.makeText(context.applicationContext, msg, duration)
            } else {
                toast?.setText(msg)
                toast?.duration = duration
            }
            toast?.show()
        }
    }

    /**
     * 居中显示Toast
     */
    fun showCenter(context: Context, msg: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (msg.isNullOrEmpty()) return

        mainHandler.post {
            if (toast == null) {
                toast = Toast.makeText(context.applicationContext, msg, duration)
            } else {
                toast?.setText(msg)
                toast?.duration = duration
            }
            toast?.setGravity(Gravity.CENTER, 0, 0)
            toast?.show()
        }
    }

    /**
     * 取消Toast
     */
    fun cancel() {
        toast?.cancel()
    }
}
