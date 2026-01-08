package com.genialsir.iothelper.wifi;

/**
 * @author genialsir@163.com (GenialSir) on 2024/1/15
 */
public interface OnWifiConnectListener {
    void onConnectChanged(boolean status, IWifi iWifi);
}
