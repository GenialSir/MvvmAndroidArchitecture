package com.genialsir.iothelper.wifi;

import java.util.List;

/**
 * @author genialsir@163.com (GenialSir) on 2024/1/15
 */
public interface IWifiManager {

    boolean isOpened();

    void openWifi();

    void closeWifi();

    void scanWifi();

    boolean disConnectWifi();

    boolean connectEncryptWifi(IWifi wifi, String password);

    boolean connectEncryptWifi(String wifiSSID, String password, String safetyCategory);

    boolean connectSavedWifi(IWifi wifi);

    boolean connectOpenWifi(IWifi wifi);

    boolean removeWifi(IWifi wifi);

    void removeNoConnectWifi(String usedWifiSSID, String storeUsedWifiSSID);

    List<IWifi> getWifi();

    void setOnWifiConnectListener(OnWifiConnectListener onWifiConnectListener);

    void setOnWifiStateChangeListener(OnWifiStateChangeListener onWifiStateChangeListener);

    void setOnWifiChangeListener(OnWifiChangeListener onWifiChangeListener);


    void destroy();
}
