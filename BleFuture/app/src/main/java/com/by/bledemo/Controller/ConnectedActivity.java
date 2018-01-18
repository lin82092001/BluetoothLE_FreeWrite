package com.by.bledemo.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private boolean Paused;
    private Controller LDevice,RDevice; //Declare two device

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

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        RAddress=inteData.getStringExtra("RightAddress");

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                LAdd.setText(LAddress);
                RAdd.setText(RAddress);
            }
        });
    }

    public void Test(View view){

    }
    public void Disconnect(View view){

    }
    public void Close(View view){
        this.finish();
    }
}
