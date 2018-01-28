package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private String Position,RecordData,Fingers,Key;
    private TextView LAdd;
    private TextView RAdd;
    private ListView LeftServices;
    private ListView RightServices;
    private ArrayAdapter<String> LlistAdapter;
    private ArrayAdapter<String> RlistAdapter;
    private Controller LDevice,RDevice;
    private ControllerThread leftThread,rightThread;
    private Context con=this;
    private boolean Paused,LOp,ROp;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=(TextView)this.findViewById(R.id.LAdd);
        RAdd=(TextView)this.findViewById(R.id.RAdd);
        LeftServices=(ListView)this.findViewById(R.id.LeftServices);
        RightServices=(ListView)this.findViewById(R.id.RightServices);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        RAddress=inteData.getStringExtra("RightAddress");
        me.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                LAdd.setText(LAddress);
                RAdd.setText(RAddress);
            }
        });
        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(LeftEventListener);
        RDevice.RegisterCallback(LeftEventListener);

        leftThread=new ControllerThread(LeftServices);
        rightThread=new ControllerThread(RightServices);
        //CtrlThread.start();
    }

    public void Test(View view) //Start receiving data
    {
        if((LDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured) && (RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured))
        {
            LDevice.Open(true,true);
            RDevice.Open(true,true);
            leftThread.start();
            rightThread.start();
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
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.Close();
            RDevice.Close();
            leftThread.interrupt();
            rightThread.interrupt();
            leftThread=null;
            rightThread=null;
        }
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
            RDevice.SetControllerAddress(RAddress);
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
            leftThread.interrupt();
            rightThread.interrupt();
            leftThread=null;
            rightThread=null;
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
            leftThread.interrupt();
            rightThread.interrupt();
            leftThread=null;
            rightThread=null;
        }
        super.onDestroy();
    }

    public class ControllerThread extends Thread
    {
        private ListView listView;
        private ArrayAdapter<String>ListAdapter;
        public ControllerThread(ListView dataList)
        {
            this.listView=dataList;
            this.ListAdapter=new ArrayAdapter<>(con,android.R.layout.simple_list_item_1);
            this.listView.setAdapter(this.ListAdapter);
        }
        @Override
        public void run()
        {
            int i;
            for (i = 0; i < ListAdapter.getCount(); i++)
            {
                if (ListAdapter.getItem(i).startsWith(i+"→"))
                    break;
            }
            if (i < ListAdapter.getCount())
            {
                ListAdapter.remove(ListAdapter.getItem(i));
                ListAdapter.insert(Position, i);
//                ListAdapter.insert(RecordData, i+1);
//                ListAdapter.insert(Fingers, i+2);
//                ListAdapter.insert(Key, i+3);
            }
            else
            {
                ListAdapter.add(Position);
//                ListAdapter.add(RecordData);
//                ListAdapter.add(Fingers);
//                ListAdapter.add(Key);
            }
            ListAdapter.notifyDataSetChanged();
        }
    }

    class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {

        }
    }

    private Controller.ControllerCallback LeftEventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ)
        {
            final String ListTitle="Position :";
            final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
            Position=ListTitle+DataValue;
        }

        @Override
        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ)
        {
            final String ListTitle="Record :";
            final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
            RecordData=ListTitle+DataValue;
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
            Fingers=ListTitle + DateValue;
        }

        @Override
        public void ControllerKeysCallback(int Keys)
        {
            final String ListTitle = "Key :";
            final String DateValue = String.format("%d", Keys);
            Key=ListTitle + DateValue;
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

//    private Controller.ControllerCallback RightEventListener=new Controller.ControllerCallback() {   //資料回傳函數
//        @Override
//        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ)
//        {
//
//            final String ListTitle="Position :";
//            final String DataValue=String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
//            synchronized (RlistAdapter)
//            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        int i;
//                        for (i = 0; i < RlistAdapter.getCount(); i++)
//                        {
//                            if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                                break;
//                        }
//                        if (i < RlistAdapter.getCount())
//                        {
//                            RlistAdapter.remove(RlistAdapter.getItem(i));
//                            RlistAdapter.insert(ListTitle + DataValue, i);
//                        }
//                        else
//                        {
//                            RlistAdapter.add(ListTitle + DataValue);
//                        }
//                        RlistAdapter.notifyDataSetChanged();
//                    }
//                });
//
//            }
//        }
//
//        @Override
//        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ)
//        {
//            final String ListTitle="Record :";
//            final String DataValue=String.format("SpeedX:%f,\tSpeedY:%f,\tSpeedZ:%f,\tAccX:%1.4f,\tAccY:%1.4f,\tAccZ:%1.4f",SpeedX,SpeedY,SpeedZ,AccX,AccY,AccZ);
//            synchronized (RlistAdapter)
//            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        int i;
//                        for (i = 0; i < RlistAdapter.getCount(); i++)
//                        {
//                            if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                                break;
//                        }
//                        if (i < RlistAdapter.getCount())
//                        {
//                            RlistAdapter.remove(RlistAdapter.getItem(i));
//                            RlistAdapter.insert(ListTitle + DataValue, i);
//                        }
//                        else
//                        {
//                            RlistAdapter.add(ListTitle + DataValue);
//                        }
//                        RlistAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void ControllerFingersCallback(Controller.FingersStatus Figs)
//        {
//            int i, j;
//            final String ListTitle = "Fingers :";
//            String Data = "";
//            for(i = 0; i < 5; i++)
//            {
//                if(Figs.Enable[i][0])
//                {
//                    if(Data.length()>0)
//                        Data = String.format("%s,Fig[%d]:%3d ", Data, i, Figs.Degree[i][0]);
//                    else
//                        Data = String.format("Fig[%d]:%3d ", i, Figs.Degree[i][0]);
//                    if(Figs.Enable[i][1])
//                    {
//                        Data = String.format("%s,Fig[%d-1]:%3d ", Data, i, Figs.Degree[i][1]);
//                    }
//                }
//            }
//            final String DateValue = Data;
//            synchronized (RlistAdapter)
//            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        int i;
//                        for (i = 0; i < RlistAdapter.getCount(); i++)
//                        {
//                            if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                                break;
//                        }
//                        if (i < RlistAdapter.getCount())
//                        {
//                            RlistAdapter.remove(RlistAdapter.getItem(i));
//                            RlistAdapter.insert(ListTitle + DateValue, i);
//                        }
//                        else
//                        {
//                            RlistAdapter.add(ListTitle + DateValue);
//                        }
//                        RlistAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void ControllerKeysCallback(int Keys)
//        {
//            final String ListTitle = "Key :";
//            final String DateValue = String.format("%d", Keys);
//            synchronized (RlistAdapter)
//            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        int i;
//                        for (i = 0; i < RlistAdapter.getCount(); i++)
//                        {
//                            if (RlistAdapter.getItem(i).startsWith(ListTitle))
//                                break;
//                        }
//                        if (i < RlistAdapter.getCount())
//                        {
//                            RlistAdapter.remove(RlistAdapter.getItem(i));
//                            RlistAdapter.insert(ListTitle + DateValue, i);
//                        }
//                        else
//                        {
//                            RlistAdapter.add(ListTitle + DateValue);
//                        }
//                        RlistAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void LostConnection()
//        {
//            Toast.makeText(me, "RDevice lost connection.",Toast.LENGTH_SHORT).show();
//            Button bt = (Button)me.findViewById(R.id.Test);
//            bt.setEnabled(false);
//            bt = (Button)me.findViewById(R.id.Close);
//            bt.setEnabled(false);
//        }
//
//        @Override
//        public void DeviceValid()
//        {
//            Toast.makeText(me, "RDevice ready.",Toast.LENGTH_SHORT).show();
//            Button bt = (Button)me.findViewById(R.id.Test);
//            bt.setEnabled(true);
//        }
//    };
}