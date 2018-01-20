package com.by.bledemo.Controller;

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

import com.by.bledemo.R;
/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class ConnectedActivity extends AppCompatActivity {
    private String LAddress,RAddress;
    private TextView LAdd;
    private TextView RAdd;
    private ListView LeftServices;
    private ListView RightServices;
    private ArrayAdapter<String> listAdapter;
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
        listAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        LeftServices.setAdapter(listAdapter);
        RightServices.setAdapter(listAdapter);
        Paused=false;

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        //RAddress=inteData.getStringExtra("RightAddress");

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                LAdd.setText(LAddress);
                //RAdd.setText(RAddress);
            }
        });
        CtrlThread=new ControllerThread();
    }

    public void Test(View view) //Start receiving data
    {
        if((CtrlThread.LDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured) /*&& (CtrlThread.RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)*/)
        {
            LOp=CtrlThread.LDevice.Open(true,false);
            //ROp=CtrlThread.RDevice.Open(true,false);
            if(LOp /*&& ROp*/)
            {
                CtrlThread.start();
            }
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
        if(CtrlThread.LDevice.Connected() /*&& CtrlThread.RDevice.Connected()*/)
        {
            CtrlThread.LDevice.Close();
            //CtrlThread.RDevice.Close();
        }
        CtrlThread.interrupt();
        this.finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public class ControllerThread extends Thread
    {
        private Controller LDevice/*,RDevice*/;
        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        public ControllerThread()
        {
            LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
            //RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
            LDevice.RegisterCallback(EventListener);
            //RDevice.RegisterCallback(EventListener);
        }

        private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {
            @Override
            public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ)
            {

            }

            @Override
            public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ)
            {

            }

            @Override
            public void ControllerFingersCallback(Controller.FingersStatus Figs)
            {

            }

            @Override
            public void ControllerKeysCallback(int Keys)
            {

            }

            @Override
            public void LostConnection()
            {

            }

            @Override
            public void DeviceValid()
            {

            }
        };
    }
}