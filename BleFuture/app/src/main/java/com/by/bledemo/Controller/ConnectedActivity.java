package com.by.bledemo.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.by.bledemo.R;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public class ConnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
    }

    public void Test(View view){

    }
    public void Disconnect(View view){

    }
    public void Close(View view){
        this.finish();
    }
}
