package com.serverpit.wolf.controller;
//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
//import android.content.Context;
import android.util.Log;
//import android.widget.Button;
import android.widget.Toast;

//import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import com.serverpit.wolf.BLE.*;

/**
 * Created by Wolf on 2017/5/15.
 */

public class Controller {
    public interface ControllerCallBack {
        void ControllerStatusCallBack(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ);
        void ControllerOtherCallBack(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ);
        void ControllerFigCallBack(FingersStatus Figs);
        void ControllerKeysCallBack(int Keys);
        void LostConnection();
        void DeviceVailid();
    }
    private ControllerCallBack UserCB;
    private String DevAdd;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceManager DeviceManager;
    private Activity Parent;
    private BaseService PosService;
    private boolean PosConnected;
    private BaseService FigService;
    private boolean FigConnected;
    private BaseService RecService;
    private static class Services
    {
        public static final UUID[] ServiceID = {
                UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),//GAP_SERVICE_UUID
                UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"),//GATT_SERVICE_UUID
                UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"),//DEVINFO_SERV_UUID
                UUID.fromString("f000aa30-0451-4000-b000-000000000000"),//Pos Service
                UUID.fromString("f000aa35-0451-4000-b000-000000000000"),//FIG Service
                UUID.fromString("f000aa3C-0451-4000-b000-000000000000"),//REC Service
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

        //--Common values for turning a sensor on and off + config/status , For Bar, Hum, Opt, Mov, Tmp
        public static final int ST_CFG_SENSOR_DISABLE = 0x00;
        public static final int ST_CFG_SENSOR_ENABLE = 0x01;
        //-------------------

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
    public enum Status{
        DeviceConfigured,
        DeviceNotFound,
        DeviceConnectTimeOut,
        DeviceServiceError,
        DeviceCallBackError,
        DeviceClosed
    }
    private Status CurrentStatus = Status.DeviceClosed;
    private FingersStatus _FigStatus;
    private int _LocalKeys;
    private boolean Opened;
    private float[] Speed = new float[3];
    private float[] Acc = new float[3];
    public Controller(Activity P, String Address, BluetoothManager bluetoothManager)
    {
        PosConnected = false;
        FigConnected = false;
        DevAdd = Address;
        UserCB = null;
        Parent = P;
        Speed[0] = 0.0f;
        Speed[1] = 0.0f;
        Speed[2] = 0.0f;
        Acc[0] = 0.0f;
        Acc[1] = 0.0f;
        Acc[2] = 0.0f;
        _LocalKeys = 0x00;
        Opened = false;
        _FigStatus = new FingersStatus();
        PosService = new BaseService("Pos", Services.ServiceID[Services.SERV_Pos_UUID]);
        FigService = new BaseService("Fig", Services.ServiceID[Services.SERV_Fig_UUID]);
        RecService = new BaseService("Rec", Services.ServiceID[Services.SERV_Rec_UUID]);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        DeviceManager = new DeviceManager(Parent, mBluetoothAdapter, DevAdd);
        DeviceManager.SetListener(listener);
    }
    public void SetControllerAddress(String Address)
    {
        DeviceManager.Close();
        DevAdd = Address;
        DeviceManager = new DeviceManager(Parent, mBluetoothAdapter, DevAdd);
        DeviceManager.SetListener(listener);
    }
    public boolean Connected()
    {
        return Opened;
    }
    public String DeviceName()
    {
        if(DeviceManager.Connected())
            return DeviceManager.GetDeviceName();
        else
            return "Invalid";
    }
    public Status GetCurrentStatus()
    {
        return CurrentStatus;
    }
    public boolean Open(boolean FigEnable, boolean RecordEnable)
    {
        if(UserCB == null)
        {
            CurrentStatus = Status.DeviceCallBackError;
            return false;
        }
        if(DeviceManager.Connected() && CurrentStatus == Status.DeviceConfigured)
        {
            boolean result = true;
            result = DeviceManager.ConnectService(PosService);
            if(result)
                DeviceManager.setCharacteristicNotification(PosService, true);
            else
            {
                CurrentStatus = Status.DeviceServiceError;
                DeviceManager.Close();
                return false;
            }
            PosConnected = true;
            if(FigEnable){
                result = DeviceManager.ConnectService(FigService);
                if(result)
                    DeviceManager.setCharacteristicNotification(FigService, true);
                else
                {
                    CurrentStatus = Status.DeviceServiceError;
                    DeviceManager.Close();
                    return false;
                }
                FigConnected = true;
            }
            if(RecordEnable){
                result = DeviceManager.ConnectService(RecService);
                if(result)
                    DeviceManager.setCharacteristicNotification(RecService, true);
                else
                {
                    CurrentStatus = Status.DeviceServiceError;
                    DeviceManager.Close();
                    return false;
                }
            }
            DeviceManager.SetChar(PosService, Services.Pos[Services.PERI], new byte[]{0x0C});//Peri
            DeviceManager.SetChar(PosService, Services.Pos[Services.CONF], new byte[]{0x14});//Enable
            if(FigEnable) {
                DeviceManager.SetChar(FigService, Services.Fig[Services.PERI], new byte[]{0x32});//Peri
                DeviceManager.SetChar(FigService, Services.Fig[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_ENABLE});//Enable
            }
            if(RecordEnable)
                DeviceManager.SetChar(RecService, Services.Rec[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_ENABLE});//Enable

        }
        else
            return false;
        Opened = true;
        return true;
    }
    public void Close()
    {
        if(DeviceManager.Connected())
        {
            if(CurrentStatus == Status.DeviceConfigured) {
                CurrentStatus = Status.DeviceClosed;
                if(FigConnected) {
                    DeviceManager.SetChar(FigService, Services.Fig[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_DISABLE});//Enable
                    FigConnected = false;
                }
                if(PosConnected){
                    DeviceManager.SetChar(PosService, Services.Pos[Services.CONF], new byte[]{Services.ST_CFG_SENSOR_DISABLE});//Enable
                    PosConnected = false;
                }
            }
        }
        DeviceManager.Close();
        Opened = false;
    }
    public void RegisterCallback(ControllerCallBack cb)
    {
        if(cb != null)
            UserCB = cb;
    }
    private Listener listener = new Listener() {
        @Override
        public void Disconnected() {
            if(CurrentStatus == Status.DeviceConfigured) {
                CurrentStatus = Status.DeviceConnectTimeOut;
            }
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(CurrentStatus == Status.DeviceConnectTimeOut && UserCB != null)
                        UserCB.LostConnection();
                }
            });
            DeviceManager.Close();
        }

        @Override
        public void Configured() {
            if(DeviceManager.Connected()) {
                CurrentStatus = Status.DeviceConfigured;
                PosService.CharacteristicUUID.add(null);
                FigService.CharacteristicUUID.add(null);
                RecService.CharacteristicUUID.add(null);

                PosService.listener = devicelistener;
                FigService.listener = devicelistener;
                RecService.listener = devicelistener;
            }
            else {
                CurrentStatus = Status.DeviceNotFound;
                DeviceManager.Close();
            }
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(UserCB != null)
                        UserCB.DeviceVailid();
                }
            });
        }
        @Override
        public void ConfigError(final int status)
        {
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Parent, "Device connection Error("+status+").",Toast.LENGTH_SHORT).show();
                    DeviceManager.Close();
                    if(UserCB != null) {
                        UserCB.LostConnection();
                    }
                }
            });
        }
        @Override
        public void onError(final String err) {
            Parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };
    private DeviceListener devicelistener = new DeviceListener() {
        @Override
        public void GotNotification(String Name, UUID CharUUID, final byte[] data) {
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
                    public void run() {
                        if(UserCB != null) {
                            UserCB.ControllerStatusCallBack(Status, CMD, Roll * De2Ra, Pitch  * De2Ra, Yaw * De2Ra, DisX, DisY, DisZ);
                        }
                    }
                });
            }
            else if(Name.equals(FigService.Name()))
            {
                final int KEY = (data[10] & 0xff | (data[11] << 8))&0xffff;
                for (int i = 0; i < 5; i++)
                {
                    _FigStatus.Degree[i][0] = (data[i * 2] & 0xff);
                    _FigStatus.Degree[i][1] = (data[i * 2 + 1] & 0xff);
                    if (!_FigStatus.Enable[i][0] && (_FigStatus.Degree[i][0] != 0))
                        _FigStatus.Enable[i][0] = true;

                    if (!_FigStatus.Enable[i][1] && (_FigStatus.Degree[i][1] != 0))
                        _FigStatus.Enable[i][1] = true;
                }
                Parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(UserCB != null){
                            if(_LocalKeys != KEY)
                            {
                                _LocalKeys = KEY;
                                UserCB.ControllerKeysCallBack(KEY);
                            }
                            UserCB.ControllerFigCallBack(_FigStatus);
                        }
                    }
                });
            }
            else if(Name.equals(RecService.Name()))
            {
                if(CharUUID == Services.Rec[0])//Speed
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
                    public void run() {
                        if(UserCB != null){
                            UserCB.ControllerOtherCallBack(Speed[0], Speed[1], Speed[2], Acc[0], Acc[1], Acc[2]);
                        }
                    }
                });
            }
        }
        @Override
        public void ReadValue(String Name, byte[] data) {

        }
        @Override
        public void onError(String Name, String err) {
            Log.e(Name, err);
        }
    };
}
