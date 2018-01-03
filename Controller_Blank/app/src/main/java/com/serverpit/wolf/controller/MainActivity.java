package com.serverpit.wolf.controller;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 15000;  // Stops scanning after 10 seconds.
    private BluetoothLeScanner mLEScanner;  //finding BLE devices
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private ListView DevicesList;           //宣告ListView物件
    private ArrayAdapter<String> listAdapter;   //搭配ListView將Item放入陣列中
    private ArrayList<BluetoothDevice> DeviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DevicesList = (ListView)this.findViewById(R.id.DevicesList);
        DeviceArray = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        DevicesList.setAdapter(listAdapter);
        mHandler = new Handler();

        /*使用此檢查來確定設備上是否支持BLE。
         否則利用finish()關閉程式。
        然後，您可以選擇性地禁用BLE相關功能。*/
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        /*If BLE is supported, but disabled, then you can request that
        the user enable Bluetooth without leaving your application.
        BluetoothAdapter represents the device's own Bluetooth adapter. (the Bluetooth radio)*/
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /*Ensures Bluetooth is available on the device and it is enabled. If not,
          displays a dialog requesting user permission to enable Bluetooth.*/
        /*一般來說，只要使用到mBluetoothAdapter.isEnabled()就可以將BL開啟了，但此部分添加一個Result Intent
           跳出詢問視窗是否開啟BL，因此該Intenr為BluetoothAdapter.ACTION.REQUEST_ENABLE*/
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  //再利用startActivityForResult啟動該Intent
        }

        //點擊ListView的Item選擇要連線的裝置
        DevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                connectToDevice(DeviceArray.get(index));
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        else {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
//                filters = new ArrayList<>();
//                settings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                        .build();
//            }
//            scanLeDevice(true); //使用ScanFunction(true) 開啟BLE搜尋功能
//        }
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//            scanLeDevice(false);
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    /*這個Override Function是因為在onResume中使用了ActivityForResult，當使用者按了取消或確定鍵時，結果會
    返回到此onActivvityResult中，在判別requestCode判別是否==RESULT_CANCELED，如果是則finish()程式*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && requestCode==Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //從UI自定義OnClick事件函數
    public void OnButton_Scan_Click(View arg0)
    {
        scanLeDevice(true);
    }
    public void OnButton_Stop_Click(View arg0)
    {
        scanLeDevice(false);
    }
    public void OnButton_Clear_Click(View arg0)
    {
        listAdapter.clear();
        listAdapter.notifyDataSetChanged();
        DeviceArray.clear();
    }

    /*As soon as you find the desired device, stop scanning.
        Never scan on a loop, and set a time limit on your scan.
        A device that was previously available may have moved out of range, and continuing to scan drains the battery.*/
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            //啟動一個Handler，並使用postDelayed在SCAN_PERIOD秒後自動執行此Runnable()
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { //如果compileSdkVersion低於21則不做掃描
//                        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled())
//                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    } else {
//                        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){}
//                            mLEScanner.stopScan(mScanCallback);
//                    }
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //mLEScanner.stopScan(mScanCallback);
                    Log.d("scanLeDevice()","After "+SCAN_PERIOD/1000 +"s Stop Scan");
                }
            }, SCAN_PERIOD);//SCAN_PERIOD為幾秒後要執行此Runnable

            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //mLEScanner.startScan(filters, settings, mScanCallback);
            Log.d("scanLeDevice()","Start Scan");
        } /*else {
            //if(mLEScanner!=null && mBluetoothAdapter!=null)
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mLEScanner.stopScan(mScanCallback);
                Log.d("scanLeDevice()","Stop Scan");
        }*/
    }

    /*Here is an implementation of the BluetoothAdapter.LeScanCallback,
        which is the interface used to deliver BLE scan results*/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    //runOnUiThread(new Runnable() {
                        //@Override
                        //public void run() {
                            Log.i("onLeScan", device.getName()+":"+rssi);
                            DeviceArray.add(device);
                            //deviceName.add(device.getName()+" rssi:"+rssi+"\r\n" + device.getAddress());
                            listAdapter.add(device.getName() + ":" + device.getAddress()+", RSSI:"+rssi);
                            listAdapter.notifyDataSetChanged();
                        //}
                    //});
                }
            };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String data;
            BluetoothDevice btDevice = result.getDevice();
            data = "BT:" + btDevice.getName() + ":" + btDevice.getAddress();
            if(!DeviceArray.contains(btDevice)) {
                listAdapter.add(data+","+result.getRssi());
                DeviceArray.add(btDevice);
            }
            else
            {
                for(int i=0;i<listAdapter.getCount();i++)
                {
                    if(listAdapter.getItem(i).startsWith(data))
                    {
                        listAdapter.remove(listAdapter.getItem(i));
                        listAdapter.insert(data+","+result.getRssi(),i);
                        break;
                    }
                }
            }
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    public void connectToDevice(BluetoothDevice device) {
        //建立一個Intent，將從此Activity進到ControlActivity中
        //在ConnectedActivity中將與BLE Device連線，並互相溝通
        Intent inte = new Intent();
        inte.setClass(MainActivity.this,ConnectedActivity.class);
        //將device address存到ConnectedActivity，以供ConnectedActivity使用
        inte.putExtra("Address",device.getAddress());
        scanLeDevice(false);// will stop after first device detection
        startActivity(inte);
    }
}
