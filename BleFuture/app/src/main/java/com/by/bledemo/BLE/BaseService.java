package com.by.bledemo.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 林北94狂 on 2018/1/10.
 */
    /*
      Gatt Service contains a collection of BluetoothGattCharacteristic, as well as referenced services.
    * A GATT characteristic is a basic data element used to construct a GATT service, BluetoothGattService.
    * The characteristic contains a value as well as additional information and optional GATT descriptors, BluetoothGattDescriptor.
    */
public class BaseService {
    private String Name;
    public BluetoothGattService GattService;    //Represents a Bluetooth GATT Service
    public UUID Service_UUID;   //An immutable universally unique identifier (UUID). A UUID represents a 128-bit value.
    public List<BluetoothGattCharacteristic> Characteristics;   //Represents a Bluetooth GATT Characteristic
    public List<UUID> CharacteristicUUID;
    public DeviceListener listener;
    public boolean Configured;

    public BaseService(String Name,UUID Service_UUID)
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
    public void InitService()   //Initial GATT services
    {
        this.Characteristics.clear();
        if(this.GattService != null)    //if find GATT services,show out characteristic from UUID or list of characteristic
        {
            for(UUID CharID : this.CharacteristicUUID)
            {
                if(CharID != null)
                {
                    BluetoothGattCharacteristic ch = this.GattService.getCharacteristic(CharID);    //Returns a characteristic with a given UUID out of the list of characteristics offered by this service.
                    if (ch == null)
                    {
                        if (this.listener != null)
                            this.listener.onError(this.Name(), "Characteristic " + CharID + " not found in " + this.Service_UUID);
                    }
                    else
                    {
                        this.Characteristics.add(ch);   //Add given UUID's characteristic
                    }
                }
                else
                {
                    for(BluetoothGattCharacteristic ch:this.GattService.getCharacteristics())   //Returns a list of characteristics included in this service.
                    {
                        Log.d(this.Name(),"Find UUID:"+ch.getUuid().toString());
                        this.Characteristics.add(ch);   //add all
                    }
                    break;
                }
            }
            this.Configured = true;
        }
    }
}
