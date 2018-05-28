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

    private int LanquageSelector = 0;
    private int[] zero = {R.raw.zero, R.raw.zeroen};
    private int[] one = {R.raw.one, R.raw.oneen};
    private int[] two = {R.raw.two, R.raw.twoen};
    private int[] three = {R.raw.three, R.raw.threeen};
    private int[] four = {R.raw.four, R.raw.fouren};
    private int[] five = {R.raw.five, R.raw.fiveen};
    private int[] six = {R.raw.six, R.raw.sixen};
    private int[] seven = {R.raw.seven, R.raw.sevenen};
    private int[] eight = {R.raw.eight, R.raw.eighten};
    private int[] nine = {R.raw.nine, R.raw.nineen};
    private int[] ten = {R.raw.ten, R.raw.tenen};
    private int[] twenty = {R.raw.twenty, R.raw.twentyen};
    private int[] thirty = {R.raw.thirty, R.raw.thirtyen};
    private int[] forty = {R.raw.forty, R.raw.fortyen};
    private int[] fifty = {R.raw.fifty, R.raw.fiftyen};
    private int[] sixty = {R.raw.sixty, R.raw.sixtyen};
    private int[] seventy = {R.raw.seventy, R.raw.seventyen};
    private int[] eighty = {R.raw.eighty, R.raw.eightyen};
    private int[] ninety = {R.raw.ninety, R.raw.ninetyen};
    private int[] hundred = {R.raw.hundred, R.raw.hundreden};
    private int[] thousand = {R.raw.thousand, R.raw.thousanden};

    private int[] you = {R.raw.you, R.raw.youen};
    private int[] hello = {R.raw.hello, R.raw.helloen};


    public RecognitionWorker(String State){

        if(State.equals("Chinese")){
            LanquageSelector = 0;
        }else if(State.equals("English")){
            LanquageSelector = 1;
        }
        VoiceData.zero = zero[LanquageSelector];
        VoiceData.one = one[LanquageSelector];
        VoiceData.two = two[LanquageSelector];
        VoiceData.three = three[LanquageSelector];
        VoiceData.four = four[LanquageSelector];
        VoiceData.five = five[LanquageSelector];
        VoiceData.six = six[LanquageSelector];
        VoiceData.seven = seven[LanquageSelector];
        VoiceData.eight = eight[LanquageSelector];
        VoiceData.nine = nine[LanquageSelector];
        VoiceData.ten = ten[LanquageSelector];
        VoiceData.twenty = twenty[LanquageSelector];
        VoiceData.thirty = thirty[LanquageSelector];
        VoiceData.forty = forty[LanquageSelector];
        VoiceData.fifty = fifty[LanquageSelector];
        VoiceData.sixty = sixty[LanquageSelector];
        VoiceData.seventy = seventy[LanquageSelector];
        VoiceData.eighty = eighty[LanquageSelector];
        VoiceData.ninety = ninety[LanquageSelector];
        VoiceData.hundred = hundred[LanquageSelector];
        VoiceData.thousand = thousand[LanquageSelector];

        VoiceData.you = you[LanquageSelector];
        VoiceData.hello = hello[LanquageSelector];
    }
    //靜態手勢
    public ArrayList<HandRecognition> handRecognitions = new ArrayList<HandRecognition>();
    public void StaticVocabulary(){
        //參考
        //handRecognitions.add(new HandRecognition("中文", "英文", mp3id, 左手面相, 右手面向, "左手手勢", "右手手勢"));
        //test
        handRecognitions.add(new HandRecognition("你", "you", VoiceData.you, Inward, DontCare, BasicGesture.you, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("_你好", "hello", R.raw.nulll, Raise, DontCare, BasicGesture.hello, BasicGesture.DontCare));
        //test
        //左手
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
        handRecognitions.add(new HandRecognition("二十", "twenty", VoiceData.twenty, Raise, DontCare, BasicGesture.twenty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("三十", "thirty", VoiceData.thirty, Raise, DontCare, BasicGesture.thirty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("四十", "forty", VoiceData.forty, Raise, DontCare, BasicGesture.forty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("五十", "fifty", VoiceData.fifty, Raise, DontCare, BasicGesture.fifty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("六十", "sixty", VoiceData.sixty, Raise, DontCare, BasicGesture.sixty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("七十", "seventy", VoiceData.seventy, Raise, DontCare, BasicGesture.seventy, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("八十", "eighty", VoiceData.eighty, Raise, DontCare, BasicGesture.eighty, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("九十", "ninety", VoiceData.ninety, Raise, DontCare, BasicGesture.ninety, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("百", "hundred", VoiceData.hundred, Raise, DontCare, BasicGesture.hundred, BasicGesture.DontCare));
        handRecognitions.add(new HandRecognition("千", "thousand", VoiceData.thousand, Raise, DontCare, BasicGesture.thousand, BasicGesture.DontCare));

        //右手
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
