package com.example.tontonsumogame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * メインアクティビティ（メニュー画面）
 */
public class MainActivity extends AppCompatActivity {

    // フィールド変数
    private Button btnStart, btnHowTo; // ゲームスタートボタン、遊び方へボタン

    private static final int REQUESTCODE_GAME = 1; // ゲーム画面のリクエストコード

    private BackGroundMusic bgm = new BackGroundMusic(); // BGMクラス
    private ImageView btnVolume; // 音量ボタン
    private boolean volumeStatus = true; // 音量の状態を格納
    Map<String, Drawable> btnVolumeImages; // ボリュームボタンのリソースファイルを格納しておくコレクション

    private ImageView imgCloud01, imgCloud02; // 雲の画像
    // 横移動アニメーション
    private TranslateAnimation cloudAnima01 = new TranslateAnimation(
            android.view.animation.Animation.RELATIVE_TO_PARENT, 0.0f,
            android.view.animation.Animation.RELATIVE_TO_PARENT, 2.0f,
            android.view.animation.Animation.RELATIVE_TO_PARENT, 0.0f,
            android.view.animation.Animation.RELATIVE_TO_PARENT, 0.0f);

    // 上下移動アニメーション
    private TranslateAnimation cloudAnima02 = new TranslateAnimation(
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.0f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.1f);

    // アニメーションセット
    private AnimationSet animationSet01 = new AnimationSet(true);


    /**
     * -----------------------------------------------------------------
     * onCreateメソッド
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BGM準備
        bgm.onCreate(this);

        // 前画面画像のリソースファイルを格納しておくコレクション
        btnVolumeImages = new HashMap<String, Drawable>();
        btnVolumeImages.put("on", getResources().getDrawable(R.drawable.btn_on));
        btnVolumeImages.put("off", getResources().getDrawable(R.drawable.btn_off));

        // ボリュームのオンオフ
        btnVolume = findViewById(R.id.btnVolume);
        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnVolumeClick();
            }
        });

        // ゲームスタート
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnStartClick();
            }
        });

        // 遊び方へ
        btnHowTo = findViewById(R.id.btnHowTo);
        btnHowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnHowToClick();
            }
        });

        imgCloud01 = findViewById(R.id.imgCloud01);
        imgCloud02 = findViewById(R.id.imgCloud02);

        cloudAnima01.setRepeatCount(Animation.INFINITE);
        cloudAnima01.setDuration(5000);
        cloudAnima02.setRepeatCount(Animation.INFINITE);
        cloudAnima02.setDuration(500);
        cloudAnima02.setRepeatMode(Animation.REVERSE);

        animationSet01.addAnimation(cloudAnima01);
        animationSet01.addAnimation(cloudAnima02);
        animationSet01.setFillAfter(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_GAME:
                if (RESULT_OK == resultCode) {
                    Intent intent = getIntent();
                    volumeStatus = intent.getBooleanExtra("volumeStatus", volumeStatus);
                }
                break;
        }
    }

    /**
     * 画面表示時メソッド
     */
    public void onResume() {
        super.onResume();

        imgCloud01.startAnimation(animationSet01);
        imgCloud02.startAnimation(animationSet01);

        // 画面表示時のBGM状態
        if (volumeStatus) {
            btnVolume.setBackground(btnVolumeImages.get("on"));
            bgm.onStart();
        } else {
            btnVolume.setBackground(btnVolumeImages.get("off"));
            bgm.onPause();
        }
    }

    /**
     * 画面停止時メソッド
     */
    public void onPause() {
        super.onPause();
        bgm.onPause();
    }

    /**
     * 画面が破棄される前の情報保存
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // ボリュームの状態格納
        outState.putBoolean("volumeStatus", volumeStatus);
    }


    /**
     * 画面復帰時の情報呼び出し
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // ボリュームの状態取得
        volumeStatus = savedInstanceState.getBoolean("volumeStatus");
    }


    /**
     * ボリュームボタンが押された時のメソッド
     */
    public void onBtnVolumeClick() {
        if (volumeStatus) {
            btnVolume.setBackground(btnVolumeImages.get("off"));
            volumeStatus = false;
            bgm.onPause();
        } else {
            btnVolume.setBackground(btnVolumeImages.get("on"));
            volumeStatus = true;
            bgm.onStart();
        }
    }


    /**
     * ゲームスタートボタンが押された時のメソッド
     */
    public void onBtnStartClick() {
        Intent intent = new Intent(getApplication(), SubActivity.class);
        intent.putExtra("volumeStatus", volumeStatus);
        startActivityForResult(intent, REQUESTCODE_GAME);
    }

    /**
     * 遊び方ボタンが押された時のメソッド
     */
    public void onBtnHowToClick() {
        Intent intent = new Intent(getApplication(), HowToActivity.class);
        startActivity(intent);
    }
}