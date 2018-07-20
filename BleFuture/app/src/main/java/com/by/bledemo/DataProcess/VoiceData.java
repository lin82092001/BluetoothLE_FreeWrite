package com.by.bledemo.DataProcess;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by zxcv1 on 2018/4/26.
 */

public class VoiceData {
    //test
    //test

    //mp3ID
    public int U_like;
    public int L_like;
    public int J_like;
    public int arm_waving;
    public int wrist_waving;
    public int wrist_rotation;
    public int Null;
    public int you;
    public int toilet;
    public int zero;
    public int one;
    public int two;
    public int three;
    public int four;
    public int five;
    public int six;
    public int seven;
    public int eight;
    public int nine;
    public int ten;
    public int twenty;
    public int thirty;
    public int forty;
    public int fifty;
    public int sixty;
    public int seventy;
    public int eighty;
    public int ninety;
    public int hundred;
    public int thousand;
    public int male;
    public int female;
    public int brother;
    public int sister;
    public int money;
    public int thanks;
    public int taipei;
    public int technology;
    public int university;
    public int hello;
    public int love;
    public int protect;
    public int coffee;
    public int admit;
    public int help;
    public int lonely;
    public int I;
    public int letter;
    public int recletter;
    public int stamp;
    public int sandwich;
    public int welcome;
    public int graduation;
    public int teacher;

    public VoiceData(){

    }

    public void Speaker(final Context Text, final int ID){
        new Runnable(){
            @Override
            public void run(){
                try{
                    final MediaPlayer player = MediaPlayer.create(Text, ID);
                    if(player != null){
                        player.stop();
                    }
                    if(!player.isPlaying()){
                        player.prepare();
                        player.start();
                    }
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            player.release();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }
}

