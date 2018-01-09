package com.by.bledemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLeScanner;
    private Handler mHandler = new Handler();
    private int REQUEST_ENABLE=1;
    private boolean mScanning;
    private static final long SCAN_PERIOD=10000;
    private ListView LeftDeviceList;
    private ListView RightDeviceList;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<BluetoothDevice> DeviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LeftDeviceList=(ListView)this.findViewById(R.id.LeftDeviceList);
        RightDeviceList=(ListView)this.findViewById(R.id.RightDeviceList);
        DeviceArray=new ArrayList<>();
        listAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        LeftDeviceList.setAdapter(listAdapter);
        RightDeviceList.setAdapter(listAdapter);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE);
//            }
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE);
//            }
//        }

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this,"@string/NOTsup",Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mLeScanner=mBluetoothAdapter.getBluetoothLeScanner();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startScan(View view) {
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        scanLeDevice();
    }

    public void Clear(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
                DeviceArray.clear();
            }
        });
    }

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            final String data=device.getName()+"\n"+device.getAddress()+"\n"+rssi;
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!DeviceArray.contains(device)){
                            DeviceArray.add(device);
                            listAdapter.add(device.getName()+"\n"+device.getAddress()+"\n"+rssi);
                        }
                        else {
                            for(int i=0;i<listAdapter.getCount();i++){
                                if(listAdapter.getItem(i).startsWith(data)){
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

    private void scanLeDevice() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
        mScanning = true;

        mBluetoothAdapter.startLeScan(mLeScanCallback);
        Log.i("scanLeDevice()","Start Scan");
    }

}
