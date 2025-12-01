package com.genialsir.mvvmcommon.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/12
 */

fun <T> LifecycleOwner.observeKt(
    liveData: LiveData<T>,
    action: (t: T) -> Unit) {
    liveData.observe(this, Observer { it?.let { t -> action(t) } })
}


fun <T> LifecycleOwner.observeEventKt(
    liveData: LiveData<SingleEvent<T>>,
    action: (t: SingleEvent<T>) -> Unit
) {
    liveData.observe(this, Observer { it?.let { t -> action(t) } })
}