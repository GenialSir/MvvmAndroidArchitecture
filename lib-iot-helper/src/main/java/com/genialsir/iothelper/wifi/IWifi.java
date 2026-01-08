package com.genialsir.iothelper.wifi;

/**
 * @author genialsir@163.com (GenialSir) on 2024/1/15
 */
public interface IWifi {

    String name();

    boolean isEncrypt();

    boolean isSaved();

    boolean isConnected();

    String encryption();

    int level();

    String description();

    String ip();

    String description2();

    void state(String state);

    @Deprecated
    String SSID();

    @Deprecated
    String capabilities();

    @Deprecated
    IWifi merge(IWifi merge);

    String state();
}
