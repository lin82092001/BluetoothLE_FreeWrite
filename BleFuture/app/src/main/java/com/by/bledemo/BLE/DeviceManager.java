package com.by.bledemo.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class DeviceManager {

    private class Data2Write
    {
        public boolean type;
        public Object Dest;
        public byte[] data;

        public Data2Write(BluetoothGattCharacteristic chars,byte[] data)
        {
            type = false;
            this.Dest = chars;
            this.data = data;
        }
        /*
        * GATT Descriptors contain additional information and attributes of a GATT characteristic, BluetoothGattCharacteristic.
        * They can be used to describe the characteristic's features or to control certain behaviours of the characteristic.
        */
        public Data2Write(BluetoothGattDescriptor desc, byte[] data)
        {
            type = true;
            this.Dest = desc;
            this.data = data;
        }
    }
    protected final UUID GATT_CLIENT_CHAR_CFG_UUID=UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    protected BluetoothGatt mGatt;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothDevice Device;
    private Queue<Data2Write> SendData;
    private Queue<BluetoothGattCharacteristic> ReadData;
    private List<BaseService> Services;
    private Listener listener;
    private Context parent;
    private Handler handler;
    private Runnable RSSIReader;
    private  final int DEFAULT_RSSI_PERI = 0;//Disable
    private int RSSI;
    private long RSSI_READ_PERI;
    private boolean RSSI_Enable = false;
    private boolean RSSI_Waitting = false;
    private boolean Writing;
    private boolean Reading;
    private boolean Connected;

    public DeviceManager()
    {
        this.Writing = false;
        this.Reading = false;
        this.Connected = false;
        this.mGatt = null;
        this.mBluetoothAdapter = null;
        this.Device = null;
        this.RSSI_READ_PERI = DEFAULT_RSSI_PERI;    //default 10ms
        this.SendData=new ConcurrentLinkedDeque<>();    //Constructs an empty deque.
        this.ReadData=new ConcurrentLinkedDeque<>();
        /*
        * If multiple threads access a linked list concurrently,
        * and at least one of the threads modifies the list structurally, it must be synchronized externally.
        * This is best done at creation time, to prevent accidental unsynchronized access to the list.
        * */
        this.Services= Collections.synchronizedList(new LinkedList<BaseService>()); //Returns a synchronized (thread-safe) list backed by the specified list.
        this.handler = new Handler();
    }
    public boolean Connected()  //The device is connected.
    {
        return this.Device!=null && this.Connected && this.mGatt!=null;
    }
}
