package com.genialsir.mvvmcommon.util

import com.elvishew.xlog.XLog

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/17
 */
class LogUtil {
    companion object {

        fun v(msg: String) {
            XLog.v(msg)
        }

        fun d(msg: String) {
            XLog.d(msg)
        }

        fun i(msg: String) {
            XLog.i(msg)
        }

        fun w(msg: String) {
            XLog.w(msg)
        }

        fun e(msg: String) {
            XLog.e(msg)
        }

        fun v(tag: String, msg: String) {
            XLog.tag(tag).v(msg)
        }

        fun d(tag: String, msg: String) {
            XLog.tag(tag).d(msg)
        }

        fun i(tag: String, msg: String) {
            XLog.tag(tag).i(msg)
        }

        fun w(tag: String, msg: String) {
            XLog.tag(tag).w(msg)
        }

        fun e(tag: String, msg: String) {
            XLog.tag(tag).e(msg)
        }
    }
}
