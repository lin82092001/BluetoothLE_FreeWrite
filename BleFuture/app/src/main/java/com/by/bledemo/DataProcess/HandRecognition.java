package com.by.bledemo.DataProcess;

import java.util.regex.Pattern;

/**
 * Created by zxcv1 on 2018/4/18.
 */

public class HandRecognition {
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

    //HandRecognition建構子
    public HandRecognition(String ChineseWord, String EnglishWord, int mp3ID, String LeftHandPosture, String RightHandPosture, String LeftFigPosture,  String RightFigPosture)
    {//進入建構子
        this.ChineseWord = ChineseWord;
        this.EnglishWord = EnglishWord;

        this.mp3ID = mp3ID;

        this.LeftHandPosture = Pattern.compile(LeftHandPosture);
        this.RightHandPosture = Pattern.compile(RightHandPosture);

        this.LeftFigPosture = Pattern.compile(LeftFigPosture);
        this.RightFigPosture = Pattern.compile(RightFigPosture);
    }//結束建構子

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

    //multiMatcher
    public boolean MultiMatcher(String LeftHandPosture, String RightHandPosture, String LeftFigPosture, String RightFigPosture)
    {
        boolean IsLeftHandPostureMatched, IsRightHandPostureMatched;
        IsLeftHandPostureMatched = LeftHandPostureMatcher(LeftHandPosture);
        IsRightHandPostureMatched = RightHandPostureMatcher(RightHandPosture);

        boolean IsLeftFigPostureMatched, IsRightFigPostureMatched;
        IsLeftFigPostureMatched = LeftFigPostureMatcher(LeftFigPosture);
        IsRightFigPostureMatched = RightFigPostureMatcher(RightFigPosture);

        if (LeftHandPosture == "DontCare")
        {
            return IsRightHandPostureMatched && IsRightFigPostureMatched;
        }else if(RightHandPosture == "DontCare")
        {
            return IsLeftHandPostureMatched && IsLeftFigPostureMatched;
        }else
        {
            return IsLeftHandPostureMatched && IsRightHandPostureMatched && IsLeftFigPostureMatched && IsRightFigPostureMatched;
        }
    }

}
