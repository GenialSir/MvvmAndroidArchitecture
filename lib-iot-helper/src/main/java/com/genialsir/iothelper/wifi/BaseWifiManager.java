package com.genialsir.iothelper.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author genialsir@163.com (GenialSir) on 2024/1/15
 */
public abstract class BaseWifiManager implements IWifiManager {

    private static final String TAG = BaseWifiManager.class.getSimpleName();

    static final int WIFI_STATE_DISABLED = 1;
    static final int WIFI_STATE_DISABLING = 2;
    static final int WIFI_STATE_ENABLING = 3;
    static final int WIFI_STATE_ENABLED = 4;
    static final int WIFI_STATE_UNKNOWN = 5;
    static final int WIFI_STATE_MODIFY = 6;
    static final int WIFI_STATE_CONNECTED = 7;
    static final int WIFI_STATE_UNCONNECTED = 8;


    WifiManager manager;
    List<IWifi> wifiData;
    OnWifiChangeListener onWifiChangeListener;
    OnWifiConnectListener onWifiConnectListener;
    OnWifiStateChangeListener onWifiStateChangeListener;
    WifiReceiver wifiReceiver;
    Context context;

    //已连接的状态，用来防止多次成功连接的消息发送。
    private boolean connectedStatus = false;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_STATE_DISABLED:
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onStateChanged(State.DISABLED);
                    }
                    break;
                case WIFI_STATE_DISABLING:
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onStateChanged(State.DISABLING);
                    }
                    break;
                case WIFI_STATE_ENABLING:
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onStateChanged(State.ENABLING);
                    }
                    break;
                case WIFI_STATE_ENABLED:
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onStateChanged(State.ENABLED);
                    }
                    break;
                case WIFI_STATE_UNKNOWN:
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onStateChanged(State.UNKNOWN);
                    }
                    break;
                case WIFI_STATE_MODIFY:
                    if (onWifiChangeListener != null) {
                        onWifiChangeListener.onWifiChanged(wifiData);
                    }
                    break;
                case WIFI_STATE_CONNECTED:
                    if (onWifiConnectListener != null) {
                        WifiInfo wifiInfo = (WifiInfo) msg.obj;
                        IWifi iWifi = Wifi.create(true, wifiInfo);
                        onWifiConnectListener.onConnectChanged(true, iWifi);
                    }
                    break;
                case WIFI_STATE_UNCONNECTED:
                    if (onWifiConnectListener != null) {
                        WifiInfo wifiInfo = (WifiInfo) msg.obj;
                        IWifi iWifi = Wifi.create(false, wifiInfo);
                        onWifiConnectListener.onConnectChanged(false, iWifi);
                    }
                    break;
            }
        }
    };

    BaseWifiManager(Context context) {
        this.context = context;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiData = new ArrayList<>();
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, filter);
    }

    @Override
    public void destroy() {
        context.unregisterReceiver(wifiReceiver);
        handler.removeCallbacksAndMessages(null);
        manager = null;
        wifiData = null;
        context = null;
    }

    @Override
    public void setOnWifiChangeListener(OnWifiChangeListener onWifiChangeListener) {
        this.onWifiChangeListener = onWifiChangeListener;
    }

    @Override
    public void setOnWifiConnectListener(OnWifiConnectListener onWifiConnectListener) {
        this.onWifiConnectListener = onWifiConnectListener;
    }

    @Override
    public void setOnWifiStateChangeListener(OnWifiStateChangeListener onWifiStateChangeListener) {
        this.onWifiStateChangeListener = onWifiStateChangeListener;
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            Log.d("WifiReceiver", "WifiReceiver onReceive " + action);


            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                int what = 0;
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        what = WIFI_STATE_DISABLED;
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        what = WIFI_STATE_DISABLING;
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        what = WIFI_STATE_ENABLING;
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        scanWifi();
                        what = WIFI_STATE_ENABLED;
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        what = WIFI_STATE_UNKNOWN;
                        break;
                }
                handler.sendEmptyMessage(what);
            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                boolean isUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (isUpdated) {
                    modifyWifi();
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info == null) {
                    Log.d(TAG, "================info is null================");
                    return;
                }
                Log.d(TAG, "================info is ================" + info);
                NetworkInfo.DetailedState state = info.getDetailedState();
                String SSID = info.getExtraInfo();
                if (TextUtils.isEmpty(SSID)) {
                    Log.w(TAG, "================TextUtils.isEmpty(SSID) is ================" + SSID);
                }
                if (state == NetworkInfo.DetailedState.IDLE) {
                } else if (state == NetworkInfo.DetailedState.SCANNING) {
                } else if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
                    modifyWifi(SSID, "身份验证中...");
                    Log.d(TAG, "================身份验证中================");
                } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    modifyWifi(SSID, "获取地址信息...");
                    Log.d(TAG, "================获取地址信息================");
                } else if (state == NetworkInfo.DetailedState.CONNECTED) {
                    WifiInfo wifiInfo = manager.getConnectionInfo();
                    if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
                        if (!wifiInfo.getSSID().equals("<unknown ssid>") && !connectedStatus) {
                            Log.d(TAG, wifiInfo.getSSID() + ": 已连接");
                            connectedStatus = true;
                            modifyWifi(SSID, "已连接");
                            //发送已连接的WIFI信息。
                            Message message = new Message();
                            message.what = WIFI_STATE_CONNECTED;
                            message.obj = wifiInfo;
                            handler.sendMessage(message);
                        }
                    } else {
                        Log.w(TAG, "CONNECTED wifiInf SSID: " + wifiInfo.getSSID());
                    }

                } else if (NetworkInfo.State.CONNECTING == info.getState()) {
                    connectedStatus = false;
                    Log.d(TAG, "================正在连接================");
                } else if (state == NetworkInfo.DetailedState.SUSPENDED) {
                    modifyWifi(SSID, "连接中断");
                    Log.d(TAG, "================连接中断================");
                } else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
                    modifyWifi(SSID, "断开中...");
                    Log.d(TAG, "================断开中================");
                } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
//                    modifyWifi(SSID, "已断开");
                    modifyWifi();
                    WifiInfo wifiInfo = manager.getConnectionInfo();
                    //发送已断开连接的WIFI信息。
                    Message message = new Message();
                    message.what = WIFI_STATE_UNCONNECTED;
                    message.obj = wifiInfo;
                    handler.sendMessage(message);
                    Log.d(TAG, "================已断开================");
                } else if (state == NetworkInfo.DetailedState.FAILED) {
                    modifyWifi(SSID, "连接失败");
                    Log.d(TAG, "================连接失败================");
                } else if (state == NetworkInfo.DetailedState.BLOCKED) {
                    modifyWifi(SSID, "wifi无效");
                    Log.d(TAG, "================wifi无效================");
                } else if (state == NetworkInfo.DetailedState.VERIFYING_POOR_LINK) {
                    modifyWifi(SSID, "信号差");
                    Log.d(TAG, "================信号差================");
                } else if (state == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
                    modifyWifi(SSID, "强制登陆门户");
                    Log.d(TAG, "================强制登陆门户================");
                }
            } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                String SSID = "";
                if (info != null) {
                    SSID = info.getExtraInfo();
                }
                if (TextUtils.isEmpty(SSID)) {
                    SSID = "NULL";
                }
                int code = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if (code == WifiManager.ERROR_AUTHENTICATING) {
                    //这个系统广播状态直接根据ERROR_AUTHENTICATING无法确定所连接WIFI是否密码输入正确，
                    //需要再进一步进行detailedState的区分。
                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    NetworkInfo.DetailedState detailedState = WifiInfo.getDetailedStateOf(supplicantState);
                    if (detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
                        modifyWifi(SSID, "密码错误");
                        Log.e(TAG, SSID + "================ERROR_AUTHENTICATING DISCONNECTED 密码错误================");
                        handler.sendEmptyMessage(WIFI_STATE_DISABLING);
                    } else if (detailedState == NetworkInfo.DetailedState.SCANNING) {
                        Log.d(TAG, SSID + "================ERROR_AUTHENTICATING SCANNING 密码正确================");
                    } else {
                        Log.w(TAG, SSID + "detailedState: " + detailedState);
                    }

                } else {
                    modifyWifi(SSID, "身份验证出现问题");
                    Log.d(TAG, "================身份验证出现问题================");
                }
            }
        }
    }

    protected void modifyWifi() {
        synchronized (BaseWifiManager.class) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Location permission denied. Feature: [ACCESS_FINE_LOCATION], Context: " + context.getPackageName());
                return;
            }
            List<ScanResult> results = manager.getScanResults();
            List<IWifi> wifiList = new LinkedList<>();
            List<IWifi> mergeList = new ArrayList<>();

            @SuppressLint("MissingPermission")
            List<WifiConfiguration> configurations = manager.getConfiguredNetworks();
            String connectedSSID = manager.getConnectionInfo().getSSID();
            int ipAddress = manager.getConnectionInfo().getIpAddress();
            for (ScanResult result : results) {
                IWifi mergeObj = Wifi.create(result, configurations, connectedSSID, ipAddress);
                if (mergeObj == null) continue;
                mergeList.add(mergeObj);
            }
            mergeList = WifiHelper.removeDuplicate(mergeList);
            for (IWifi merge : mergeList) {
                boolean isMerge = false;
                for (IWifi wifi : wifiData) {
                    if (wifi.equals(merge)) {
                        wifiList.add(wifi.merge(merge));
                        isMerge = true;
                    }
                }
                if (!isMerge)
                    wifiList.add(merge);
            }
            wifiData.clear();
            wifiData.addAll(wifiList);
            handler.sendEmptyMessage(WIFI_STATE_MODIFY);
        }
    }

    protected void modifyWifi(String SSID, String state) {
        if (TextUtils.isEmpty(SSID)) {
            Log.w(TAG, "modifyWifi ssid: " + SSID);
            return;
        }
        synchronized (BaseWifiManager.class) {
            List<IWifi> wifiList = new ArrayList<>();
            for (IWifi wifi : wifiData) {
                if (SSID.equals(wifi.SSID())) {
                    wifi.state(state);
                    wifiList.add(0, wifi);
                } else {
                    wifi.state(null);
                    wifiList.add(wifi);
                }
            }
            wifiData.clear();
            wifiData.addAll(wifiList);
            handler.sendEmptyMessage(WIFI_STATE_MODIFY);
        }
    }
}
