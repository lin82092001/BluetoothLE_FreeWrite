package com.by.bledemo.DataProcess;

import android.gesture.Gesture;
import com.by.bledemo.Gesture.BasicGesture;
import com.by.bledemo.R;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by zxcv1 on 2018/4/19.
 */

public class RecognitionWorker {

    private String Upward = "Upward";
    private String Downward = "Downward";
    private String Inward = "Inward";
    private String Outward = "Outward";
    private String Raise = "Raise";
    private String DontCare = "DontCare";
    public VoiceData VoiceData = new VoiceData();

    public RecognitionWorker(String State){
        if(State.equals("TW")){
            //test
            VoiceData.you = R.raw.you;
            //test
            VoiceData.zero = R.raw.zero;
            VoiceData.one = R.raw.one;
            VoiceData.two = R.raw.two;
            VoiceData.three = R.raw.three;
            VoiceData.four = R.raw.four;
            VoiceData.five = R.raw.five;
            VoiceData.six = R.raw.six;
            VoiceData.seven = R.raw.seven;
            VoiceData.eight = R.raw.eight;
            VoiceData.nine = R.raw.nine;
            VoiceData.ten = R.raw.ten;
        }else if(State.equals("EN")){
            VoiceData.zero = R.raw.zeroen;
            VoiceData.one = R.raw.oneen;
            VoiceData.two = R.raw.twoen;
            VoiceData.three = R.raw.threeen;
            VoiceData.four = R.raw.fouren;
            VoiceData.five = R.raw.fiveen;
            VoiceData.six = R.raw.sixen;
            VoiceData.seven = R.raw.sevenen;
            VoiceData.eight = R.raw.eighten;
            VoiceData.nine = R.raw.nineen;
            VoiceData.ten = R.raw.tenen;
        }
    }
    //靜態手勢
    public ArrayList<HandRecognition> handRecognitions = new ArrayList<HandRecognition>() {{
        //參考
        //add(new HandRecognition("中文", "英文", mp3id, 左手面相, 右手面向, "左手手勢", "右手手勢"));
        //test
        add(new HandRecognition("你", "you", R.raw.you, Inward, DontCare, BasicGesture.you, BasicGesture.DontCare));
        add(new HandRecognition("你好", "hello", R.raw.good, Raise, DontCare, BasicGesture.you, BasicGesture.DontCare));
        add(new HandRecognition("我想上廁所", "toilet", R.raw.toilet, Downward, DontCare, BasicGesture.you, BasicGesture.DontCare));
        //test
        //一到十(左手
        add(new HandRecognition("零", "zero", R.raw.zero, Raise, DontCare, BasicGesture.zero, BasicGesture.DontCare));
        add(new HandRecognition("一", "one", R.raw.one, Raise, DontCare, BasicGesture.one, BasicGesture.DontCare));
        add(new HandRecognition("二", "two", R.raw.two, Raise, DontCare, BasicGesture.two, BasicGesture.DontCare));
        add(new HandRecognition("三", "three", R.raw.three, Raise, DontCare, BasicGesture.three, BasicGesture.DontCare));
        add(new HandRecognition("四", "four", R.raw.four, Raise, DontCare, BasicGesture.four, BasicGesture.DontCare));
        add(new HandRecognition("五", "five", R.raw.five, Raise, DontCare, BasicGesture.five_A, BasicGesture.DontCare));
        add(new HandRecognition("五", "five", R.raw.five, Raise, DontCare, BasicGesture.five_B, BasicGesture.DontCare));
        add(new HandRecognition("六", "six", R.raw.six, Inward, DontCare, BasicGesture.six, BasicGesture.DontCare));
        add(new HandRecognition("七", "seven", R.raw.seven, Inward, DontCare, BasicGesture.seven, BasicGesture.DontCare));
        add(new HandRecognition("八", "eight", R.raw.eight, Inward, DontCare, BasicGesture.eight, BasicGesture.DontCare));
        add(new HandRecognition("九", "nine", R.raw.nine, Inward, DontCare, BasicGesture.nine, BasicGesture.DontCare));
        add(new HandRecognition("十", "ten", R.raw.ten, Raise, DontCare, BasicGesture.ten_N, BasicGesture.DontCare));
        add(new HandRecognition("十", "ten", R.raw.ten, Raise, DontCare, BasicGesture.ten_S, BasicGesture.DontCare));
        //一到十(右手
        add(new HandRecognition("零", "zero", R.raw.zero, DontCare, Raise, BasicGesture.DontCare, BasicGesture.zero));
        add(new HandRecognition("一", "one", R.raw.one, DontCare, Raise, BasicGesture.DontCare, BasicGesture.one));
        add(new HandRecognition("二", "two", R.raw.two, DontCare, Raise, BasicGesture.DontCare, BasicGesture.two));
        add(new HandRecognition("三", "three", R.raw.three, DontCare, Raise, BasicGesture.DontCare, BasicGesture.three));
        add(new HandRecognition("四", "four", R.raw.four, DontCare, Raise, BasicGesture.DontCare, BasicGesture.four));
        add(new HandRecognition("五", "five", R.raw.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_A));
        add(new HandRecognition("五", "five", R.raw.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_B));
        add(new HandRecognition("六", "six", R.raw.six, DontCare, Inward, BasicGesture.DontCare, BasicGesture.six));
        add(new HandRecognition("七", "seven", R.raw.seven, DontCare, Inward, BasicGesture.DontCare, BasicGesture.seven));
        add(new HandRecognition("八", "eight", R.raw.eight, DontCare, Inward, BasicGesture.DontCare, BasicGesture.eight));
        add(new HandRecognition("九", "nine", R.raw.nine, DontCare, Inward, BasicGesture.DontCare, BasicGesture.nine));
        add(new HandRecognition("十", "ten", R.raw.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_N));
        add(new HandRecognition("十", "ten", R.raw.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_S));
        //一到十
    }};

    //動態手勢
    public ArrayList<MotionRecognition> motionRecognitions = new ArrayList<MotionRecognition>(){{
        //參考
        //add(new MotionRecognition("中文", "英文", mp3id, "左手面相", "右手面向", "左手手勢", "右手手勢", acc, acc, acc, acc, acc, acc));

    }};

}
