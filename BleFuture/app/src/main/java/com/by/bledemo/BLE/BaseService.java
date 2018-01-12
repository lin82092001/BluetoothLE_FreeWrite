package com.by.bledemo.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class BaseService {
    private String Name;
    public BluetoothGattService Service;
    public UUID Service_UUID;
    public List<BluetoothGattCharacteristic> Characteristic;
    public List<UUID> CharacteristicUUID;
    public DeviceListener listener;
    public boolean Configure;

    public BaseService(String Name,UUID Service_UUID){

    }
}
