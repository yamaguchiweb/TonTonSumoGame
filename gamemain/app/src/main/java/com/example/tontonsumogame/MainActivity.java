package com.example.tontonsumogame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // フィールド変数
    private Button btnStart; // ゲーム画面へ移動ボタン
    private Button btnHowTo; // 遊び方画面へ移動ボタン
    private MediaPlayer mediaPlayer; // BGMをいれる

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnStartClick();
            }
        });

        btnHowTo = findViewById(R.id.btnHowTo);
        btnHowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnHowToClick();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.audio_01);
        mediaPlayer.start();

    }

    // 画面が破棄される前の情報保存
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mediaPlayer.pause();
    }

    // 再描画時の処理
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mediaPlayer.start();
    }

    // ゲームスタートボタンが押された時
    public void onBtnStartClick() {
        mediaPlayer.pause();
        Intent intent = new Intent(getApplication(), SubActivity.class);
        startActivity(intent);
    }

    // 遊び方画面へ移動ボタンが押された時
    public void onBtnHowToClick() {
        mediaPlayer.pause();
        Intent intent = new Intent(getApplication(), HowToActivity.class);
        startActivity(intent);
    }
}