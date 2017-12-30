package com.serverpit.wolf.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Wolf on 2016/2/28.
 */
public class BaseService {
    private String Name;
    public BluetoothGattService Service;
    public UUID Service_UUID;
    public List<BluetoothGattCharacteristic> Characteristics;
    public List<UUID> CharacteristicUUID;
    public DeviceListener listener;
    public boolean Configured;
    public BaseService(String Name, UUID Service_UUID)
    {
        this.Configured = false;
        this.listener = null;
        this.Service_UUID = Service_UUID;
        this.Characteristics = new ArrayList<>();
        this.CharacteristicUUID = new ArrayList<>();
        this.Name = Name;
    }
    public String Name()
    {
        return Name;
    }
    public void InitService()
    {
        this.Characteristics.clear();
        if(this.Service != null)
        {
            for(UUID CharID : this.CharacteristicUUID)
            {
                if(CharID != null) {
                    BluetoothGattCharacteristic ch = this.Service.getCharacteristic(CharID);
                    if (ch == null) {
                        if (this.listener != null)
                            this.listener.onError(this.Name(), "Characteristic " + CharID + " not found in " + this.Service_UUID);
                    } else {
                        this.Characteristics.add(ch);
                    }
                }
                else//add all
                {
                    for(BluetoothGattCharacteristic ch:this.Service.getCharacteristics()) {
                        Log.d(this.Name(),"Find UUID:"+ch.getUuid().toString());
                        this.Characteristics.add(ch);
                    }
                    break;
                }
            }
            this.Configured = true;
        }
    }
}
