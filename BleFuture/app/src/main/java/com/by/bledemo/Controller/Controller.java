package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.util.Log;
import android.widget.Toast;

import com.by.bledemo.BLE.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class Controller {
    public interface ControllerCallback
    {
        void ControllerStatusCallback(int Status,int CMD,float Roll,float Pitch,float Yaw,float DisX,float DisY,float DisZ,String Address);
        void ControllerOtherCallback(float SpeedX,float SpeedY,float SpeedZ,float AccX,float AccY,float AccZ,String Address);
        void ControllerFingersCallback(FingersStatus Figs,String Address);
        void ControllerKeysCallback(int Keys,String Address);
        void LostConnection();
        void DeviceValid();
    }

    private ControllerCallback UserCB;
    private String DeviceAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceManager deviceManager;
    private Activity Parent;
    private BaseService FingersService;
    private BaseService PosService;
    private BaseService RecService;
    private boolean FingersConnected;
    private boolean PosConnected;

    private static class Services
    {
        public static final UUID[] ServicesUUID={
                UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),//GAP_SERVICE_UUID
                UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"),//GATT_SERVICE_UUID
                UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"),//DEVINFO_SERV_UUID
                UUID.fromString("f000aa30-0451-4000-b000-000000000000"),//Pos Service
                UUID.fromString("f000aa35-0451-4000-b000-000000000000"),//FIG Service
                UUID.fromString("f000aa3C-0451-4000-b000-000000000000"),//REC Service
        };
        public static final UUID[] Pos = {
                UUID.fromString("f000aa31-0451-4000-b000-000000000000"),//Data
                UUID.fromString("f000aa32-0451-4000-b000-000000000000"),//Config
                UUID.fromString("f000aa33-0451-4000-b000-000000000000")//Peri
        };
        public static final UUID[] Fig = {
                UUID.fromString("f000aa36-0451-4000-b000-000000000000"),//Data
                UUID.fromString("f000aa37-0451-4000-b000-000000000000"),//Config
                UUID.fromString("f000aa38-0451-4000-b000-000000000000")//Peri
        };
        public static final UUID[] Rec = {
                UUID.fromString("f000aa3D-0451-4000-b000-000000000000"),//Data1
                UUID.fromString("f000aa3E-0451-4000-b000-000000000000"),//Data2
        };
        public static final int GAP_SERVICE_UUID = 0;
        public static final int GATT_SERVICE_UUID = 1;
        public static final int DEVINFO_SERV_UUID = 2;
        public static final int SERV_Pos_UUID = 3;
        public static final int SERV_Fig_UUID = 4;
        public static final int SERV_Rec_UUID = 5;
        public static final int DATA = 0;
        public static final int CONF = 1;
        public static final int PERI = 2;
        //--Common values for turning a sensor on and off + config/status , For Bar, Hum, Opt, Mov, Tmp
        public static final int ST_CFG_SENSOR_DISABLE = 0x00;
        public static final int ST_CFG_SENSOR_ENABLE = 0x01;

        public static final UUID GATT_CLIENT_CHAR_CFG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }

    public abstract class Command
    {
        public static final short CMD_TempStop = 0x01;
        public static final short CMD_Ready = 0x80;
        public static final short CMD_Calibrated = 0x40;
    }
    public class FingersStatus
    {
        public boolean[][] Enable = {{false,false},{false,false},{false,false},{false,false},{false,false}};
        public int[][] Degree = {{0,0},{0,0},{0,0},{0,0},{0,0}};
    }
    public enum Status  //Check device status
    {
        DeviceConfigured,
        DeviceNotFound,
        DeviceConnectTimeOut,
        DeviceServiceError,
        DeviceCallBackError,
        DeviceClosed
    }

    private Status CurrentStatus= Status.DeviceClosed;
    private FingersStatus FigStatus;
    private int LocalKeys;
    private boolean Opened;
    private float[] Speed = new float[3];
    private float[] Acc = new float[3];
    public Controller(Activity P, String Address, BluetoothManager bluetoothManager)
    {
        FingersConnected=false;
        PosConnected=false;
        DeviceAddress=Address;
        Parent=P;
        UserCB=null;
        Speed[0] = 0.0f;
        Speed[1] = 0.0f;
        Speed[2] = 0.0f;
        Acc[0] = 0.0f;
        Acc[1] = 0.0f;
        Acc[2] = 0.0f;
        LocalKeys = 0x00;
        Opened = true;
        FigStatus=new FingersStatus();
        FingersService=new BaseService("Fingers",Services.ServicesUUID[Services.SERV_Fig_UUID]);
        PosService=new BaseService("Position",Services.ServicesUUID[Services.SERV_Pos_UUID]);
        RecService=new BaseService("Rec",Services.ServicesUUID[Services.SERV_Rec_UUID]);
        mBluetoothAdapter=bluetoothManager.getAdapter();
        deviceManager=new DeviceManager(Parent,mBluetoothAdapter,DeviceAddress);
        deviceManager.SetListener(listener);
    }
    public void SetControllerAddress(String Address)
    {
        deviceManager.Close();
        DeviceAddress=Address;
        deviceManager=new DeviceManager(Parent,mBluetoothAdapter,DeviceAddress);
        deviceManager.SetListener(listener);
    }
    public boolean Connected()
    {
        return Opened;
    }
    public String DeviceName()
    {
        if(deviceManager.Connected())
            return deviceManager.GetDeviceName();
        else
            return "Invalid";
    }
    public Status GetCurrentStatus()
    {
        return CurrentStatus;
    }
    public boolean Open(boolean FigEnable,boolean RecordEnable)
    {
        if(UserCB == null)
        {
            CurrentStatus = Status.DeviceCallBackError;
            return false;
        }
        if(deviceManager.Connected() && CurrentStatus == Status.DeviceConfigured)
        {
            boolean result = deviceManager.ConnectService(PosService);
            if(result)
            {
                deviceManager.setCharacteristicNotification(PosService, true);
            }   //if connected,enable notification
            else
            {
                CurrentStatus = Status.DeviceServiceError;
                deviceManager.Close();
                return false;
            }
            PosConnected = true;
            if(FigEnable)
            {
                result = deviceManager.ConnectService(FingersService);
                if(result)
                {
                    deviceManager.setCharacteristicNotification(FingersService, true);
                }
                else
                {
                    CurrentStatus = Status.DeviceServiceError;
                    deviceManager.Close();
                    Log.e("Fig :","Fig Error!");
                    return false;
                }
                FingersConnected = true;
            }
            if(RecordEnable)
            {
                result = deviceManager.ConnectService(RecService);
                if(result)
                {
                    deviceManager.setCharacteristicNotification(RecService, true);
                }
                else
                {
                    CurrentStatus = Status.DeviceServiceError;
                    deviceManager.Close();
                    return false;
                }
            }
            deviceManager.SetCharacteristic(PosService, Services.Pos[Services.PERI], new byte[]{0x0C});//Peri
            deviceManager.SetCharacteristic(PosService, Services.Pos[Services.CONF], new byte[]{0x14});//Enable
            if(FigEnable)
            {
                deviceManager.SetCharacteristic(FingersService, Services.Fig[Services.PERI], new byte[]{0x32});//Peri
                deviceManager.SetCharacteristic(FingersService, Services.Fig[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_ENABLE});//Enable
            }
            if(RecordEnable)
            {
                //deviceManager.SetCharacteristic(RecService, Services.Rec[Services.DATA], new byte[]{Services.ST_CFG_SENSOR_ENABLE});//Enable
                deviceManager.SetCharacteristic(RecService, Services.Rec[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_ENABLE});//Enable
            }
        }
        else
        {
            return false;
        }
        Opened = true;
        return true;
    }
    public void Close()
    {
        Opened = false;
        if(deviceManager.Connected())
        {
            if(CurrentStatus == Status.DeviceConfigured)
            {
                CurrentStatus = Status.DeviceClosed;
                if(FingersConnected)
                {
                    deviceManager.SetCharacteristic(FingersService, Services.Fig[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_DISABLE});//Disable
                    FingersConnected = false;
                }
                if(PosConnected)
                {
                    deviceManager.SetCharacteristic(PosService, Services.Pos[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_DISABLE});//Enable
                    PosConnected = false;
                }
            }
        }
        deviceManager.Close();
    }
    public void RegisterCallback(ControllerCallback cb)
    {
        if (cb!=null)
            UserCB=cb;
        else
            UserCB=null;
    }

    private Listener listener=new Listener() {
        @Override
        public void Disconnect()
        {
            if(CurrentStatus==Status.DeviceConfigured)
            {
                CurrentStatus=Status.DeviceConnectTimeOut;
            }
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(CurrentStatus==Status.DeviceConnectTimeOut && UserCB!=null)
                    {
                        UserCB.LostConnection();
                    }
                }
            });
            deviceManager.Close();
        }

        @Override
        public void Configured()
        {
            if(deviceManager.Connected())
            {
                CurrentStatus=Status.DeviceConfigured;
                FingersService.CharacteristicUUID.add(null);
                PosService.CharacteristicUUID.add(null);
                RecService.CharacteristicUUID.add(null);
                FingersService.listener=deviceListener;
                PosService.listener=deviceListener;
                RecService.listener=deviceListener;
            }
            else
            {
                CurrentStatus=Status.DeviceNotFound;
                deviceManager.Close();
            }
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(CurrentStatus==Status.DeviceConfigured && UserCB!=null)
                    {
                        UserCB.DeviceValid();
                    }
                }
            });
        }

        @Override
        public void ConfigError(final int status)
        {
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(Parent,"Device connection error("+status+").",Toast.LENGTH_SHORT).show();
                    deviceManager.Close();
                    if(UserCB!=null)
                        UserCB.LostConnection();
                }
            });
        }

        @Override
        public void onError(final String err)
        {
            Log.e("Listener onError",err);
        }
    };
    private DeviceListener deviceListener=new DeviceListener() {
        @Override
        public void GotNotification(String Name, UUID CharUUID, byte[] data)
        {
            if(Name.equals(PosService.Name()))
            {
                final float De2Ra = (float)(Math.PI / 180.0);
                int Converter;
                final int Status, CMD;
                final float Roll, Pitch, Yaw, DisX, DisY, DisZ;
                Status = ((int)data[18])&0xff;
                CMD = ((int)data[19])&0xff;

                Converter = (data[0] & 0xff | (data[1] << 8) & 0xff00)&0xffff;
                if((Converter & 0x8000) != 0)
                    Converter |= 0xffff0000;
                Yaw = (float)((Converter / 100.0));

                Converter = (data[2] & 0xff | (data[3] << 8) & 0xff00)&0xffff;
                if((Converter & 0x8000) != 0)
                    Converter |= 0xffff0000;
                Pitch = (float)((Converter / 100.0) );

                Converter = (data[4] & 0xff | (data[5] << 8) & 0xff00)&0xffff;
                if((Converter & 0x8000) != 0)
                    Converter |= 0xffff0000;
                Roll = (float)((Converter / 100.0) );

                DisX = ByteBuffer.wrap(data, 6, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                DisY = ByteBuffer.wrap(data, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                DisZ = ByteBuffer.wrap(data, 14, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                Parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(UserCB != null)
                        {
                            UserCB.ControllerStatusCallback(Status, CMD, Roll * De2Ra, Pitch  * De2Ra, Yaw * De2Ra, DisX, DisY, DisZ,DeviceAddress);
                        }
                    }
                });
            }
            if(Name.equals(FingersService.Name()))
            {
                final int KEY = (data[10] & 0xff | (data[11] << 8))&0xffff;
                for (int i = 0; i < 5; i++)
                {
                    FigStatus.Degree[i][0] = (data[i * 2] & 0xff);
                    FigStatus.Degree[i][1] = (data[i * 2 + 1] & 0xff);
                    if (!FigStatus.Enable[i][0] && (FigStatus.Degree[i][0] != 0))
                        FigStatus.Enable[i][0] = true;

                    if (!FigStatus.Enable[i][1] && (FigStatus.Degree[i][1] != 0))
                        FigStatus.Enable[i][1] = true;
                }
                Parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(UserCB != null)
                        {
                            if(LocalKeys != KEY)
                            {
                                LocalKeys = KEY;
                                UserCB.ControllerKeysCallback(KEY,DeviceAddress);
                            }
                            UserCB.ControllerFingersCallback(FigStatus,DeviceAddress);
                        }
                    }
                });
            }
            if(Name.equals(RecService.Name()))
            {
                //Modifies this buffer's byte order.
                /*Constant denoting little-endian byte order.
                In this order, the bytes of a multibyte value are ordered from least significant to most significant.
                Relative get method for reading a float value.*/
                if(CharUUID == Services.Rec[Services.DATA])//Speed
                {
                    Speed[0] = ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Speed[1] = ByteBuffer.wrap(data, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Speed[2] = ByteBuffer.wrap(data, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                }
                else//Acc
                {
                    Acc[0] = ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Acc[1] = ByteBuffer.wrap(data, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Acc[2] = ByteBuffer.wrap(data, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                }
                Parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(UserCB != null)
                        {
                            UserCB.ControllerOtherCallback(Speed[0], Speed[1], Speed[2], Acc[0], Acc[1], Acc[2],DeviceAddress);
                        }
                    }
                });
            }
        }

        @Override
        public void ReadValue(String Name, byte[] data)
        {

        }

        @Override
        public void onError(String Name, String err)
        {
            Log.e(Name,err);
        }
    };
}