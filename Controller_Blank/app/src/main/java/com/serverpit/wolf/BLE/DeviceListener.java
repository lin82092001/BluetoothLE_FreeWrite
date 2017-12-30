package com.serverpit.wolf.BLE;

import java.util.UUID;

/**
 * Created by Wolf on 2016/2/28.
 */
public interface DeviceListener {
    void GotNotification(String Name, UUID CharUUID, byte[] data);
    void ReadValue(String Name, byte[] data);
    void onError(String Name, String err);
}
