package com.genialsir.iothelper.ble.callback;


import com.genialsir.iothelper.ble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}