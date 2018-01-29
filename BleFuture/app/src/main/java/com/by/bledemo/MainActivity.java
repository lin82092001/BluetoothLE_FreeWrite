package com.by.bledemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.Controller.ConnectedActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public enum BluetoothState
    {
        SELECT_DEVICE,CONNECTING
    }
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothState state=BluetoothState.SELECT_DEVICE;
    private Handler mHandler = new Handler();
    private int REQUEST_ENABLE_BT=1;
    private static final int REQUEST_ENABLE_ACCESS_COARSE = 2;
    private static final int REQUEST_ENABLE_ACCESS_FINE = 3;
    private boolean mScanning;
    private static final long SCAN_PERIOD=3000;    // Stops scanning after 10 seconds.
    private String LeftAddress="";
    private String RightAddress="";
    private Button Connect;
    private TextView scanState;
    private TextView LeftHand;
    private TextView RightHand;
    private ListView LeftDeviceList;      //宣告ListView物件
    private ListView RightDeviceList;
    private ArrayAdapter<String> listAdapter;   //搭配ListView將Item放入陣列中
    private ArrayList<BluetoothDevice> DeviceArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connect=(Button)this.findViewById(R.id.Connect);
        LeftHand=(TextView)this.findViewById(R.id.LeftHand);
        RightHand=(TextView)this.findViewById(R.id.RightHand);
        scanState=(TextView)this.findViewById(R.id.scanState);
        LeftDeviceList=(ListView)this.findViewById(R.id.LeftDeviceList);
        RightDeviceList=(ListView)this.findViewById(R.id.RightDeviceList);
        DeviceArray=new ArrayList<>();
        listAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        LeftDeviceList.setAdapter(listAdapter);
        RightDeviceList.setAdapter(listAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //提供應用程式存取定位權限
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_ACCESS_COARSE);
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_ACCESS_FINE);
            }
        }
         /*使用此檢查來確定設備上是否支持BLE。
            否則利用finish()關閉程式。
            然後，您可以選擇性地禁用BLE相關功能。*/
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this,R.string.NOTsup,Toast.LENGTH_SHORT).show();
            finish();
        }

        /*If BLE is supported, but disabled, then you can request that
        the user enable Bluetooth without leaving your application.
        BluetoothAdapter represents the device's own Bluetooth adapter. (the Bluetooth radio)*/
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /*Ensures Bluetooth is available on the device and it is enabled. If not,
          displays a dialog requesting user permission to enable Bluetooth.*/
        /*一般來說，只要使用到mBluetoothAdapter.isEnabled()就可以將BL開啟了，但此部分添加一個Result Intent
           跳出詢問視窗是否開啟BL，因此該Intenr為BluetoothAdapter.ACTION.REQUEST_ENABLE*/
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //再利用startActivityForResult啟動該Intent
        }

        //若沒開啟定位，請求使用者開啟
        final LocationManager locationManager=
                (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warning).setMessage(R.string.NOGPS).setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent enableLocation=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(enableLocation);
                }
            }).show();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                LeftDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        BluetoothDevice device=DeviceArray.get(position);
                        LeftAddress=device.getAddress();
                        LeftHand.setText(LeftAddress);
                    }
                });
                RightDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        BluetoothDevice device=DeviceArray.get(position);
                        RightAddress=device.getAddress();
                        RightHand.setText(RightAddress);
                    }
                });
                Connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (state)
                        {
                            case SELECT_DEVICE:
//                                if (LeftAddress=="" && RightAddress=="")
//                                {
//                                    new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warning).setMessage(R.string.NOdevice).setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    }).show();
//                                    break;
//                                }
//                                if (LeftAddress.equals(RightAddress))
//                                {
//                                    new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warning).setMessage(R.string.NOTsame).setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    }).show();
//                                    break;
//                                }
                                state=BluetoothState.CONNECTING;
                                break;
                            case CONNECTING:
                                connectToDevice(LeftAddress,RightAddress);
                                state=BluetoothState.SELECT_DEVICE;
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*這個Override Function是因為在onResume中使用了ActivityForResult，當使用者按了取消或確定鍵時，結果會
    返回到此onActivvityResult中，在判別requestCode判別是否==RESULT_CANCELED，如果是則finish()程式*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
    }

    //從UI自定義OnClick事件函數
    public void startScan(View view)
    {
        if (mScanning)
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        scanLeDevice(true);
    }

    public void Clear(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LeftAddress="";
                RightAddress="";
                LeftHand.setText(R.string.Left);
                RightHand.setText(R.string.Right);
                state=BluetoothState.SELECT_DEVICE;
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
                DeviceArray.clear();
            }
        });
    }

    /*As soon as you find the desired device, stop scanning.
       Never scan on a loop, and set a time limit on your scan.
       A device that was previously available may have moved out of range, and continuing to scan drains the battery.*/
    private void scanLeDevice(final boolean enable)
    {
        if(enable)
        {
            // Stops scanning after a pre-defined scan period.
            //啟動一個Handler，並使用postDelayed在SCAN_PERIOD秒後自動執行此Runnable()
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scanState.setText("After "+SCAN_PERIOD/1000+"s stop scan.");
                }
            }, SCAN_PERIOD);//SCAN_PERIOD為幾秒後要執行此Runnable
            scanState.setText(R.string.startScan);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else
        {
            if(mBluetoothAdapter!=null)
            {
                mScanning=false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }
    /*Here is an implementation of the BluetoothAdapter.LeScanCallback,
        which is the interface used to deliver BLE scan results*/
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            final String data=device.getName()+"\n"+device.getAddress()+"\n"+rssi;
            runOnUiThread(new Runnable()
            {
                    @Override
                    public void run()
                    {
                        if(!DeviceArray.contains(device))
                        {
                            DeviceArray.add(device);
                            listAdapter.add(device.getName()+"\n"+device.getAddress()+"\n"+rssi);
                        }
                        else
                        {
                            for(int i=0;i<listAdapter.getCount();i++)
                            {
                                if(listAdapter.getItem(i).startsWith(data))
                                {
                                    listAdapter.remove(listAdapter.getItem(i));
                                    listAdapter.insert(data,i);
                                    break;
                                }
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                        Log.i("onLeScan", "Find BLE Device:  " + device.getName() + " RSSI: " + rssi);
                    }
                });
        }
    };

    public void connectToDevice(String LAddress,String RAddress)
    {
        //建立一個Intent，將從此Activity進到ConnectedActivity中在ConnectedActivity中將與BLE Device連線，並互相溝通
        Intent intent=new Intent(MainActivity.this, ConnectedActivity.class);
        //將兩個device address存到ConnectedActivity，以供ConnectedActivity使用
        intent.putExtra("LeftAddress",LAddress);
        intent.putExtra("RightAddress",RAddress);
        scanLeDevice(false);// will stop after first device detection
        startActivity(intent);
    }

}
