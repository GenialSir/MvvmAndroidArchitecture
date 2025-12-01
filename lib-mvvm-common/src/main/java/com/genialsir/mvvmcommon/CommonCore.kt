package com.genialsir.mvvmcommon

import android.annotation.SuppressLint
import android.content.Context

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/18
 */
@SuppressLint("StaticFieldLeak")
object CommonCore {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun getContext(): Context = context
}
