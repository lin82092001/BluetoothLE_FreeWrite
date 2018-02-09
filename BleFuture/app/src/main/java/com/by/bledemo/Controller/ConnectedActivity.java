package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.R;
import com.by.bledemo.SensorData;

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
    private ArrayAdapter<String> LlistAdapter;
    private ArrayAdapter<String> RlistAdapter;
    private Controller LDevice,RDevice;
    private boolean Paused,LOp,ROp;
    SensorData sensorData;
    ControllerThread left,right;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=(TextView)this.findViewById(R.id.LAdd);
        RAdd=(TextView)this.findViewById(R.id.RAdd);
        LeftServices=(TextView)this.findViewById(R.id.LeftServices);
        RightServices=(TextView)this.findViewById(R.id.RightServices);
        LlistAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        RlistAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
//        LeftServices.setAdapter(LlistAdapter);
//        RightServices.setAdapter(RlistAdapter);
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
        left=new ControllerThread();
        //right=new ControllerThread(RAddress);
    }

    public void Test(View view) //Start receiving data
    {
        if(LOp && ROp)
        {
            left.start();
            //right.start();
        }
    }

    public void Close(View view)
    {
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.RegisterCallback(null);
            RDevice.RegisterCallback(null);
            LDevice.Open(false,false);
            RDevice.Open(false,false);
            LDevice.Close();
            RDevice.Close();

            left.interrupt();
            left=null;
            //right.interrupt();
            //right=null;
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

            left.interrupt();
            left=null;
            //right.interrupt();
            //right=null;
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

            left.interrupt();
            left=null;
            //right.interrupt();
            //right=null;
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

            left.interrupt();
            left=null;
            //right.interrupt();
            //right=null;
        }
        super.onDestroy();
    }

    public class ControllerThread extends Thread
    {
        public ControllerThread(/*String Address*/)
        {
            //this.Address=Address;
        }

        @Override
        public void run()
        {
            while (LOp&&ROp)
            {
                if (sensorData.getAddress().equals(LAddress))
                {
                    final String data="AccX:"+sensorData.getAccX()+"\nAccY:"+sensorData.getAccY()+"\nAccZ:"+sensorData.getAccZ()
                            +"\nRoll:"+sensorData.getRoll()+"\nPitch:"+sensorData.getPitch()+"\nYaw:"+sensorData.getYaw();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LeftServices.setText(data);
                        }
                    });
                }
                if(sensorData.getAddress().equals(RAddress))
                {
                    final String data="AccX:"+sensorData.getAccX()+"\nAccY:"+sensorData.getAccY()+"\nAccZ:"+sensorData.getAccZ()
                            +"\nRoll:"+sensorData.getRoll()+"\nPitch:"+sensorData.getPitch()+"\nYaw:"+sensorData.getYaw();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RightServices.setText(data);
                        }
                    });
                }
            }
        }
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ,String Address)
        {
            if(LAddress==Address)
            {
                final String ListTitle="Position :";
                final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
                final String data=ListTitle+DataValue;
//                synchronized (LlistAdapter)
//                {
//                    int i;
//                    for (i = 0; i < LlistAdapter.getCount(); i++)
//                    {
//                        if (LlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < LlistAdapter.getCount())
//                    {
//                        LlistAdapter.remove(LlistAdapter.getItem(i));
//                        LlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        LlistAdapter.add(data);
//                    }
//                    LlistAdapter.notifyDataSetChanged();
//                }
                sensorData.setEuler(Roll,Pitch,Yaw,Address);
            }
            if(RAddress==Address)
            {
                final String ListTitle="Position :";
                final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
                final String data=ListTitle+DataValue;
//                synchronized (RlistAdapter)
//                {
//                    int i;
//                    for (i = 0; i < RlistAdapter.getCount(); i++)
//                    {
//                        if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < RlistAdapter.getCount())
//                    {
//                        RlistAdapter.remove(RlistAdapter.getItem(i));
//                        RlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        RlistAdapter.add(data);
//                    }
//                    RlistAdapter.notifyDataSetChanged();
//                }
                sensorData.setEuler(Roll,Pitch,Yaw,Address);
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
//                synchronized (LlistAdapter)
//                {
//                    int i;
//                    for (i = 0; i < LlistAdapter.getCount(); i++)
//                    {
//                        if (LlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < LlistAdapter.getCount())
//                    {
//                        LlistAdapter.remove(LlistAdapter.getItem(i));
//                        LlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        LlistAdapter.add(data);
//                    }
//                    LlistAdapter.notifyDataSetChanged();
//                }
                sensorData.setAcc(AccX,AccY,AccZ,Address);
            }
            if(RAddress==Address)
            {
                final String ListTitle="Record :";
                final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
                final String data=ListTitle+DataValue;
//                synchronized (RlistAdapter)
//                {
//                    int i;
//                    for (i = 0; i < RlistAdapter.getCount(); i++)
//                    {
//                        if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < RlistAdapter.getCount())
//                    {
//                        RlistAdapter.remove(RlistAdapter.getItem(i));
//                        RlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        RlistAdapter.add(data);
//                    }
//                    RlistAdapter.notifyDataSetChanged();
//                }
                sensorData.setAcc(AccX,AccY,AccZ,Address);
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
//                synchronized (LlistAdapter)
//                {
//                    for (i = 0; i < LlistAdapter.getCount(); i++)
//                    {
//                        if (LlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < LlistAdapter.getCount())
//                    {
//                        LlistAdapter.remove(LlistAdapter.getItem(i));
//                        LlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        LlistAdapter.add(data);
//                    }
//                    LlistAdapter.notifyDataSetChanged();
//                }
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
//                synchronized (RlistAdapter)
//                {
//                    for (i = 0; i < RlistAdapter.getCount(); i++)
//                    {
//                        if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                            break;
//                    }
//                    if (i < RlistAdapter.getCount())
//                    {
//                        RlistAdapter.remove(RlistAdapter.getItem(i));
//                        RlistAdapter.insert(data, i);
//                    }
//                    else
//                    {
//                        RlistAdapter.add(data);
//                    }
//                    RlistAdapter.notifyDataSetChanged();
//                }
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
            Button bt = (Button)me.findViewById(R.id.Test);
            bt.setEnabled(false);
            bt = (Button)me.findViewById(R.id.Close);
            bt.setEnabled(false);
        }

        @Override
        public void DeviceValid()
        {
            Toast.makeText(me, "Device ready.",Toast.LENGTH_SHORT).show();
            Button bt = (Button)me.findViewById(R.id.Test);
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