package com.serverpit.wolf.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
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
 * Created by Wolf on 2016/2/28.
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
        public Data2Write(BluetoothGattDescriptor desc,byte[] data)
        {
            type = true;
            this.Dest = desc;
            this.data = data;
        }
    }
    private  final int DEFAULT_RSSI_PERI = 0;//Disable
    protected final UUID GATT_CLIENT_CHAR_CFG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    protected BluetoothGatt mGatt;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothDevice Device;
    private int RSSI;
    private Queue<Data2Write> SendData;
    private Queue<BluetoothGattCharacteristic> ReadData;
    private List<BaseService> Services;
    private boolean Writing;
    private boolean Reading;
    private Listener listener;
    private boolean Connected;
    private Context parent;
    private long RSSI_READ_PERI;
    private boolean RSSI_Enable = false;
    private boolean RSSI_Watting = false;
    public boolean Connected()
    {
        return this.Device!=null && this.Connected && this.mGatt!=null;
    }
    private Handler handler;
    private Runnable RSSIReader;
    public DeviceManager()
    {

        this.Writing = false;
        this.Reading = false;
        this.Connected = false;
        this.mGatt = null;
        this.mBluetoothAdapter = null;
        this.Device = null;
        this.RSSI_READ_PERI = DEFAULT_RSSI_PERI;//default 10ms
        this.SendData = new ConcurrentLinkedDeque<>();
        this.ReadData = new ConcurrentLinkedDeque<>();
        this.Services = Collections.synchronizedList(new LinkedList<BaseService>());
        this.handler = new Handler();
    }
    public DeviceManager(Context con,BluetoothAdapter bluetoothManager,String DeviceAddress)
    {
        this();
        this.mBluetoothAdapter = bluetoothManager;
        this.Device = this.mBluetoothAdapter.getRemoteDevice(DeviceAddress);
        this.listener = null;
        this.parent = con;
        this.mGatt = this.Device.connectGatt(this.parent,false,gattCallback);
        RSSIReader = new Runnable() {
                                @Override
                                public void run() {
                                    if(RSSI_Enable && Connected()) {
                                        if(!RSSI_Watting) {
                                            mGatt.readRemoteRssi();
                                            RSSI_Watting = true;
                                        }
                                    }
                                    handler.postDelayed(this,RSSI_READ_PERI);
                                }
                            };
        handler.postDelayed(RSSIReader,RSSI_READ_PERI);
    }
    public String GetDeviceName()
    {
        String Name = "Invalid";
        if(Device != null)
            Name = this.Device.getName();
        return Name;
    }
    public void SetListener(Listener listener)
    {
        this.listener = listener;
    }
    public void SetRSSIPeri(int Peri)
    {
        if(Peri>0) {
            if(!RSSI_Enable && this.Connected())
                this.mGatt.readRemoteRssi();
            this.RSSI_READ_PERI = Peri;
            this.RSSI_Enable = true;
        }
        else
            this.RSSI_Enable = false;
    }
    public int GetRSSI()
    {
        return this.RSSI;
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
            int waittime = 0;
            while(SendData.size()>0)//Watting for remain data write to device
            {
                try
                {
                    waittime++;
                    Thread.sleep(1);
                }
                catch (Exception e)
                {
                    break;
                }
                if(waittime > 1000)
                    break;
            }
        }
        if(this.Services!=null)
        {
            for(BaseService ser : this.Services)
            {
                ser.Service = null;
                ser.Characteristics.clear();
            }
            this.Services.clear();
        }
        if(this.mGatt != null)
        {
            this.mGatt.disconnect();
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
    public void setCharacteristicNotification(BaseService Service, boolean enable)
    {
        if(this.Services.contains(Service) && this.mGatt != null)
        {
            for(BluetoothGattCharacteristic ch : Service.Characteristics)
            {
                if((ch.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
                {
                    if(enable) {
                        Log.d("Notification",ch.getUuid()+" EN");
                        this.SetDesc(ch.getDescriptor(this.GATT_CLIENT_CHAR_CFG_UUID), BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                    else {
                        Log.d("Notification",ch.getUuid()+" DIS");
                        this.SetDesc(ch.getDescriptor(this.GATT_CLIENT_CHAR_CFG_UUID), BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                    this.mGatt.setCharacteristicNotification(ch,enable);
                    Log.d("Notification",Service.Name()+(enable?" EN":" DIS"));
                }
            }
        }
        else
        {
            if(this.listener!=null)
                this.listener.onError("Manager didn't contain this service");
        }
    }
    public boolean ConnectService(BaseService Service)
    {
        if(this.Connected) {
            Service.Service = this.mGatt.getService(Service.Service_UUID);
            if(Service.Service == null) {
                if (this.listener != null) {
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
                Service.Service = null;
                Service.Characteristics.clear();
                this.Services.remove(Service);
            }
        }
    }
    private void SetChar(BluetoothGattCharacteristic chars, byte[] data)
    {
        if(this.Connected && chars != null && data != null)
        {
            SendData.offer(new Data2Write(chars, data));
            Write();
        }
    }
    public void SetChar(BaseService service,UUID target, byte[] data)
    {
        if(this.Services.contains(service))
        {
            if(service.Characteristics.size()!=0)
            {
                for(BluetoothGattCharacteristic ch : service.Characteristics)
                {
                    if(ch.getUuid().equals(target))
                    {
                        this.SetChar(ch, data);
                        return;
                    }
                }
                if (this.listener != null)
                    this.listener.onError("Service  " + service.Name() + " not have UUID:" + target.toString());
            }
            else {
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
    public void SetDesc(BluetoothGattDescriptor desc, byte[] data)
    {
        if(this.Connected && desc !=null && data != null)
        {
            SendData.offer(new Data2Write(desc, data));
            Write();
        }
    }
    private void Write()
    {
        if(!this.Writing) {
            if (this.Device!=null) {
                if (SendData.size() > 0) {
                    Data2Write dt = SendData.poll();
                    if(dt.data==null || dt.data.length == 0) {
                        if(this.listener!=null)
                            this.listener.onError("Write NULL DATA");
                        return;
                    }
                    if (dt.type)//Descriptor
                    {
                        BluetoothGattDescriptor desc = (BluetoothGattDescriptor) dt.Dest;
                        desc.setValue(dt.data);
                        this.Writing = true;
                        this.mGatt.writeDescriptor(desc);
                    } else//Characteristic
                    {
                        BluetoothGattCharacteristic chars = (BluetoothGattCharacteristic) dt.Dest;
                        chars.setValue(dt.data);
                        this.Writing = true;
                        this.mGatt.writeCharacteristic(chars);
                    }
                } else {
                    this.Writing = false;
                }
            }
            else
                this.Writing = false;
        }
    }
    public void ReadChar(BluetoothGattCharacteristic characteristic)
    {
        if(this.Connected())
        {
            this.ReadData.offer(characteristic);
            this.Read();
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
                    if((ch.getProperties()&BluetoothGattCharacteristic.PROPERTY_READ)!=0) {
                        this.Reading = true;
                        this.mGatt.readCharacteristic(ch);
                    }
                }
                else
                    this.Reading = false;
            }
        }
    }
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        mGatt.close();
                        Connected = false;
                        if (listener != null)
                            listener.Disconnected();
                        if (listener != null)
                            listener.onError("Service disconnected");
                        break;
                    default:
                        break;
                }
            } else {
                mGatt.close();
                Connected = false;
                if (listener != null)
                    listener.ConfigError(status);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("onServicesDiscovered", gatt.getServices().size() + "");
            for(int i=0;i<gatt.getServices().size();i++)
                Log.i("Service UUID",gatt.getServices().get(i).getUuid().toString());
            Connected = true;
            if(RSSI_Enable)
                gatt.readRemoteRssi();
            if (listener != null)
                listener.Configured();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] value = characteristic.getValue();
            for(BaseService ser : Services)
            {
                if(ser.Characteristics.contains(characteristic))
                {
                    if(ser.listener!=null) {
                        if(status == BluetoothGatt.GATT_SUCCESS)
                            ser.listener.ReadValue(ser.Name(), value);
                        else
                            ser.listener.onError(ser.Name(),"ERROR");
                    }
                    break;
                }
            }
            if(Reading) {
                Reading = false;
                Read();
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(Writing) {
                        Writing = false;
                        Write();
                    }
                }
            });
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(Writing) {
                        Writing = false;
                        Write();
                    }
                }
            });
        }

        @Override
        public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            byte[] value = characteristic.getValue();
            BaseService Target = null;
            synchronized(Services) {
                for (BaseService ser : Services) {
                    if (ser.Characteristics.contains(characteristic)) {
                        Target = ser;
                        break;
                    }
                }
            }
            if(Target != null)
                Target.listener.GotNotification(Target.Name(), characteristic.getUuid(), value);
        }

        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, int rssi, int status)
        {
            RSSI = rssi;
            RSSI_Watting = false;
        }
    };
}
