package com.by.bledemo.DataProcess;

import android.app.Application;
import android.content.Context;
import android.gesture.Gesture;
import android.speech.tts.Voice;
import android.widget.Toast;

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

            VoiceData.you = R.raw.you;
            VoiceData.hello = R.raw.hello;
            VoiceData.good = R.raw.good;
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

            VoiceData.you = R.raw.youen;
            VoiceData.hello = R.raw.helloen;
            //VoiceData.good = R.raw.gooden;
        }
    }
    //靜態手勢
    public ArrayList<HandRecognition> handRecognitions = new ArrayList<HandRecognition>();
    public void StaticVocabulary(){
        //參考
        //handRecognitions.add(new HandRecognition("中文", "英文", mp3id, 左手面相, 右手面向, "左手手勢", "右手手勢"));
        //test
        handRecognitions.add(new HandRecognition("你", "you", VoiceData.you, Inward, DontCare, BasicGesture.you, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("_你好", "hello", R.raw.nulll, Raise, DontCare, BasicGesture.hello, BasicGesture.DontCare));
        //add(new HandRecognition("我想上廁所", "toilet", VoiceData.toilet, Downward, DontCare, BasicGesture.you, BasicGesture.DontCare));
        //test
        //一到十(左手
        handRecognitions.add(new HandRecognition("零", "zero", VoiceData.zero, Raise, DontCare, BasicGesture.zero, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("一", "one", VoiceData.one, Raise, DontCare, BasicGesture.one, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("二", "two", VoiceData.two, Raise, DontCare, BasicGesture.two, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("三", "three", VoiceData.three, Raise, DontCare, BasicGesture.three, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("四", "four", VoiceData.four, Raise, DontCare, BasicGesture.four, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, Raise, DontCare, BasicGesture.five_A, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, Raise, DontCare, BasicGesture.five_B, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("六", "six", VoiceData.six, Inward, DontCare, BasicGesture.six, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("七", "seven", VoiceData.seven, Inward, DontCare, BasicGesture.seven, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("八", "eight", VoiceData.eight, Inward, DontCare, BasicGesture.eight, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("九", "nine", VoiceData.nine, Inward, DontCare, BasicGesture.nine, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, Raise, DontCare, BasicGesture.ten_N, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, Raise, DontCare, BasicGesture.ten_S, BasicGesture.DontCare));
        //一到十(右手
        handRecognitions.add(new HandRecognition("零", "zero", VoiceData.zero, DontCare, Raise, BasicGesture.DontCare, BasicGesture.zero));
        handRecognitions.add(new HandRecognition("一", "one", VoiceData.one, DontCare, Raise, BasicGesture.DontCare, BasicGesture.one));
        handRecognitions.add(new HandRecognition("二", "two", VoiceData.two, DontCare, Raise, BasicGesture.DontCare, BasicGesture.two));
        handRecognitions.add(new HandRecognition("三", "three", VoiceData.three, DontCare, Raise, BasicGesture.DontCare, BasicGesture.three));
        handRecognitions.add(new HandRecognition("四", "four", VoiceData.four, DontCare, Raise, BasicGesture.DontCare, BasicGesture.four));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_A));
        handRecognitions.add(new HandRecognition("五", "five", VoiceData.five, DontCare, Raise, BasicGesture.DontCare, BasicGesture.five_B));
        handRecognitions.add(new HandRecognition("六", "six", VoiceData.six, DontCare, Inward, BasicGesture.DontCare, BasicGesture.six));
        handRecognitions.add(new HandRecognition("七", "seven", VoiceData.seven, DontCare, Inward, BasicGesture.DontCare, BasicGesture.seven));
        handRecognitions.add(new HandRecognition("八", "eight", VoiceData.eight, DontCare, Inward, BasicGesture.DontCare, BasicGesture.eight));
        handRecognitions.add(new HandRecognition("九", "nine", VoiceData.nine, DontCare, Inward, BasicGesture.DontCare, BasicGesture.nine));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_N));
        handRecognitions.add(new HandRecognition("十", "ten", VoiceData.ten, DontCare, Raise, BasicGesture.DontCare, BasicGesture.ten_S));
        //一到十
    }

    //動態手勢
    public ArrayList<MotionRecognition> motionRecognitions = new ArrayList<MotionRecognition>();
    public void MotionVocabulary(){
        //參考
        //motionRecognitions.add(new MotionRecognition("中文", "英文", mp3id, "左手面相", "右手面向", "左手手勢", "右手手勢", LAx~z, RAx~z, LAF0~LAF4, RAF0~RAF4));
    }

    //組合手勢
    public ArrayList<CombinationWordRecognition> combinationWordRecognitions = new ArrayList<CombinationWordRecognition>();
    public void CombinationVocabulary(){
        //參考
        //combinationWordRecognitions.add(new CombinationWordRecognition("中文", "英文", mp3D, "第一階段", "第二階段"));
        combinationWordRecognitions.add(new CombinationWordRecognition("你好", "Hello", VoiceData.hello, "你", "_你好"));
    }

}
