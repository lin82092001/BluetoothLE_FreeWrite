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

    //掌加速度
    private int LAx, LAy, LAz;
    private int RAx, RAy, RAz;

    //指加速度
    private int LAF0;
    private int LAF1;
    private int LAF2;
    private int LAF3;
    private int LAF4;
    private int RAF0;
    private int RAF1;
    private int RAF2;
    private int RAF3;
    private int RAF4;

    public MotionRecognition(String ChineseWord, String EnglishWord, int mp3ID,
                             String LeftHandPosture, String RightHandPosture, String LeftFigPosture,  String RightFigPosture,
                             int LAx, int LAy, int LAz,
                             int RAx, int RAy, int RAz,
                             int LAF0, int LAF1, int LAF2, int LAF3, int LAF4,
                             int RAF0, int RAF1, int RAF2, int RAF3, int RAF4){
        this.ChineseWord = ChineseWord;
        this.EnglishWord = EnglishWord;
        this.mp3ID = mp3ID;
        this.LeftHandPosture = Pattern.compile(LeftHandPosture);
        this.RightHandPosture = Pattern.compile(RightHandPosture);
        this.LeftFigPosture = Pattern.compile(LeftFigPosture);
        this.RightFigPosture = Pattern.compile(RightFigPosture);
        //手掌
        this.LAx = LAx;
        this.LAy = LAy;
        this.LAz = LAz;
        this.RAx = RAx;
        this.RAy = RAy;
        this.RAz = RAz;
        //手指
        this.LAF0 = LAF0;
        this.LAF1 = LAF1;
        this.LAF2 = LAF2;
        this.LAF3 = LAF3;
        this.LAF4 = LAF4;
        //手指
        this.RAF0 = RAF0;
        this.RAF1 = RAF1;
        this.RAF2 = RAF2;
        this.RAF3 = RAF3;
        this.RAF4 = RAF4;
    }
    public boolean TotalMatcher(String LeftHandPosture, String RightHandPosture, String LeftFigPosture, String RightFigPosture,
                                int LAx, int LAy, int LAz,
                                int RAx, int RAy, int RAz,
                                int LAF0, int LAF1, int LAF2, int LAF3, int LAF4,
                                int RAF0, int RAF1, int RAF2, int RAF3, int RAF4){

        boolean IsLeftHandPostureMatched, IsRightHandPostureMatched;
                IsLeftHandPostureMatched = LeftHandPostureMatcher(LeftHandPosture);
                IsRightHandPostureMatched = RightHandPostureMatcher(RightHandPosture);

        boolean IsLeftFigPostureMatched, IsRightFigPostureMatched;
                IsLeftFigPostureMatched = LeftFigPostureMatcher(LeftFigPosture);
                IsRightFigPostureMatched = RightFigPostureMatcher(RightFigPosture);

        boolean IsLAxMatched, IsLAyMatched, IsLAzMatched, IsRAxMatched, IsRAyMatched, IsRAzMatched,
                IsLAF0Matched, IsLAF1Matched, IsLAF2Matched, IsLAF3Matched, IsLAF4Matched,
                IsRAF0Matched, IsRAF1Matched, IsRAF2Matched, IsRAF3Matched, IsRAF4Matched;

        //首掌
        IsLAxMatched = AccMatcher(LAx, this.LAx);//外部輸入與詞彙庫定的門檻值比較,外部輸入>詞彙庫門檻值
        IsLAyMatched = AccMatcher(LAy, this.LAy);
        IsLAzMatched = AccMatcher(LAz, this.LAz);
        //手指
        IsLAF0Matched = AccMatcher(LAF0, this.LAF0);
        IsLAF1Matched = AccMatcher(LAF1, this.LAF1);
        IsLAF2Matched = AccMatcher(LAF2, this.LAF2);
        IsLAF3Matched = AccMatcher(LAF3, this.LAF3);
        IsLAF4Matched = AccMatcher(LAF4, this.LAF4);
        //手掌
        IsRAxMatched = AccMatcher(RAx, this.RAx);
        IsRAyMatched = AccMatcher(RAy, this.RAy);
        IsRAzMatched = AccMatcher(RAz, this.RAz);
        //手指
        IsRAF0Matched = AccMatcher(RAF0, this.RAF0);
        IsRAF1Matched = AccMatcher(RAF1, this.RAF1);
        IsRAF2Matched = AccMatcher(RAF2, this.RAF2);
        IsRAF3Matched = AccMatcher(RAF3, this.RAF3);
        IsRAF4Matched = AccMatcher(RAF4, this.RAF4);

        if(RightHandPosture == "DontCare")
        {
            return IsLeftHandPostureMatched && IsLeftFigPostureMatched &&
                    IsLAxMatched && IsLAyMatched && IsLAzMatched &&
                    IsLAF0Matched && IsLAF1Matched && IsLAF2Matched && IsLAF3Matched && IsLAF4Matched;
        }else if (LeftHandPosture == "DontCare")
        {
            return IsRightHandPostureMatched && IsRightFigPostureMatched &&
                    IsRAxMatched && IsRAyMatched && IsRAzMatched &&
                    IsRAF0Matched && IsRAF1Matched && IsRAF2Matched && IsRAF3Matched && IsRAF4Matched;
        }else
        {
            return IsLeftHandPostureMatched && IsRightHandPostureMatched && IsLeftFigPostureMatched && IsRightFigPostureMatched &&
                    IsLAxMatched && IsLAyMatched && IsLAzMatched && IsRAxMatched && IsRAyMatched && IsRAzMatched &&
                    IsLAF0Matched && IsLAF1Matched && IsLAF2Matched && IsLAF3Matched && IsLAF4Matched &&
                    IsRAF0Matched && IsRAF1Matched && IsRAF2Matched && IsRAF3Matched && IsRAF4Matched;
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
