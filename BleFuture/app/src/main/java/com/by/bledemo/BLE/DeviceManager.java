package com.by.bledemo.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

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
    public DeviceManager(Context context,BluetoothAdapter bluetoothAdapter,String DeviceAddress)
    {
        this();
        this.mBluetoothAdapter = bluetoothAdapter;
        this.Device=this.mBluetoothAdapter.getRemoteDevice(DeviceAddress);  //Get device
        this.listener = null;
        this.parent = context;
        this.mGatt=this.Device.connectGatt(this.parent,false,gattCallback); //Connect to GATT Server hosted by this device.

        RSSIReader=new Runnable() {
            @Override
            public void run() {
                if(RSSI_Enable && Connected())
                {
                    if(!RSSI_Waitting)
                    {
                        mGatt.readRemoteRssi(); //Read the RSSI for a connected remote device.
                        RSSI_Waitting = true;
                    }
                }
                handler.postDelayed(this,RSSI_READ_PERI);
            }
        };
        handler.postDelayed(RSSIReader,RSSI_READ_PERI);
    }

    public boolean Connected()  //The device is connected.
    {
        return this.Device!=null && this.Connected && this.mGatt!=null;
    }
    public void Connect()
    {
        if(!this.Connected)
        {
            if(this.Device != null)
            {
                this.mGatt = this.Device.connectGatt(this.parent,false,gattCallback);
                this.RSSI_Enable = this.RSSI_READ_PERI>0;
            }
        }
    }
    public void Disconnect()
    {
        this.RSSI_Enable = false;
        if(this.Connected)
        {
            int wait_time = 0;
            while(SendData.size()>0)    //Watting for remain data write to device
            {
                try
                {
                    wait_time++;
                    Thread.sleep(1);
                }
                catch (Exception e)
                {
                    break;
                }
                if(wait_time > 1000)    //After 1s stop send
                    break;
            }
        }
        if(this.Services!=null)
        {
            for(BaseService ser : this.Services)
            {
                ser.GattService = null;
                ser.Characteristics.clear();
            }
            this.Services.clear();
        }
        if(this.mGatt != null)
        {
            this.mGatt.disconnect();    //Disconnects an established connection, or cancels a connection attempt currently in progress.
        }
    }
    public void Close()
    {
        this.Disconnect();
        this.Services = null;
        if(this.mGatt != null)
        {
            this.mGatt.close();
        }
    }
    public String GetDeviceName()
    {
        String Name="Invalid";
        if(Device!=null)
            Name=this.Device.getName();
        return Name;
    }
    public void SetListener(Listener listener)
    {
        this.listener = listener;
    }
    public void SetRSSIPeri(int Peri)
    {
        if(Peri>0)
        {
            if(!RSSI_Enable && this.Connected())
                this.mGatt.readRemoteRssi();
            this.RSSI_READ_PERI = Peri;
            this.RSSI_Enable = true;
        }
        else
        {
            this.RSSI_Enable = false;
        }
    }
    public int GetRSSI()
    {
        return this.RSSI;
    }

    private void Write()
    {
        if(!this.Writing)
        {
            if (this.Device!=null)
            {
                if (SendData.size() > 0)
                {
                    Data2Write dt = SendData.poll();
                    if(dt.data==null || dt.data.length == 0)
                    {
                        if(this.listener!=null)
                            this.listener.onError("Write NULL DATA");
                        return;
                    }
                    if (dt.type)    //Descriptor
                    {
                        BluetoothGattDescriptor desc = (BluetoothGattDescriptor) dt.Dest;
                        desc.setValue(dt.data);
                        this.Writing = true;
                        this.mGatt.writeDescriptor(desc);
                    }
                    else    //Characteristic
                    {
                        BluetoothGattCharacteristic chars = (BluetoothGattCharacteristic) dt.Dest;
                        chars.setValue(dt.data);
                        this.Writing = true;
                        this.mGatt.writeCharacteristic(chars);
                    }
                }
                else
                {
                    this.Writing = false;
                }
            }
            else
            {
                this.Writing = false;
            }
        }
    }
    private void Read()
    {
        if(!this.Reading)
        {
            if(this.Connected())
            {
                if(this.ReadData.size()>0)
                {
                    BluetoothGattCharacteristic ch = this.ReadData.poll();
                    if((ch.getProperties()&BluetoothGattCharacteristic.PROPERTY_READ)!=0)
                    {
                        this.Reading = true;
                        this.mGatt.readCharacteristic(ch);  //Reads the requested characteristic from the associated remote device.
                    }
                }
                else
                {
                    this.Reading = false;
                }
            }
        }
    }

    public void setCharacteristicNotification(BaseService Service,boolean enable)   //Setup GATT server is notification
    {
        if(this.Services.contains(Service) && this.mGatt != null)
        {
            for(BluetoothGattCharacteristic ch : Service.Characteristics)
            {
                if((ch.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
                {
                    if(enable)
                    {
                        Log.d("Notification",ch.getUuid()+" ,Enable");
                        this.SetDescriptor(ch.getDescriptor(this.GATT_CLIENT_CHAR_CFG_UUID), BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                    else
                    {
                        Log.d("Notification",ch.getUuid()+" ,Disable");
                        this.SetDescriptor(ch.getDescriptor(this.GATT_CLIENT_CHAR_CFG_UUID), BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                    this.mGatt.setCharacteristicNotification(ch,enable);
                    Log.d("Notification",Service.Name()+(enable?" Enable":" Disable"));
                }
            }
        }
        else
        {
            if(this.listener!=null)
                this.listener.onError("Manager didn't contain this service");
        }
    }
    private void SetCharacteristic(BluetoothGattCharacteristic chars, byte[] data)
    {
        if(this.Connected && chars != null && data != null)
        {
            SendData.offer(new Data2Write(chars, data));
            Write();
        }
    }
    public void SetCharacteristic(BaseService service,UUID target, byte[] data)
    {
        if(this.Services.contains(service))
        {
            if(service.Characteristics.size()!=0)
            {
                for(BluetoothGattCharacteristic ch : service.Characteristics)
                {
                    if(ch.getUuid().equals(target))
                    {
                        this.SetCharacteristic(ch, data);
                        return;
                    }
                }
                if (this.listener != null)
                    this.listener.onError("Service  " + service.Name() + " not have UUID:" + target.toString());
            }
            else
            {
                if (this.listener != null)
                    this.listener.onError("Service  " + service.Name() + " not initial yet.");
            }
        }
        else
        {
            if(this.listener!=null)
            {
                this.listener.onError("DeviceManager didn't contain " + service.Name());
                return;
            }

        }
    }
    public void ReadCharacteristic(BluetoothGattCharacteristic ch)
    {
        if(this.Connected())
        {
            this.ReadData.offer(ch);
            this.Read();
        }
    }
    public void SetDescriptor(BluetoothGattDescriptor desc, byte[] data)
    {
        if(this.Connected && desc !=null && data != null)
        {
            SendData.offer(new Data2Write(desc, data));
            Write();
        }
    }

    public boolean ConnectService(BaseService Service)  //Connect or remove service
    {
        if(this.Connected)
        {
            Service.GattService = this.mGatt.getService(Service.Service_UUID);
            if(Service.GattService == null)
            {
                if (this.listener != null)
                {
                    this.listener.onError("Service " + Service.Service_UUID + " not found.");
                }
                return false;
            }
            if(!Services.contains(Service))
                Services.add(Service);
            Service.InitService();
        }
        return true;
    }
    public void RemoveService(BaseService Service)
    {
        if(Service!=null)
        {
            if(this.Services.contains(Service))
            {
                this.setCharacteristicNotification(Service, false);
                Service.GattService = null;
                Service.Characteristics.clear();
                this.Services.remove(Service);
            }
        }
    }

    private BluetoothGattCallback gattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(status==BluetoothGatt.GATT_SUCCESS)
            {
                switch (newState)
                {
                    case BluetoothGatt.STATE_CONNECTED:
                        gatt.discoverServices();    //Discovers services offered by a remote device as well as their characteristics and descriptors.
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        mGatt.close();
                        Connected=false;
                        if(listener!=null)
                        {
                            listener.Disconnect();
                            listener.onError("Service disconnected.");
                        }
                        break;
                    default:
                        break;
                }
            }
            else
            {
                mGatt.close();
                Connected=false;
                if(listener!=null)
                    listener.ConfigError(status);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("onServicesDiscovered",gatt.getServices().size()+"");
            for(int i=0;i<gatt.getServices().size();i++)
                Log.i("Service UUID",gatt.getServices().get(i).getUuid().toString());
            Connected=true;
            if(RSSI_Enable)
                gatt.readRemoteRssi();
            if(listener!=null)
                listener.Configured();
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] value=characteristic.getValue();
            for(BaseService ser:Services)
            {
                if(ser.Characteristics.contains(characteristic))
                {
                    if(ser.listener!=null)
                    {
                        if(status == BluetoothGatt.GATT_SUCCESS)
                            ser.listener.ReadValue(ser.Name(), value);
                        else
                            ser.listener.onError(ser.Name(),"ERROR");
                    }
                    break;
                }
            }
            if(Reading)
            {
                Reading = false;
                Read();
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    if(Writing)
                    {
                        Writing=false;
                        Write();
                    }
                }
            });
        }
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    if(Writing)
                    {
                        Writing=false;
                        Write();
                    }
                }
            });
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] value=characteristic.getValue();
            BaseService Target=null;
            synchronized (Services)
            {
                for (BaseService ser:Services)
                {
                    if(ser.Characteristics.contains(characteristic))
                    {
                        Target=ser;
                        break;
                    }
                }
            }
            if(Target!=null)
                Target.listener.GotNotification(Target.Name(),characteristic.getUuid(),value);
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            RSSI=rssi;
            RSSI_Waitting=false;
        }
    };
}
