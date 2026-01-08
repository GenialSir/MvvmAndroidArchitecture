package com.genialsir.iothelper.ble.callback;

import com.genialsir.iothelper.ble.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
