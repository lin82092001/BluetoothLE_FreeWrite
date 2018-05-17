package com.by.bledemo.DataProcess;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by zxcv1 on 2018/4/26.
 */

public class VoiceData {
    //test
    public int you;
    public int toilet;
    //test

    //mp3ID
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

    public int hello;
    public int good;

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

