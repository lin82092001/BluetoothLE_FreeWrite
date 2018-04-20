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

    public ArrayList<HandRecognition> handRecognitions = new ArrayList<HandRecognition>();

    public void StaticVocabulary(){
        handRecognitions.add(new HandRecognition("零", "zero", R.raw.zero, "Raise", "DontCare", BasicGesture.one, BasicGesture.DontCare));
    }
}
