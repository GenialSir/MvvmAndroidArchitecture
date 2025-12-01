package com.genialsir.mvvmcommon.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author genialsir@163.com (GenialSir) on 2022/11/3
 */
public class SystemControl {

    private static final String TAG = SystemControl.class.getSimpleName();

    //重启当前的APP
    public static void resetApp(Context context){
        // 重启应用
        context.startActivity(context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName()));
        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //重启系统
    public static void reboot(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        context.sendBroadcast(intent);
    }

    //恢复出厂设置
    public static void resetSystem(Context context) {
        Intent intent = new Intent("android.intent.action.FACTORY_RESET");
        intent.setPackage("android");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("android.intent.extra.REASON", "doFactoryReset");
        intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", false);
        intent.putExtra("android.intent.extra.EXTRA_WIPE_ESIMS", true);
        context.sendBroadcast(intent);
    }

    //关机
    public static void shutdownDevice(Context context) {
        try {
            PowerManager pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pManager != null) {
                Method method = pManager.getClass().getMethod("shutdown",
                        boolean.class, String.class, boolean.class);
                method.invoke(pManager, false, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 设置系统时间
     * @param context 上下文
     * @param timestamp 时间戳，需要到毫秒级别（1519948818000）
     */
    public static void setSystemTime(Context context, String timestamp) {
        setSystemTime(context, timestamp, false, false);
    }

    /**
     * 配置系统时间
     * @param context 上下文
     * @param timestamp 时间戳，需要到毫秒级别（1519948818000）
     * @param autoTimeStatus 系统设置中的“自动更新时间”选项，false关闭，true打开。
     * @param autoTimeZoneStatus 系统设置中的“自动更新时区”选项，false关闭，true打开。
     */
    public static void setSystemTime(Context context, String timestamp, boolean autoTimeStatus,
                                     boolean autoTimeZoneStatus) {
        try {
            //秒级别的时间补3位
            if (timestamp.length() == 10) {
                timestamp += "000";
            }
            if (Settings.System.canWrite(context)) {
                //获取ContentResolver
                ContentResolver contentResolver = context.getContentResolver();
                //设置系统时间
                //这行代码将系统设置中的“自动更新时间”选项关闭。
                Settings.Global.putInt(contentResolver,
                        Settings.Global.AUTO_TIME, autoTimeStatus ? 1 : 0);
                //这行代码将系统设置中的“自动更新时区”选项关闭。
                Settings.Global.putInt(contentResolver,
                        Settings.Global.AUTO_TIME_ZONE, autoTimeZoneStatus ? 1 : 0);

                long newSystemTimeMillis = Long.parseLong(timestamp);
                //毫秒位数的时间戳
                SystemClock.setCurrentTimeMillis(newSystemTimeMillis);
                LogUtil.Companion.i(TAG, "同步系统时间成功，timestamp：" + timestamp);
            } else {
                LogUtil.Companion.e(TAG, "同步系统时间没有系统权限");
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.Companion.e(TAG, "setSystemTime error: " + e);
        }
    }

    public static String getTimeStampDate(){
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        return sdFormatter.format(nowTime);
    }
}
