package com.by.bledemo.DataProcess;

import java.util.regex.Pattern;

/**
 * Created by zxcv1 on 2018/5/11.
 */

public class CombinationWordRecognition {
    public String ChineseWord, EnglishWord;

    private Pattern Word1, Word2;

    public int mp3ID;

    public CombinationWordRecognition(String ChineseWord, String EnglishWord, int mp3ID, String Word1, String Word2)
    {
        this.ChineseWord = ChineseWord;
        this.EnglishWord = EnglishWord;
        this.mp3ID = mp3ID;
        this.Word1 = Pattern.compile(Word1);
        this.Word2 = Pattern.compile(Word2);
    }

    public boolean MultiMatcher(String InputWord1, String InputWord2)
    {
        boolean IsInputWord1Matched, IsInputWord2Matched;
        IsInputWord1Matched = this.Word1.matcher(InputWord1).matches();
        IsInputWord2Matched = this.Word2.matcher(InputWord2).matches();

        return IsInputWord1Matched && IsInputWord2Matched;
    }
}
