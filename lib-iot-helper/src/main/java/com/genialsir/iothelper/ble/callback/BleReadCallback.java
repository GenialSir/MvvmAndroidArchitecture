package com.genialsir.iothelper.ble.callback;


import com.genialsir.iothelper.ble.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
