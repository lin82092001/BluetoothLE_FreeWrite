package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.R;
import com.by.bledemo.DataProcess.SensorData;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class ConnectedActivity extends AppCompatActivity {
    private Activity me=this;
    private String LAddress,RAddress;
    private TextView LAdd;
    private TextView RAdd;
    private TextView LeftServices;
    private TextView RightServices;
    private Controller LDevice,RDevice;
    private boolean Paused,LOp,ROp;
    SensorData sensorData;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=this.findViewById(R.id.LAdd);
        RAdd=this.findViewById(R.id.RAdd);
        LeftServices=this.findViewById(R.id.LeftServices);
        RightServices=this.findViewById(R.id.RightServices);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        RAddress=inteData.getStringExtra("RightAddress");

        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(EventListener);
        RDevice.RegisterCallback(EventListener);
        sensorData=new SensorData(0,0,0,0,0,0,null,"");
    }

    public void Test(View view) //Start receiving data
    {
        if(LOp && ROp)
        {

        }
    }

    public void Close(View view)
    {
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.Close();
            RDevice.Close();
        }
        this.finish();
    }

    @Override
    protected void onResume()
    {
        if(Paused)
        {
            //LDevice.SetControllerAddress(LAddress);
            //RDevice.SetControllerAddress(RAddress);
            Paused = false;
        }
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.Close();
            RDevice.Close();
            Paused=true;
        }
        super.onPause();
    }
    @Override
    protected void onDestroy()
    {
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.Close();
            RDevice.Close();
            Paused=true;
        }
        super.onDestroy();
    }

    public class ControllerThread extends Thread
    {
        public ControllerThread()
        {

        }

        @Override
        public void run()
        {

        }
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerSignCallback(int Status, int CMD, float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ,Controller.FingersStatus Figs,String Address)
        {
            if(LAddress==Address)
            {
                final String PosDataValue=String.format("(%d,0x%02x)\nRoll:%1.4f,\nPitch:%1.4f,\nYaw:%1.4f", Status, CMD, Roll, Pitch,Yaw);
                final String RecDataValue=String.format("AccX:%1.4f,\nAccY:%1.4f,\nAccZ:%1.4f",AccX,AccY,AccZ);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LeftServices.setText(PosDataValue+"\n"+RecDataValue);
                    }
                });
            }
            if(RAddress==Address)
            {
                final String PosDataValue=String.format("(%d,0x%02x)\nRoll:%1.4f,\nPitch:%1.4f,\nYaw:%1.4f", Status, CMD, Roll, Pitch,Yaw);
                final String RecDataValue=String.format("AccX:%1.4f,\nAccY:%1.4f,\nAccZ:%1.4f",AccX,AccY,AccZ);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RightServices.setText(PosDataValue+"\n"+RecDataValue);
                    }
                });
            }
        }

        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ,String Address)
        {
            if(LAddress==Address)
            {
                final String ListTitle="Position :";
                final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
                final String data=ListTitle+DataValue;
            }
            if(RAddress==Address)
            {
                final String ListTitle="Position :";
                final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
                final String data=ListTitle+DataValue;
            }
        }

        @Override
        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ,String Address)
        {
            if(LAddress==Address)
            {
                final String ListTitle="Record :";
                final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
                final String data=ListTitle+DataValue;
            }
            if(RAddress==Address)
            {
                final String ListTitle="Record :";
                final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
                final String data=ListTitle+DataValue;
            }
        }

        @Override
        public void ControllerFingersCallback(Controller.FingersStatus Figs,String Address)
        {
            if(LAddress==Address)
            {
                int i, j;
                final String ListTitle = "Fingers :";
                String Data = "";
                for(i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0])
                    {
                        if(Data.length()>0)
                            Data = String.format("%s,Fig[%d]:%3d ", Data, i, Figs.Degree[i][0]);
                        else
                            Data = String.format("Fig[%d]:%3d ", i, Figs.Degree[i][0]);
                        if(Figs.Enable[i][1])
                        {
                            Data = String.format("%s,Fig[%d-1]:%3d ", Data, i, Figs.Degree[i][1]);
                        }
                    }
                }
                final String DateValue = Data;
                final String data=ListTitle + DateValue;
            }
            if(RAddress==Address)
            {
                int i, j;
                final String ListTitle = "Fingers :";
                String Data = "";
                for(i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0])
                    {
                        if(Data.length()>0)
                            Data = String.format("%s,Fig[%d]:%3d ", Data, i, Figs.Degree[i][0]);
                        else
                            Data = String.format("Fig[%d]:%3d ", i, Figs.Degree[i][0]);
                        if(Figs.Enable[i][1])
                        {
                            Data = String.format("%s,Fig[%d-1]:%3d ", Data, i, Figs.Degree[i][1]);
                        }
                    }
                }
                final String DateValue = Data;
                final String data=ListTitle + DateValue;
            }
        }

        @Override
        public void ControllerKeysCallback(int Keys,String Address)
        {
            final String ListTitle = "Key :";
            final String DateValue = String.format("%d", Keys);
        }

        @Override
        public void LostConnection()
        {
            Toast.makeText(me, "Device lost connection.",Toast.LENGTH_SHORT).show();
            Button bt = me.findViewById(R.id.Test);
            bt.setEnabled(false);
            bt = me.findViewById(R.id.Close);
            bt.setEnabled(false);
        }

        @Override
        public void DeviceValid()
        {
            Toast.makeText(me, "Device ready.",Toast.LENGTH_SHORT).show();
            Button bt = me.findViewById(R.id.Test);
            bt.setEnabled(true);
            if(LDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                LAdd.setText(LDevice.DeviceName());
                LOp=LDevice.Open(true,true);
            }
            if(RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                RAdd.setText(RDevice.DeviceName());
                ROp=RDevice.Open(true,true);
            }
        }
    };
}