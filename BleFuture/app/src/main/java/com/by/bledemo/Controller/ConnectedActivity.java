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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.R;
/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class ConnectedActivity extends AppCompatActivity {
    private Activity me=this;
    private String LAddress,RAddress;
    private TextView LAdd;
    private TextView RAdd;
    private ListView LeftServices;
    private ListView RightServices;
    private ArrayAdapter<String> LlistAdapter;
    private ArrayAdapter<String> RlistAdapter;
    private Controller LDevice/*,RDevice*/;
    private ControllerThread CtrlThread;
    private boolean Paused,LOp,ROp;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=(TextView)this.findViewById(R.id.LAdd);
        RAdd=(TextView)this.findViewById(R.id.RAdd);
        LeftServices=(ListView)this.findViewById(R.id.LeftServices);
        RightServices=(ListView)this.findViewById(R.id.RightServices);
        LlistAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        LeftServices.setAdapter(LlistAdapter);
        RightServices.setAdapter(RlistAdapter);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        //RAddress=inteData.getStringExtra("RightAddress");
        me.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                LAdd.setText(LAddress);
                //RAdd.setText(RAddress);
            }
        });
        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        //RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(EventListener);

        //CtrlThread=new ControllerThread(EventListener);
        //CtrlThread.start();
    }

    public void Test(View view) //Start receiving data
    {
        if((LDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured) /*&& (RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)*/)
        {
            LDevice.Open(true,true);
            //ROp=RDevice.Open(true,false);
//            if(LOp /*&& ROp*/)
//            {
//                CtrlThread.start();
//            }
        }
        Button test=(Button)this.findViewById(R.id.Test);
        test.setEnabled(false);
    }
    //    public void Disconnect(View view)
//    {
//        if(CtrlThread.LDevice.Connected() /*&& CtrlThread.RDevice.Connected()*/)
//        {
//            CtrlThread.LDevice.Close();
//            //CtrlThread.RDevice.Close();
//        }
//        CtrlThread.interrupt();
//        Button disc=(Button)this.findViewById(R.id.Disconnect);
//        disc.setEnabled(false);
//    }
    public void Close(View view)
    {
        if(LDevice.Connected() /*&& RDevice.Connected()*/)
        {
            LDevice.Close();
            //RDevice.Close();
        }
        //CtrlThread.interrupt();
        this.finish();
    }

    @Override
    protected void onResume()
    {
        if(Paused)
        {
            Button bt = (Button) me.findViewById(R.id.Close);
            bt.setEnabled(true);
            LDevice.SetControllerAddress(LAddress);
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
        }
        super.onDestroy();
    }

    public class ControllerThread extends Thread
    {
        private Controller.ControllerCallback Listener;
        public ControllerThread(Controller.ControllerCallback Listener)
        {
            this.Listener=Listener;
        }
        @Override
        public void run()
        {
            LDevice.RegisterCallback(Listener);
            //RDevice.RegisterCallback(Listener);
        }
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ)
        {

            final String ListTitle="Position :";
            final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
            synchronized (LlistAdapter)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        int i;
                        for (i = 0; i < LlistAdapter.getCount(); i++)
                        {
                            if (LlistAdapter.getItem(i).startsWith(ListTitle))
                                break;
                        }
                        if (i < LlistAdapter.getCount())
                        {
                            LlistAdapter.remove(LlistAdapter.getItem(i));
                            LlistAdapter.insert(ListTitle + DataValue, i);
                        }
                        else
                        {
                            LlistAdapter.add(ListTitle + DataValue);
                        }
                        LlistAdapter.notifyDataSetChanged();
                    }
                });

            }
        }

        @Override
        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ)
        {
            final String ListTitle="Record :";
            final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
            synchronized (LlistAdapter)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        int i;
                        for (i = 0; i < LlistAdapter.getCount(); i++)
                        {
                            if (LlistAdapter.getItem(i).startsWith(ListTitle))
                                break;
                        }
                        if (i < LlistAdapter.getCount())
                        {
                            LlistAdapter.remove(LlistAdapter.getItem(i));
                            LlistAdapter.insert(ListTitle + DataValue, i);
                        }
                        else
                        {
                            LlistAdapter.add(ListTitle + DataValue);
                        }
                        LlistAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void ControllerFingersCallback(Controller.FingersStatus Figs)
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
            synchronized (LlistAdapter)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        int i;
                        for (i = 0; i < LlistAdapter.getCount(); i++)
                        {
                            if (LlistAdapter.getItem(i).startsWith(ListTitle))
                                break;
                        }
                        if (i < LlistAdapter.getCount())
                        {
                            LlistAdapter.remove(LlistAdapter.getItem(i));
                            LlistAdapter.insert(ListTitle + DateValue, i);
                        }
                        else
                        {
                            LlistAdapter.add(ListTitle + DateValue);
                        }
                        LlistAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void ControllerKeysCallback(int Keys)
        {
            final String ListTitle = "Key :";
            final String DateValue = String.format("%d", Keys);
            synchronized (LlistAdapter)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        int i;
                        for (i = 0; i < LlistAdapter.getCount(); i++)
                        {
                            if (LlistAdapter.getItem(i).startsWith(ListTitle))
                                break;
                        }
                        if (i < LlistAdapter.getCount())
                        {
                            LlistAdapter.remove(LlistAdapter.getItem(i));
                            LlistAdapter.insert(ListTitle + DateValue, i);
                        }
                        else
                        {
                            LlistAdapter.add(ListTitle + DateValue);
                        }
                        LlistAdapter.notifyDataSetChanged();
                    }
                });
            }
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
        }
    };
}