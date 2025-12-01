package com.genialsir.mvvmcommon.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import kotlin.collections.joinToString
import kotlin.text.format

/**
 * @author genialsir@163.com (GenialSir) on 2025/11/27
 */
object AppUtil {

    /**
     * FLAG_ACTIVITY_NEW_TASK：启动一个新的任务
     * FLAG_ACTIVITY_CLEAR_TASK：清空这个任务栈中所有旧 Activity
     * 效果：启动新的根 Activity，返回键不会回到旧 Activity
     */
    public fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    /**
     * 获取 App 版本名称（versionName）
     */
    fun getVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取 App 版本号（versionCode）
     */
    fun getVersionCode(context: Context): Long {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 获取包名
     */
    fun getPackageName(context: Context): String {
        return context.packageName
    }

    /**
     * 获取应用显示名称
     */
    fun getAppName(context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 判断是否是 Debug 包
     */
    fun isDebug(context: Context): Boolean {
        return try {
            val applicationInfo = context.applicationInfo
            (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取应用签名 SHA1（可用于第三方 SDK / 调试）
     */
    fun getSHA1(context: Context): String {
        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val pi = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                pi.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures
            }

            val cert = signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA1")
            val digest = md.digest(cert)
            digest.joinToString(":") { String.format("%02X", it) }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取 AndroidManifest 中自定义 meta-data（常用于渠道号）
     */
    fun getMetaData(context: Context, key: String): String? {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData?.getString(key)
        } catch (e: Exception) {
            null
        }
    }

}