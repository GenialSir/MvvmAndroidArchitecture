package com.genialsir.mvvmcommon.util

import android.content.Context
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.Flattener2
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.ConsolePrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.genialsir.mvvmcommon.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/18
 */
object LogHelper {

    // 日志文件相关常量
    private const val LOG_FILE_MAX_SIZE = 5 * 1024 * 1024 // *MB
    private const val LOG_FILE_MAX_BACKUP = 5 // 最多保留5个备份
    private const val LOG_FILE_KEEP_DAYS = 7 // 保留天数
    private const val LOG_FILE_KEEP_MILLIS = LOG_FILE_KEEP_DAYS * 24 * 60 * 60 * 1000L

    fun init(context: Context) {
        //Debug 全量日志，Release 只打印 ERROR
//        val logLevel = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.ERROR
        val logLevel = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.ALL

        val config = LogConfiguration.Builder()
            .logLevel(logLevel)
            .tag("GENIAL_TAG")
            .enableThreadInfo()
            // 调用栈根据环境选择
            .apply {
                if (BuildConfig.DEBUG) {
                    enableStackTrace(2) // 测试环境开启，显示2层调用栈
                } else {
                    disableStackTrace() // 线上关闭调用栈
                }
            }
            .enableBorder()
            .build()

        // 可读时间的 Flattener
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val customFlattener = Flattener2 { timeMillis, logLevel, tag, message ->
            val timeStr = dateFormat.format(Date(timeMillis))
            "$timeStr | ${LogLevel.getLevelName(logLevel)} | $tag | $message"
        }

        //Android Logcat
        val androidPrinter = AndroidPrinter(BuildConfig.DEBUG) // Release 不打印Logcat
        //控制台
        val consolePrinter = ConsolePrinter()
        //文件日志
        val filePrinter = FilePrinter.Builder(context.filesDir.absolutePath + "/logs")
//            .fileNameGenerator(DateFileNameGenerator())
            .fileNameGenerator(object : DateFileNameGenerator() {
                override fun generateFileName(logLevel: Int, timestamp: Long): String {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    return sdf.format(java.util.Date(timestamp)) + ".log"
                }
            })
//            .backupStrategy(NeverBackupStrategy())
            .backupStrategy(
                FileSizeBackupStrategy2(
                    LOG_FILE_MAX_SIZE.toLong(),
                    LOG_FILE_MAX_BACKUP
                )
            ) // 每个文件 1MB，最多保留 5 个备份
            .cleanStrategy(FileLastModifiedCleanStrategy(LOG_FILE_KEEP_MILLIS)) // 保留7天
            .flattener(customFlattener)//使用自定义格式
            .build()

        XLog.init(config, androidPrinter, filePrinter)
    }
}
