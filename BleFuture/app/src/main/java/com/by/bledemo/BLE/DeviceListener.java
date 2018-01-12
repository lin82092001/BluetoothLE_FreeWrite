package com.by.bledemo.BLE;

import java.util.UUID;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public interface DeviceListener {
    void GotNotification(String Name, UUID CharUUID,byte[] data);
    void ReadValue(String Name,byte[] data);
    void onError(String Name,String err);
}
