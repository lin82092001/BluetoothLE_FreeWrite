package com.by.bledemo.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.util.stream.*;
import com.by.bledemo.DataProcess.CombinationWordRecognition;
import com.by.bledemo.DataProcess.RecognitionWorker;
import com.by.bledemo.DataProcess.VoiceData;
import com.by.bledemo.MainActivity;
import com.by.bledemo.R;
import java.util.stream.IntStream;

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


    private String Word="";

    private String LeftDirect="";
    //private String LeftDirectCode =  "00";
    private String LeftFigCode[] = {"0", "00", "00", "00", "00"};
    private String LeftFigCodeTotal="000000000";

    private int[] LAcc = {0, 0, 0, 0};

    private String RightDirect="";
    //private String RightDirectCode =  "00";
    private String RightFigCode[] = {"0", "00", "00", "00", "00"};
    private String RightFigCodeTotal="000000000";

    private int[] RAcc = {0, 0, 0, 0};

    private int ExchangWord = -1;
    private RecognitionWorker RecognitionWorker;

    private int MatchingTime = 0;
    private int MatchedTime = 100;
    private String CombinationWordTemp = "";

    private float MotionSetValue = 5;

    private float[] LFDegReg = {00, 00, 00, 00};
    private float[] RFDegReg = {00, 00, 00, 00};

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
        RAdd=this.findViewById(R.id.RAdd);
        LeftServices=this.findViewById(R.id.LeftServices);
        RightServices=this.findViewById(R.id.RightServices);
        Paused=false;

        final BluetoothManager bluetoothManager=
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        Intent inteData=this.getIntent();
        LAddress=inteData.getStringExtra("LeftAddress");    //Get device address from MainActivity
        RAddress=inteData.getStringExtra("RightAddress");

        LDevice=new Controller(ConnectedActivity.this,LAddress,bluetoothManager);
        RDevice=new Controller(ConnectedActivity.this,RAddress,bluetoothManager);
        LDevice.RegisterCallback(EventListener);
        RDevice.RegisterCallback(EventListener);

        Spinner spinner = (Spinner)findViewById(R.id.LanquageSelector);
        final String[] Lanquage = {"Chinese", "English"};
        ArrayAdapter<String> LanquageList = new ArrayAdapter<>(ConnectedActivity.this, android.R.layout.simple_spinner_dropdown_item, Lanquage);
        //int Spinner1i = LanquageList.getPosition("Chinese");
        spinner.setAdapter(LanquageList);
        //spinner.setSelection(Spinner1i, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecognitionWorker = null;
                RecognitionWorker = new RecognitionWorker(Lanquage[position]);
                Word = Lanquage[position];
                RecognitionWorker.StaticVocabulary();
                RecognitionWorker.MotionVocabulary();
                RecognitionWorker.CombinationVocabulary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                RecognitionWorker = null;
                RecognitionWorker = new RecognitionWorker(Lanquage[0]);
                Word = Lanquage[0];
                RecognitionWorker.StaticVocabulary();
                RecognitionWorker.MotionVocabulary();
                RecognitionWorker.CombinationVocabulary();
            }
        });

        Spinner spinner2 = (Spinner)findViewById(R.id.DelayTime);
        final String[] DelayTime = {"40", "60", "80", "100", "120", "140", "160"};
        ArrayAdapter<String> DelayTimeList = new ArrayAdapter<String>(ConnectedActivity.this, android.R.layout.simple_spinner_dropdown_item, DelayTime);
        int Spinner2i = DelayTimeList.getPosition("100");
        spinner2.setAdapter(DelayTimeList);
        spinner2.setSelection(Spinner2i, true);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MatchedTime = Integer.valueOf(DelayTime[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                MatchedTime = 100;
            }
        });
/*
        Button SetButton00 = findViewById(R.id.Set00);
        SetButton00.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

        Button SetButton01 = findViewById(R.id.Set01);
        SetButton01.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

        Button SetButton10 = findViewById(R.id.Set10);
        SetButton10.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

        Button SetButton11 = findViewById(R.id.Set11);
        SetButton11.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });*/
    }
    public void Test(View view) //Start receiving data
    {
        if(LOp && ROp)
        {

        }
    }

    public void Close(View view)
    {
        if(LDevice.Connected() && RDevice.Connected())
        {
            LDevice.Close();
            RDevice.Close();
        }
        this.finish();
    }

    @Override
    protected void onResume()
    {
        if(Paused)
        {
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
            Paused=true;
        }
        super.onDestroy();
    }

    private Controller.ControllerCallback EventListener=new Controller.ControllerCallback() {   //資料回傳函數
        @Override
        public void ControllerSignCallback(int Status, int CMD, float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ, Controller.FingersStatus Figs, String Address)
        {
            int[] LFig = {0,0,0,0,0};
            int[] RFig = {0,0,0,0,0};
            if(LAddress == Address)
            {
                //x y z Rotat Value
                final String LeftPosDataValue = String.format("\nRoll:%1.0f,\nPitch:%1.0f,\nYaw:%1.0f\n", Roll, Pitch, Yaw);
                //acc
                //final String RecDataValue = String.format("AccX:%f,\nAccY:%f,\nAccZ:%f\n",AccX,AccY,AccZ);

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

                //FigCode
                for(int i = 0; i < 5; i++)
                {
                    if(Figs.Enable[i][0]){
                        if(i == 4)
                        {
                            LFig[i] = Figs.Degree[i][0];
                            if (Figs.Degree[i][0] < 30)
                            {
                                LeftFigCode[i] = "0";
                            }
                            else
                            {
                                LeftFigCode[i] = "1";
                            }
                        }
                        else
                        {
                            LFig[i] = Figs.Degree[i][0];
                            if(Figs.Degree[i][0] <= 35)
                            {
                                LeftFigCode[i] = "00";
                            }
                            else if(36 <= Figs.Degree[i][0] && Figs.Degree[i][0] <= 60)
                            {
                                LeftFigCode[i] = "01";
                            }
                            else if(61 <= Figs.Degree[i][0] && Figs.Degree[i][0] <= 85)
                            {
                                LeftFigCode[i] = "10";
                            }
                            else
                            {
                                LeftFigCode[i] = "11";
                            }
                        }
                    }
                }
                LeftFigCodeTotal = LeftFigCode[4];
                for(int i = 3; i >= 0; i--){
                    LeftFigCodeTotal = LeftFigCodeTotal + LeftFigCode[i];
                }
                //FigCode

                //面相
                final String LeftDirection = String.format("\n\"%s\"\n", LeftDirect);
                //

                //加速度
                LAcc[0] = (int)AccX*10;
                LAcc[1] = (int)AccY*10;
                LAcc[2] = (int)AccZ*10;
                LAcc[3] = LAcc[0] + LAcc[1] + LAcc[2];
                final String Acc = String.format("AccX:%d\nAccY:%d\nAccZ:%d\n",LAcc[0],LAcc[1],LAcc[2]);
                //加速度

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        LeftServices.setText(LeftFigCodeTotal + LeftDirection + Acc + LeftPosDataValue);
                    }
                });
            }

            //Right
            if(RAddress == Address)
            {
                final String PosDataValue=String.format("\nRoll:%1.4f,\nPitch:%1.4f,\nYaw:%1.4f", Roll, Pitch,Yaw);
                //final String RecDataValue = String.format("AccX:%f,\nAccY:%f,\nAccZ:%f",AccX,AccY,AccZ);

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
                    if(Figs.Enable[i][0]){
                        if(i == 0)
                        {
                            RFig[i] = Figs.Degree[i][0];
                            if (Figs.Degree[i][0] < 30)
                            {
                                RightFigCode[i] = "0";
                            }
                            else
                            {
                                RightFigCode[i] = "1";
                            }
                        }
                        else
                        {
                            RFig[i] = Figs.Degree[i][0];
                            if(Figs.Degree[i][0] <= 35)
                            {
                                RightFigCode[i] = "00";
                            }
                            else if(36 <= Figs.Degree[i][0] && Figs.Degree[i][0] <= 60)
                            {
                                RightFigCode[i] = "01";
                            }
                            else if(61 <= Figs.Degree[i][0] && Figs.Degree[i][0] <= 85)
                            {
                                RightFigCode[i] = "10";
                            }
                            else
                            {
                                RightFigCode[i] = "11";
                            }
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

                //加速度
                RAcc[0] = (int)AccX*10;
                RAcc[1] = (int)AccY*10;
                RAcc[2] = (int)AccZ*10;
                RAcc[3] = RAcc[0] + RAcc[1] + RAcc[2];
                final String Acc = String.format("AccX:%d\nAccY:%d\nAccZ:%d\n",RAcc[0],RAcc[1],RAcc[2]);
                //加速度

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        RightServices.setText(RightFigCodeTotal + RightDirection + Acc + PosDataValue);

                    }
                });
            }

            //test
            //RightDirect = "DontCare";
            //RightFigCodeTotal = "000000000";
            //LeftFigCodeTotal = "000000000";
            //LeftDirect = "DontCare";
            //test
            String changeWord = "";
            int totalAcc = Math.abs(LAcc[3]+RAcc[3]);
            //NotLineToast(ConnectedActivity.this, String.valueOf(RecognitionWorker.handRecognitions.size()), 1);
            //Recognize
            if(totalAcc < MotionSetValue)//如果加速度計超過動作門檻值則為動態手勢
            {
                for (int Loop1 = 0; Loop1 < RecognitionWorker.handRecognitions.size(); Loop1++)
                {
                    //NotLineToast(ConnectedActivity.this, "1", 1);

                    //Match
                    if (RecognitionWorker.handRecognitions.get(Loop1).MultiMatcher(LeftDirect, RightDirect, LeftFigCodeTotal, RightFigCodeTotal) == true)
                    {
                        //Match times process

                        MatchingTime = MatchingTime + 1;
                    /*if(ExchangWord == -1)
                    {
                        ExchangWord = Loop1;
                    }else if(ExchangWord != Loop1)
                    {
                        MatchingTime = 0;
                        ExchangWord = -1;
                    }*/

                        if (MatchingTime >= MatchedTime)//第一次配對成功
                        {
                            MatchingTime = 0;

                            if (CombinationWordTemp.length() == 0)//第一次配對紀錄
                            {
                                if (Word.equals("Chinese"))//切換語系顯示
                                    changeWord = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                                else
                                    changeWord = RecognitionWorker.handRecognitions.get(Loop1).EnglishWord.toString();

                                CombinationWordTemp = changeWord;
                                final String OutputWord = CombinationWordTemp;
                                NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, R.raw.prompt);

                            } else if (RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString() == CombinationWordTemp ||
                                    RecognitionWorker.handRecognitions.get(Loop1).EnglishWord.toString() == CombinationWordTemp)//兩次配對相同，靜態手勢
                            {
                                if (Word.equals("Chinese"))
                                    changeWord = RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString();
                                else
                                    changeWord = RecognitionWorker.handRecognitions.get(Loop1).EnglishWord.toString();

                                final String OutputWord = changeWord;
                                NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.handRecognitions.get(Loop1).mp3ID);
                                CombinationWordTemp = "";

                            } else//不是第一次配對，且兩次配對不同，可能為組合字
                            {
                                for (int Loop2 = 0; Loop2 < RecognitionWorker.combinationWordRecognitions.size(); Loop2++)//查找組合字庫
                                {
                                    if (RecognitionWorker.combinationWordRecognitions.get(Loop2).MultiMatcher(CombinationWordTemp, RecognitionWorker.handRecognitions.get(Loop1).ChineseWord.toString()) == true ||
                                            RecognitionWorker.combinationWordRecognitions.get(Loop2).MultiMatcher(CombinationWordTemp, RecognitionWorker.handRecognitions.get(Loop1).EnglishWord.toString()) == true)
                                    {
                                        if (Word.equals("Chinese"))
                                            changeWord = RecognitionWorker.combinationWordRecognitions.get(Loop2).ChineseWord.toString();
                                        else
                                            changeWord = RecognitionWorker.combinationWordRecognitions.get(Loop2).EnglishWord.toString();

                                        final String OutputWord = changeWord;
                                        NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                        RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.combinationWordRecognitions.get(Loop2).mp3ID);
                                        break;
                                    }
                                }
                                CombinationWordTemp = "";
                            }
                        }
                        break;
                    }
                }
            }
            else
            {
                for (int Loop1 = 0; Loop1 < RecognitionWorker.motionRecognitions.size(); Loop1++)
                {
                    //NotLineToast(ConnectedActivity.this, "1", 1);

                    //Match
                    if (RecognitionWorker.motionRecognitions.get(Loop1).TotalMatcher(LeftDirect, RightDirect, LeftFigCodeTotal, RightFigCodeTotal,
                            LAcc[0], LAcc[1], LAcc[2],
                            RAcc[0], RAcc[1], RAcc[2],
                            LFig[0], LFig[1], LFig[2], LFig[3], LFig[4],
                            RFig[0], RFig[1], RFig[2], RFig[3], RFig[4]) == true)
                    {
                        //Match times process

                        MatchingTime = MatchingTime + 1;

                        if (MatchingTime >= MatchedTime)//第一次配對成功
                        {
                            MatchingTime = 0;

                            if (CombinationWordTemp.length() == 0)//第一次配對紀錄
                            {
                                if (Word.equals("Chinese"))//切換語系顯示
                                    changeWord = RecognitionWorker.motionRecognitions.get(Loop1).ChineseWord.toString();
                                else
                                    changeWord = RecognitionWorker.motionRecognitions.get(Loop1).EnglishWord.toString();

                                CombinationWordTemp = changeWord;
                                final String OutputWord = CombinationWordTemp;
                                NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, R.raw.prompt);

                            } else if (RecognitionWorker.motionRecognitions.get(Loop1).ChineseWord.toString() == CombinationWordTemp ||
                                    RecognitionWorker.motionRecognitions.get(Loop1).EnglishWord.toString() == CombinationWordTemp)//兩次配對相同，靜態手勢
                            {
                                if (Word.equals("Chinese"))
                                    changeWord = RecognitionWorker.motionRecognitions.get(Loop1).ChineseWord.toString();
                                else
                                    changeWord = RecognitionWorker.motionRecognitions.get(Loop1).EnglishWord.toString();

                                final String OutputWord = changeWord;
                                NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.motionRecognitions.get(Loop1).mp3ID);
                                CombinationWordTemp = "";

                            } else//不是第一次配對，且兩次配對不同，可能為組合字
                            {
                                for (int Loop2 = 0; Loop2 < RecognitionWorker.combinationWordRecognitions.size(); Loop2++)//查找組合字庫
                                {
                                    if (RecognitionWorker.combinationWordRecognitions.get(Loop2).MultiMatcher(CombinationWordTemp, RecognitionWorker.motionRecognitions.get(Loop1).ChineseWord.toString()) == true ||
                                            RecognitionWorker.combinationWordRecognitions.get(Loop2).MultiMatcher(CombinationWordTemp, RecognitionWorker.motionRecognitions.get(Loop1).EnglishWord.toString()) == true)
                                    {
                                        if (Word.equals("Chinese"))
                                            changeWord = RecognitionWorker.combinationWordRecognitions.get(Loop2).ChineseWord.toString();
                                        else
                                            changeWord = RecognitionWorker.combinationWordRecognitions.get(Loop2).EnglishWord.toString();

                                        final String OutputWord = changeWord;
                                        NotLineToast(ConnectedActivity.this, OutputWord, 1);
                                        RecognitionWorker.VoiceData.Speaker(ConnectedActivity.this, RecognitionWorker.combinationWordRecognitions.get(Loop2).mp3ID);
                                        break;
                                    }
                                }
                                CombinationWordTemp = "";
                            }
                        }
                        break;
                    }
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
            if(RDevice.GetCurrentStatus()==Controller.Status.DeviceConfigured)
            {
                RAdd.setText(RDevice.DeviceName());
                ROp=RDevice.Open(true,true);
            }
        }
    };
}