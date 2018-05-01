package com.serverpit.wolf.controller;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.serverpit.wolf.BLE.DeviceListener;
//import com.serverpit.wolf.controller.Controller;
/**
 * Created by Wolf on 2017/5/15.
 */
public class ConnectedActivity extends AppCompatActivity {
    private Activity me = this;
    private ListView ServicesList;
    private String DevAdd;
    private ArrayAdapter<String> listAdapter;
    private Controller Device;
    private boolean Paused;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectedpag);
        Intent intent = this.getIntent();
        DevAdd = intent.getStringExtra("Address");
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ServicesList = (ListView)this.findViewById(R.id.ServicesList);
        ServicesList.setAdapter(listAdapter);
        Paused = false;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        Device = new Controller(this, DevAdd, bluetoothManager);
        Device.RegisterCallback(EventsListener);
    }
    public void on_p2_button_CloseClick(View arg0)
    {
        if(Device.Connected())
        {
            Device.Close();
        }
        this.finish();
    }
    public void on_p2_buttonTestClick(View arg0)//Start Receiving Data
    {
        if(Device.GetCurrentStatus() == Controller.Status.DeviceConfigured)
        {
            Device.Open(true, false);
        }
        Button bt = (Button)me.findViewById(R.id.p2_buttonTest);
        bt.setEnabled(false);
    }
    public void on_p2_buttonDiconcClick(View arg0)
    {
        if(Device.Connected())
        {
            Device.Close();
        }
        Button bt = (Button)me.findViewById(R.id.p2_button_disc);
        bt.setEnabled(false);
    }
    @Override
    protected void onResume() {
        if(Paused) {
            Button bt = (Button) me.findViewById(R.id.p2_button_disc);
            bt.setEnabled(true);
            Device.SetControllerAddress(DevAdd);
            Paused = false;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(Device.Connected())
        {
            Device.Close();
            Paused = true;
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if(Device.Connected())
        {
            Device.Close();
        }
        super.onDestroy();
    }
    private Controller.ControllerCallBack EventsListener = new Controller.ControllerCallBack() {//Data callbacks, All in UI Thread
        @Override
        public void ControllerStatusCallBack(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ) {
            int i;
            final String ListTitle = "Position :";
            final String DateValue = String.format("(%d,0x%02x)Roll:%1.4f,\tPitch:%1.4f,\tYaw:%1.4f,\tDisX:%3.2f,\tDisY:%3.2f,\tDisZ:%3.2f", Status, CMD, Roll, Pitch,Yaw,DisX, DisY, DisZ);
            synchronized (listAdapter) {
                for (i = 0; i < listAdapter.getCount(); i++) {
                    if (listAdapter.getItem(i).startsWith(ListTitle))
                        break;
                }
                if (i < listAdapter.getCount()) {
                    listAdapter.remove(listAdapter.getItem(i));
                    listAdapter.insert(ListTitle + DateValue, i);
                } else {
                    listAdapter.add(ListTitle + DateValue);
                }
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void ControllerOtherCallBack(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ) {

        }

        @Override
        public void ControllerFigCallBack(Controller.FingersStatus Figs) {
            int i, j;
            final String ListTitle = "Fig :";
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
            synchronized (listAdapter) {
                for (i = 0; i < listAdapter.getCount(); i++) {
                    if (listAdapter.getItem(i).startsWith(ListTitle))
                        break;
                }
                if (i < listAdapter.getCount()) {
                    listAdapter.remove(listAdapter.getItem(i));
                    listAdapter.insert(ListTitle + DateValue, i);
                } else {
                    listAdapter.add(ListTitle + DateValue);
                }
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void ControllerKeysCallBack(int Keys) {
            int i;
            final String ListTitle = "Key :";
            final String DateValue = String.format("%d", Keys);
            synchronized (listAdapter) {
                for (i = 0; i < listAdapter.getCount(); i++) {
                    if (listAdapter.getItem(i).startsWith(ListTitle))
                        break;
                }
                if (i < listAdapter.getCount()) {
                    listAdapter.remove(listAdapter.getItem(i));
                    listAdapter.insert(ListTitle + DateValue, i);
                } else {
                    listAdapter.add(ListTitle + DateValue);
                }
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void LostConnection() {
            Toast.makeText(me, "Device lost connection.",Toast.LENGTH_SHORT).show();
            Button bt = (Button)me.findViewById(R.id.p2_buttonTest);
            bt.setEnabled(false);
            bt = (Button)me.findViewById(R.id.p2_button_disc);
            bt.setEnabled(false);
        }

        @Override
        public void DeviceVailid() {
            Toast.makeText(me, "Device ready.",Toast.LENGTH_SHORT).show();
            Button bt = (Button)me.findViewById(R.id.p2_buttonTest);
            bt.setEnabled(true);
        }
    };
}
