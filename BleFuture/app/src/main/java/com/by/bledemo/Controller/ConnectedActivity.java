package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.by.bledemo.DataProcess.CombinationWordRecognition;
import com.by.bledemo.DataProcess.RecognitionWorker;
import com.by.bledemo.DataProcess.VoiceData;
import com.by.bledemo.R;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by zxcv1 on 2018/5/10
 */

public class ConnectedActivity extends AppCompatActivity {
    private Activity me=this;
    private String LAddress,RAddress;
    private TextView LAdd;
    private TextView RAdd;
    private TextView LeftServices;
    private TextView RightServices;
    private Controller LDevice,RDevice;
    private boolean Paused,LOp,ROp;


    //int MotherBoardEuler;

    private String LeftDirect="";
    //private String LeftDirectCode =  "00";
    private String LeftFigCode[] = {"0", "00", "00", "00", "00"};
    private String LeftFigCodeTotal;

    private String RightDirect="";
    //private String RightDirectCode =  "00";
    private String RightFigCode[] = {"0", "00", "00", "00", "00"};
    private String RightFigCodeTotal;

    private int ExchangWord = -1;
    private RecognitionWorker RecognitionWorker = new RecognitionWorker("TW");

    private int MatchingTime = 0;
    private String CombinationWordTemp = "";

    //new Toast
    private static Toast toast;
    private static void NotLineToast(final Context context, final String text, final int ShowTime){

        if(toast == null){
            toast = android.widget.Toast.makeText(context, text, ShowTime);
        }else{
            toast.setText(text);
            toast.setDuration(ShowTime);
        }
        toast.show();
    }
    //new Toast

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.connected);
        LAdd=this.findViewById(R.id.LAdd);
        //RAdd=this.findViewById(R.id.RAdd);
        LeftServices=this.findViewById(R.id.LeftServices);
        //RightServices=this.findViewById(R.id.RightServices);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        //RAddress=inteData.getStringExtra("RightAddress");

        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        //RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(EventListener);
        //RDevice.RegisterCallback(EventListener);
    }
    public void Test(View view) //Start receiving data
    {
        if(LOp && ROp)
        {

        }
    }

    public void Close(View view)
    {
        if(LDevice.Connected()/* && RDevice.Connected()*/)
        {
            LDevice.Close();
            //RDevice.Close();
        }
        this.finish();
    }

    @Override
    protected void onResume()
    {
        if(Paused)
        {
            LDevice.SetControllerAddress(LAddress);
            //RDevice.SetControllerAddress(RAddress);
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
            Paused=true;
        }
        super.onDestroy();
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerSignCallback(int Status, int CMD, float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ, Controller.FingersStatus Figs, String Address)
        {

            if(LAddress == Address)
            {
                //x y z Rotat Value
                final String LeftPosDataValue = String.format("(%d,0x%02x)\nRoll:%1.0f,\nPitch:%1.0f,\nYaw:%1.0f\n", Status, CMD, Roll, Pitch, Yaw);
                //not use
                final String RecDataValue = String.format("AccX:%f,\nAccY:%f,\nAccZ:%f\n",AccX,AccY,AccZ);

                //面相
                //PitchProcess
                if(-25 < Pitch && Pitch < 35)
                {
                    if(-45 < Roll && Roll < 45)
                    {
                        LeftDirect = "Downward";

                    }else if(-135 < Roll && Roll < -45)
                    {
                        LeftDirect = "Outward";

                    }else if(45 < Roll && Roll < 135)
                    {
                        LeftDirect = "Inward";

                    } else if(135 < Roll || Roll < -135)
                    {
                        LeftDirect = "Upward";

                    }
                    //RollProcess
                }else if(Pitch < -45)
                {
                    LeftDirect = "DontCare";

                }else if(50 < Pitch)
                {
                    LeftDirect = "Raise";

                }
                //面向


                //與主板歐拉角相剪
                //MotherBoardEuler = Math.round(Roll + Yaw + Pitch);
                /*
                for(int i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0] && !Figs.Enable[i][1])
                    {
                        Figs.Degree[i][0] = MotherBoardEuler - Figs.Degree[i][0];
                    }else
                    {
                        Figs.Degree[i][0] = MotherBoardEuler - Figs.Degree[i][0];
                        Figs.Degree[i][1] = Figs.Degree[i][0] - (MotherBoardEuler - Figs.Degree[i][1]);
                    }
                }
                */

                //FigCode

                //String teststr="\n";
                for(int i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0] && !Figs.Enable[i][1])
                    {
                        //teststr = String.format("%s%d\n%d\n", teststr, Figs.Degree[i][0], Math.abs(MotherBoardEuler));
                        if(Figs.Degree[i][0] > 45){
                            LeftFigCode[i] = "0";
                        }else{
                            LeftFigCode[i] = "1";
                        }
                    }else
                    {
                        if(Figs.Degree[i][0] > 45 && Figs.Degree[i][1] > 45){
                            LeftFigCode[i] = "11";
                        }else if(Figs.Degree[i][0] < 45 && Figs.Degree[i][1] > 45){
                            LeftFigCode[i] = "01";
                        }else if(Figs.Degree[i][0] > 45 && Figs.Degree[i][1] < 45){
                            LeftFigCode[i] = "10";
                        }else{
                            LeftFigCode[i] = "00";
                        }
                    }
                }
                LeftFigCodeTotal = LeftFigCode[0];
                for(int i = 3; i > 0; i--){
                    LeftFigCodeTotal = LeftFigCodeTotal + LeftFigCode[i];
                }
                LeftFigCodeTotal = LeftFigCodeTotal + LeftFigCode[4];

                //final String testString = teststr;
                //FigCode

                //面相
                final String LeftDirection = String.format("\n\"%s\"\n", LeftDirect);
                //

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        LeftServices.setText(LeftFigCodeTotal + LeftDirection  + LeftPosDataValue);
                    }
                });
            }

            //Right
            if(RAddress == Address)
            {
                final String PosDataValue=String.format("(%d,0x%02x)\nRoll:%1.4f,\nPitch:%1.4f,\nYaw:%1.4f", Status, CMD, Roll, Pitch,Yaw);
                final String RecDataValue = String.format("AccX:%f,\nAccY:%f,\nAccZ:%f",AccX,AccY,AccZ);

                //面向
                //PitchProcess
                if(-45 < Pitch && Pitch < 45)
                {
                    //PitchDirect_Flag = 0;
                    //PitchDirect = "OnChest";
                    //RollProcess
                    if(-45 < Roll && Roll < 45)
                    {
                        RightDirect = "Downward";

                    }else if(-135 < Roll && Roll < -45)
                    {
                        RightDirect = "Inward";

                    }else if(45 < Roll && Roll < 135)
                    {
                        RightDirect = "Outward";

                    } else if(135 < Roll || Roll < -135)
                    {
                        RightDirect = "Upward";

                    }
                    //RollProcess
                }else if(Pitch < -45)
                {
                    RightDirect = "DontCare";

                }else if(45 < Pitch)
                {
                    RightDirect = "Raise";

                }
                //面向

                //FigCode
                for(int i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0] && !Figs.Enable[i][1])
                    {
                        if(Figs.Degree[i][0] > 45){
                            RightFigCode[i] = "1";
                        }else{
                            RightFigCode[i] = "0";
                        }
                    }else
                    {
                        if(Figs.Degree[i][0] > 45 && Figs.Degree[i][1] > 45){
                            RightFigCode[i] = "11";
                        }else if(Figs.Degree[i][0] < 45 && Figs.Degree[i][1] > 45){
                            RightFigCode[i] = "01";
                        }else if(Figs.Degree[i][0] > 45 && Figs.Degree[i][1] < 45){
                            RightFigCode[i] = "10";
                        }else{
                            RightFigCode[i] = "00";
                        }
                    }
                }
                RightFigCodeTotal = RightFigCode[0];
                for(int i = 1; i < 5; i++){
                    RightFigCodeTotal = RightFigCodeTotal + RightFigCode[i];
                }
                //FigCode

                //面相
                final String RightDirection = String.format("\n\"%s\"\n", RightDirect);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        RightServices.setText(RightFigCodeTotal + RightDirection + PosDataValue);

                    }
                });
            }
            //test
            RightDirect = "DontCare";
            LeftFigCodeTotal = "123456789";
            RightFigCodeTotal = "000000000";
            //test

            //NotLineToast(ConnectedActivity.this, String.valueOf(RecognitionWorker.handRecognitions.size()), 1);
            //Recognize
            for (int Loop1 = 0; Loop1 < RecognitionWorker.handRecognitions.size(); Loop1++) {
                //NotLineToast(ConnectedActivity.this, "1", 1);

                //Match
                if(RecognitionWorker.handRecognitions.get(Loop1).MultiMatcher(LeftDirect, RightDirect, LeftFigCodeTotal, RightFigCodeTotal) == true)
                {
                    //Match times process
                    MatchingTime = MatchingTime + 1;
                    if(ExchangWord == -1)
                    {
                        ExchangWord = Loop1;
                    }else if(ExchangWord != Loop1)
                    {
                        MatchingTime = 0;
                        ExchangWord = -1;
                    }

                    if(MatchingTime >= 100)//第一次配對成功
                    {
                        MatchingTime = 0;

                        if(CombinationWordTemp.length() == 0)//第一次配對紀錄
                        {
                            CombinationWordTemp = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                            final String OutputWord = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                            NotLineToast(ConnectedActivity.this, OutputWord, 1);
                            RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, R.raw.prompt);

                        }else if(RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString() == CombinationWordTemp)//兩次配對相同，靜態手勢
                        {
                            final String OutputWord = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                            NotLineToast(ConnectedActivity.this, OutputWord, 1);
                            RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.handRecognitions.get(Loop1).mp3ID);
                            CombinationWordTemp = "";

                        }else//不是第一次配對，且兩次配對不同，可能為組合字
                        {
                            for(int Loop2 = 0; Loop2 < RecognitionWorker.combinationWordRecognitions.size(); Loop2++)//查找組合字庫
                            {
                                if(RecognitionWorker.combinationWordRecognitions.get(Loop2).MultiMatcher(CombinationWordTemp, RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString()) == true)
                                {
                                    final String OutputWord = RecognitionWorker.combinationWordRecognitions.get(Loop2).ChineseWord.toString();
                                    NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                    RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.combinationWordRecognitions.get(Loop2).mp3ID);
                                }
                                break;
                            }
                            CombinationWordTemp = "";
                        }
                        //final String OutputWord = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                        //NotLineToast(ConnectedActivity.this, OutputWord, 1);
                        //RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.handRecognitions.get(Loop1).mp3ID);
                    }
                    break;
                }
            }
            //Recognize
        }

        @Override
        public void ControllerStatusCallback(int Status, int CMD, float Roll, float Pitch, float Yaw, float DisX, float DisY, float DisZ,String Address)
        {

        }

        @Override
        public void ControllerOtherCallback(float SpeedX, float SpeedY, float SpeedZ, float AccX, float AccY, float AccZ,String Address)
        {

        }

        @Override
        public void ControllerFingersCallback(Controller.FingersStatus Figs,String Address)
        {

        }

        @Override
        public void ControllerKeysCallback(int Keys,String Address)
        {

        }

        @Override
        public void LostConnection()
        {
            Toast.makeText(me, "Device lost connection.",Toast.LENGTH_SHORT).show();
            Button bt = me.findViewById(R.id.Test);
            bt.setEnabled(false);
            bt = me.findViewById(R.id.Close);
            bt.setEnabled(false);
        }

        @Override
        public void DeviceValid()
        {
            Toast.makeText(me, "Device ready.",Toast.LENGTH_SHORT).show();
            Button bt = me.findViewById(R.id.Test);
            bt.setEnabled(true);
            if(LDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                LAdd.setText(LDevice.DeviceName());
                LOp=LDevice.Open(true,true);
            }
            /*if(RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                RAdd.setText(RDevice.DeviceName());
                ROp=RDevice.Open(true,true);
            }*/
        }
    };
}