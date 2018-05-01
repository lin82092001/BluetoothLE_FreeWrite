package com.by.bledemo.DataProcess;

import android.view.WindowManager;

import com.by.bledemo.R;

import java.util.regex.Pattern;

/**
 * Created by zxcv1 on 2018/4/27.
 */

public class MotionRecognition {
    //文字
    public String ChineseWord;
    public String EnglishWord;

    //語音
    public int mp3ID;

    //面向
    private Pattern LeftHandPosture;
    private Pattern RightHandPosture;

    //手指
    private Pattern LeftFigPosture;
    private Pattern RightFigPosture;

    //手加速度
    private int LAx, LAy, LAz;
    private int RAx, RAy, RAz;

    public MotionRecognition(String ChineseWord, String EnglishWord, int mp3ID,
                             String LeftHandPosture, String RightHandPosture, String LeftFigPosture,  String RightFigPosture,
                             int LAx, int LAy, int LAz,
                             int RAx, int RAy, int RAz){
        this.ChineseWord = ChineseWord;
        this.EnglishWord = EnglishWord;
        this.mp3ID = mp3ID;
        this.LeftHandPosture = Pattern.compile(LeftHandPosture);
        this.RightHandPosture = Pattern.compile(RightHandPosture);
        this.LeftFigPosture = Pattern.compile(LeftFigPosture);
        this.RightFigPosture = Pattern.compile(RightFigPosture);
        this.LAx = LAx;
        this.LAy = LAy;
        this.LAz = LAz;
        this.RAx = RAx;
        this.RAy = RAy;
        this.RAz = RAz;
    }
    public boolean TotalMatcher(String LeftHandPosture, String RightHandPosture, String LeftFigPosture, String RightFigPosture,
                                int LAx, int LAy, int LAz,
                                int RAx, int RAy, int RAz){

        boolean IsLeftHandPostureMatched, IsRightHandPostureMatched;
        IsLeftHandPostureMatched = LeftHandPostureMatcher(LeftHandPosture);
        IsRightHandPostureMatched = RightHandPostureMatcher(RightHandPosture);

        boolean IsLeftFigPostureMatched, IsRightFigPostureMatched;
        IsLeftFigPostureMatched = LeftFigPostureMatcher(LeftFigPosture);
        IsRightFigPostureMatched = RightFigPostureMatcher(RightFigPosture);

        boolean IsLAxMatched, IsLAyMatched, IsLAzMatched, IsRAxMatched, IsRAyMatched, IsRAzMatched;
        IsLAxMatched = AccMatcher(LAx, this.LAx);
        IsLAyMatched = AccMatcher(LAy, this.LAy);
        IsLAzMatched = AccMatcher(LAz, this.LAz);
        IsRAxMatched = AccMatcher(RAx, this.RAx);
        IsRAyMatched = AccMatcher(RAy, this.RAy);
        IsRAzMatched = AccMatcher(RAz, this.RAz);

        if (LeftHandPosture == "DontCare")
        {
            return IsRightHandPostureMatched && IsRightFigPostureMatched &&
                    IsRAxMatched && IsRAyMatched && IsRAzMatched;
        }else if(RightHandPosture == "DontCare")
        {
            return IsLeftHandPostureMatched && IsLeftFigPostureMatched &&
                    IsLAxMatched && IsLAyMatched && IsLAzMatched;
        }else
        {
            return IsLeftHandPostureMatched && IsRightHandPostureMatched && IsLeftFigPostureMatched && IsRightFigPostureMatched &&
                    IsRAxMatched && IsRAyMatched && IsRAzMatched && IsLAxMatched && IsLAyMatched && IsLAzMatched;
        }
    }

    //面相 Matcher
    private boolean LeftHandPostureMatcher(String LeftHandPosture)
    {
        boolean IsLeftHandPostureMatched;
        IsLeftHandPostureMatched = this.LeftHandPosture.matcher(LeftHandPosture).matches();
        //IsLeftHandPostureMatched為matches方法所傳回之布林變數
        return IsLeftHandPostureMatched;
    }
    private boolean RightHandPostureMatcher(String RightHandPosture)
    {
        boolean IsRightHandPostureMatched;
        IsRightHandPostureMatched = this.RightHandPosture.matcher(RightHandPosture).matches();
        //IsRightHandPostureMatched為matches方法所傳回之布林變數
        return IsRightHandPostureMatched;
    }

    //手指 Matcher
    private boolean LeftFigPostureMatcher(String LeftFigPosture)
    {
        boolean IsLeftFigPostureMatched;
        IsLeftFigPostureMatched = this.LeftFigPosture.matcher(LeftFigPosture).matches();
        //IsLeftFigPostureMatched為matches方法所傳回之布林變數
        return IsLeftFigPostureMatched;
    }
    private boolean RightFigPostureMatcher(String RightFigPosture)
    {
        boolean IsRightFigPostureMatched;
        IsRightFigPostureMatched = this.RightFigPosture.matcher(RightFigPosture).matches();
        //IsRightFigPostureMatched為matches方法所傳回之布林變數
        return IsRightFigPostureMatched;
    }

    //motion
    private boolean AccMatcher(int AccInput, int AccThresholdValue){
        if(AccInput > AccThresholdValue){
            return true;
        }else{
            return false;
        }

    }
}
