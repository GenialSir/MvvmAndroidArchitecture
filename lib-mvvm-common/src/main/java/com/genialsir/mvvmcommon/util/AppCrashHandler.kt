package com.genialsir.mvvmcommon.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter


/**
 * @author genialsir@163.com (GenialSir) on 2025/9/24
 * CrashGuard
 *
 * 全局应用崩溃处理器：
 * 1. 捕获未处理的异常
 * 2. 弹出提示 Toast
 * 3. 保存崩溃日志到本地文件
 * 4. 可选重启应用
 */
class AppCrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private val TAG = AppCrashHandler::class.java.simpleName

    //系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    //应用上下文
    private var mContext: Context? = null

    //日志存储目录
    private var logDirection: String? = ""

    //是否允许重启应用
    private var allowRestart = true

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: AppCrashHandler? = null


        fun getInstance(): AppCrashHandler =
            instance ?: synchronized(AppCrashHandler::class.java) {
                instance ?: AppCrashHandler().also { instance = it }
            }

//        fun getInstance(): AppCrashHandler {
//            if (instance == null) {
//                synchronized(AppCrashHandler::class.java) {
//                    if (instance == null) {
//                        instance = AppCrashHandler()
//                    }
//                }
//            }
//            return instance!!
//        }
    }

    fun init(ctx: Context, allowRestart: Boolean, logDirectory: String){
        this.mContext = ctx
        this.allowRestart = allowRestart
        this.logDirection = logDirectory
        setUpHandler()
    }

    /**
     * 设置为默认未捕获异常处理器
     */
    private fun setUpHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 捕获未处理的异常
     */
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if(!handleException(throwable) && mDefaultHandler != null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(thread, throwable)
        }else{
            //延迟Toast现实
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            //退出程序
            if(allowRestart){
                restartApp()
            }
        }
    }

    /**
     * 重启应用
     */
    private fun restartApp() {
        mContext?.let {
            SystemControl.resetApp(it)
        }
    }

    /**
     * 自定义异常处理
     */
    private fun handleException(throwable: Throwable?): Boolean {
        if(throwable == null){
            return false
        }
        Thread{
            Looper.prepare()
            mContext?.let {
                val toast = Toast.makeText(it, "程序异常，即将重启", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            Looper.loop()
        }.start()

        CoroutineScope(Dispatchers.IO).launch {
            //保存异常日志
            saveCrashInfoToFile(throwable)
        }
        return true
    }

    /**
     * 保存异常日志到文件
     */
    private fun saveCrashInfoToFile(throwable: Throwable) {
        val writer = StringWriter().also{ sw ->
            PrintWriter(sw).use { pw ->
                throwable.printStackTrace(pw)
                var cause: Throwable? = throwable.cause
                while (cause != null){
                    cause.printStackTrace(pw)
                    cause = cause.cause
                }

            }
        }

        val fileName = "crash-${timeStampData().replace(" ", "-").replace(":", "-")}.log"

        // 判断SD卡是否可正常使用
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            val path = Environment.getExternalStorageDirectory().path + "/" + logDirection + "/"
            val dir = File(path)
            if(!dir.exists()){
                dir.mkdirs()
            }
            try{
                FileOutputStream(File(path, fileName)).use { fos ->
                    fos.write(writer.toString().toByteArray())
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun timeStampData(): String{
        return SystemControl.getTimeStampDate()
    }

}