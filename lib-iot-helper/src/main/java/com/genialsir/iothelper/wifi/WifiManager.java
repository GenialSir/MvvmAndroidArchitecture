package com.genialsir.iothelper.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

/**
 * @author genialsir@163.com (GenialSir) on 2024/1/15
 */
public class WifiManager extends BaseWifiManager {

    private static final String TAG = WifiManager.class.getSimpleName();


    private WifiManager(Context context) {
        super(context);
    }

    public static IWifiManager create(Context context) {
        return new WifiManager(context);
    }

    @Override
    public boolean isOpened() {
        return manager.isWifiEnabled();
    }

    @Override
    public void openWifi() {
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }
    }

    @Override
    public void closeWifi() {
        if (manager.isWifiEnabled()) {
            manager.setWifiEnabled(false);
        }
    }

    @Override
    public void scanWifi() {
        manager.startScan();
    }

    @Override
    public boolean disConnectWifi() {
        return manager.disconnect();
    }

    @Override
    public boolean connectEncryptWifi(IWifi wifi, String password) {
        if (manager.getConnectionInfo() != null &&
                wifi.SSID().equals(manager.getConnectionInfo().getSSID())) {
            return true;
        }
        int networkId = WifiHelper.configOrCreateWifi(manager, wifi, password);
        boolean ret = manager.enableNetwork(networkId, true);
        modifyWifi(wifi.SSID(), "开始连接...");
        return ret;
    }

    @Override
    public boolean connectEncryptWifi(String wifiSSID, String password, String safetyCategory) {
        if(TextUtils.isEmpty(wifiSSID)){
            return false;
        }
        int networkId = WifiHelper.configOrCreateWifi(manager, wifiSSID, password, safetyCategory);
        boolean ret = manager.enableNetwork(networkId, true);
        modifyWifi(wifiSSID, "开始连接...");
        return false;
    }

    @Override
    public boolean connectSavedWifi(IWifi wifi) {
        int networkId = WifiHelper.configOrCreateWifi(manager, wifi, null);
        boolean ret = manager.enableNetwork(networkId, true);
        modifyWifi(wifi.SSID(), "开始连接...");
        return ret;
    }

    @Override
    public boolean connectOpenWifi(IWifi wifi) {
        boolean ret = connectEncryptWifi(wifi, null);
        modifyWifi(wifi.SSID(), "开始连接...");
        return ret;
    }

    @Override
    public boolean removeWifi(IWifi wifi) {
        boolean ret = WifiHelper.deleteWifiConfiguration(manager, wifi);
        modifyWifi();
        return ret;
    }

    @Override
    public List<IWifi> getWifi() {
        return wifiData;
    }


    @Override
    public void removeNoConnectWifi(String usedWifiSSID, String storeUsedWifiSSID) {
        try {
            Log.d(TAG, "clearNoConnectWifi usedWifiSSID:  " + usedWifiSSID + ", storeUsedWifiSSID: " + storeUsedWifiSSID);
            usedWifiSSID = usedWifiSSID.replaceAll("\"", "");

            //避免网络清空重复调用CONNECTIVITY_ACTION的广播事件
            if (!TextUtils.isEmpty(storeUsedWifiSSID) && storeUsedWifiSSID.equals(usedWifiSSID)) {
                Log.d(TAG, "存储的WIFI和连接的WIFI相同，不用做清空处理。");
                return;
            }

            Log.d(TAG, "目前使用的WIFI:  " + usedWifiSSID);

            // 获取当前已保存的Wi-Fi网络配置列表
            List<WifiConfiguration> wifiConfigurations = manager.getConfiguredNetworks();

            for(WifiConfiguration wifiConfiguration: wifiConfigurations){
                if (!wifiConfiguration.SSID.equals("\"" + usedWifiSSID + "\"")) {
                    manager.removeNetwork(wifiConfiguration.networkId);
                    manager.saveConfiguration();
                    Log.i(TAG, "clearNoConnectWifi 清空的WIFI信息：" + wifiConfiguration.SSID);
                }else{
                    Log.i(TAG, "clearNoConnectWifi 保持连接的WIFI信息：" + wifiConfiguration.SSID);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "clearNoConnectWifi error: " + e);
        }

    }
}
