package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.R;

import java.lang.reflect.Array;
import java.util.Arrays;

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

    String RollDirect=""; //Roll TAG
    String PitchDirect = "";//Pitch TAG
    int RollDirect_Flag =  00;
    int PitchDirect_Flag = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=this.findViewById(R.id.LAdd);
        //RAdd=this.findViewById(R.id.RAdd);
        LeftServices=this.findViewById(R.id.LeftServices);
        //RightServices=this.findViewById(R.id.RightServices);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        //RAddress=inteData.getStringExtra("RightAddress");

        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        //RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(EventListener);
        //RDevice.RegisterCallback(EventListener);
    }
    public void Test(View view) //Start receiving data
    {
        if(LOp && ROp)
        {

        }
    }

    public void Close(View view)
    {
        if(LDevice.Connected()/* && RDevice.Connected()*/)
        {
            LDevice.Close();
            //RDevice.Close();
        }
        this.finish();
    }

    @Override
    protected void onResume()
    {
        if(Paused)
        {
            LDevice.SetControllerAddress(LAddress);
            //RDevice.SetControllerAddress(RAddress);
            Paused = false;
        }
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        if(LDevice.Connected() /*&& RDevice.Connected()*/)
        {
            LDevice.Close();
            //RDevice.Close();
            Paused=true;
        }
        super.onPause();
    }
    @Override
    protected void onDestroy()
    {
        if(LDevice.Connected() /*&& RDevice.Connected()*/)
        {
            LDevice.Close();
            //RDevice.Close();
            Paused=true;
        }
        super.onDestroy();
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerSignCallback(int Status, int CMD, float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ,Controller.FingersStatus Figs,String Address)
        {
            String Data;
            /*
            String PalmsDirect="";
            int RollDirect_Binary[] = new int[]{00, 01, 10, 11};//上右左下
            int PalmsDirect_Flag =  00;
            int PitchtoRoll_Flag = 0;
            */
            if(LAddress == Address)
            {
                //x y z Rotat Value
                final String PosDataValue=String.format("(%d,0x%02x)\nRoll:%1.0f,\nPitch:%1.0f,\nYaw:%1.0f\n", Status, CMD, Roll, Pitch,Yaw);
                //not use
                final String RecDataValue=String.format("AccX:%f,\nAccY:%f,\nAccZ:%f\n",AccX,AccY,AccZ);


                //面相
                //PitchProcess
                if(-45 < Pitch && Pitch < 45)
                {
                    PitchDirect_Flag = 0;
                    PitchDirect = "水平";
                }else if(Pitch < -45)
                {
                    PitchDirect_Flag = -1;
                    PitchDirect = "下俯";
                }else if(45 < Pitch)
                {
                    PitchDirect_Flag = 1;
                    PitchDirect = "上仰";
                }
                //PitchProcess

                //RollProcess
                if(-45 < Roll && Roll < 45)
                {
                    RollDirect = "背上";
                    RollDirect_Flag = 00;
                }else if(-135 < Roll && Roll < -45)
                {
                    RollDirect = "背右";
                    RollDirect_Flag = 01;
                }else if(45 < Roll && Roll < 135)
                {
                    RollDirect = "背左";
                    RollDirect_Flag = 10;
                } else if(135 < Roll || Roll < -135)
                {
                    RollDirect = "背下";
                    RollDirect_Flag = 11;
                }
                //RollProcess
                //面相

                //FigDegreeValue
                int i;

                Data = "";
                for(i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0])
                    {
                        if(Data.length()>0)
                            Data = String.format("%sFig[%d-1]:%3d\n", Data, i, Figs.Degree[i][0]);
                        else
                            Data = String.format("Left Hand\n拇指[%d]:%3d\n\n", i, Figs.Degree[i][0]);
                        if(Figs.Enable[i][1])
                        {
                            Data = String.format("%sFig[%d-2]:%3d\n\n", Data, i, Figs.Degree[i][1]);
                        }
                    }
                }
                final String FigsDateValue = Data;
                //FigDegreeValue

                //面相
                final String RollDirection = String.format("%02d + %d\n\"%s\"\n\"%s\"\n", RollDirect_Flag, PitchDirect_Flag, RollDirect, PitchDirect);
                //

                //print out
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        LeftServices.setText(FigsDateValue + PosDataValue + RollDirection);
                    }
                });

            }
            /*if(RAddress==Address)
            {
                final String PosDataValue=String.format("(%d,0x%02x)\nRoll:%1.4f,\nPitch:%1.4f,\nYaw:%1.4f", Status, CMD, Roll, Pitch,Yaw);
                final String RecDataValue=String.format("AccX:%f,\nAccY:%f,\nAccZ:%f",AccX,AccY,AccZ);
                int i;

                Data = "";

                for(i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0])
                    {
                        if(Data.length()>0)
                            Data = String.format("%s\nFig[%d]:%3d", Data, i, Figs.Degree[i][0]);
                        else
                            Data = String.format("Right Hand\nFig[%d]:%3d", i, Figs.Degree[i][0]);
                        if(Figs.Enable[i][1])
                        {
                            Data = String.format("%s\nFig[%d-1]:%3d\n", Data, i, Figs.Degree[i][1]);
                        }
                    }
                }
                final String FigsDateValue = Data;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        RightServices.setText(FigsDateValue + PosDataValue);
                    }
                });
            }*/
        }

        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ,String Address)
        {

        }

        @Override
        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ,String Address)
        {

        }

        @Override
        public void ControllerFingersCallback(Controller.FingersStatus Figs,String Address)
        {

        }

        @Override
        public void ControllerKeysCallback(int Keys,String Address)
        {

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
            /*if(RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                RAdd.setText(RDevice.DeviceName());
                ROp=RDevice.Open(true,true);
            }*/
        }
    };
}