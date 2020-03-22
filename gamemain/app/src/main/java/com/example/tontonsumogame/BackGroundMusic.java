package com.example.tontonsumogame;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * BGM操作クラス
 */
public class BackGroundMusic {

    private MediaPlayer bgmMediaPlayer;

    public void onCreate(Context context){
        bgmMediaPlayer = MediaPlayer.create(context, R.raw.audio_01);
        bgmMediaPlayer.setLooping(true);
        bgmMediaPlayer.setVolume(1f,1f);
    }

    public void onStart(){
        if (bgmMediaPlayer == null) {
            return;
        }
        if (!bgmMediaPlayer.isPlaying()) {
            bgmMediaPlayer.start();
        }
        Log.i("BGMMemo", "BGMスタート");
    }

    public void onPause(){
        if (bgmMediaPlayer == null) {
            return;
        }
        if (bgmMediaPlayer.isPlaying()) {
            bgmMediaPlayer.pause();
            bgmMediaPlayer.seekTo(0);
        }
        Log.i("BGMMemo", "BGMポーズ");
    }

    public void onStop(){
        if (bgmMediaPlayer == null) {
            return;
        } else {
            bgmMediaPlayer.stop();
            bgmMediaPlayer.reset();
            bgmMediaPlayer.release();
            bgmMediaPlayer = null;
        Log.i("BGMMemo", "BGMストップ");
        }
    }
}
