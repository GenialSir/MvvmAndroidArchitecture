package com.genialsir.mvvmcommon.listener

import android.view.View

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/19
 */

/**
 * 高阶函数: 点击时间防抖（函数当参数、返回值、灵活传递逻辑，相当于把逻辑抽象出来，可以传递和复用）
 */
fun View.setOnIntervalClickListener(intervalTime: Long = 600, listener: (View) -> Unit){
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if(currentTime - lastClickTime >= intervalTime){
            lastClickTime = currentTime
            listener(it)
        }
    }
}
