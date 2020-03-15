package com.example.tontonsumogame;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Date;

public class SubActivity extends AppCompatActivity {

    // フィールド変数
    private MediaPlayer mediaPlayer; // mediaplayerをいれる
    private Button btnReStart; // ゲームスタートボタン
    private Button btnReturn; // トップへ戻るボタン
    private ImageView imgTapArea; // タップできる画像
    private float imgTapAreaX; // タップできる画像のX座標
    private ImageView imgHokori01; // ほこり画像
    private ImageView imgHokori02; // ほこり画像
    private ImageView imgNokotta01; // のこった吹き出し
    private ImageView imgNokotta02; // のこった吹き出し
    private ImageView imgCountDown; // カウントダウン画像
    private ImageView imgOverView01; // 結果画像
    private ImageView imgOverView02; // 結果画像
    private ImageView imgOverView03; // 技画像
    private ImageView imgOverView04; // 技画像

    private TimeProcessing timeProcessing = new TimeProcessing(); // 時間取得クラス
    private Judgment judgment = new Judgment(); // 勝敗判定クラス

    private final Handler timerHandler = new Handler(); // タイマー処理をするクラス
    private Runnable gameEndRunnable; // ゲーム終了のRunnable
    private Runnable resultRunnable; // ゲーム結果のRunnable
    private Runnable buttonRunnable; // ボタン表示のRunnable

    private Runnable countDownRunnable; // カウントダウンのRunnable

    private Runnable bonusRunnable; // ボーナス画像のRunnable

    private Runnable fighterMoveRunnable; // 力士画像のRunnable
    private int fighterCount = 1;  // 力士画像の動いた回数
    private boolean fighterBool = true; // 力士画像の動く方向

    private Runnable hokoriRunnable; // ほこり画像のRunnable
    private int hokoriCount = 1;  // ほこり画像の動いた回数

    private final int TIME = 15000; // ゲームのリミット時間
    private int time = TIME; // 時間の代入
    private Date startTime; // 開始時間
    private Date endTime; // 終了時間
    private long timeDiff; // 経過時間

    private boolean nowOnTime; // 時間内判定
    private boolean reDraw; // 再描画状態判定

    private int tapNum; //　タップ回数
    private int totalResult; //　トータル点数
    private int pointNum = 10; //　ポイント点数
    private int bonusLine = 10; // ボーナス付与回数
    private int bonusNum = 30; // ボーナス点数
    private int passLine = 500; // クリアライン点数
    private boolean firstPass = true; // 最初にクリアラインを超えたか

    AlphaAnimation fadeOutAnim = new AlphaAnimation(1, 0); // フェードアウトアニメーション
    AlphaAnimation fadeInAnim = new AlphaAnimation(0, 1); // フェードインアニメーション

    TranslateAnimation translateBonusAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f); // 移動アニメーション

    AnimationSet setAnimRoose = new AnimationSet(true); // アニメーションセット
    AlphaAnimation setFadeOutAnim = new AlphaAnimation(1, 0); // フェードアウトアニメーション
    ScaleAnimation setScaleAnim = new ScaleAnimation(1, 0.2f, 1, 0.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f); // 拡大縮小アニメーション

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        mediaPlayer = MediaPlayer.create(this, R.raw.audio_01);

        // カウントダウンアニメーション
        imgCountDown = findViewById(R.id.imgCountDown);
        countDownRunnable = new Runnable() {
            @Override
            public void run() {
                countDown(1000, 2000, 3000, 4000, 4750);
            }
        };
        timerHandler.post(countDownRunnable);

        btnReStart = findViewById(R.id.btnReStart);
        btnReStart.setEnabled(false);
        btnReStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnStartClick();
            }
        });

        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setEnabled(false);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnReturnClick();
            }
        });

        imgHokori01 = findViewById(R.id.imgHokori01);
        imgHokori02 = findViewById(R.id.imgHokori02);
        imgNokotta01 = findViewById(R.id.imgNokotta01);
        imgNokotta02 = findViewById(R.id.imgNokotta02);
        imgOverView01 = findViewById(R.id.imgOverView01);
        imgOverView02 = findViewById(R.id.imgOverView02);
        imgOverView03 = findViewById(R.id.imgOverView03);
        imgOverView04 = findViewById(R.id.imgOverView04);

        imgTapArea = findViewById(R.id.imgTapArea);
        imgTapArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImgTapAreaClick();
            }
        });
    }

    // 画面が破棄される前の情報保存
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Runnableを停止
        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        mediaPlayer.pause();
        Log.i("TapDetection", "mediaPlayerストップ");

        // 時間内 true であれば、経過時間を計測
        if (nowOnTime) {
            timeDiff = timeProcessing.remainingTime(startTime);
        }
        if(timeDiff > 0){
            outState.putLong("timeDiff", timeDiff); // 経過時間格納
        }
        if(startTime != null){
            outState.putLong("startTime", startTime.getTime()); // 最初のスタート時間格納
        }
        outState.putInt("tapNum", tapNum); //　現在のタップ回数格納
        outState.putInt("totalResult", totalResult); // 現在の合計点数格納
    }

    // 再描画時の処理
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Runnableを停止
        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);

        Log.i("TapDetection", "再描画されました。");

        // 最初のスタート時間を取得
        Date startTimeDate = new Date(savedInstanceState.getLong("startTime"));
        startTime = startTimeDate;

        timeDiff = savedInstanceState.getLong("timeDiff"); // 経過時間取得
        tapNum = savedInstanceState.getInt("tapNum"); // タップ回数取得
        totalResult = savedInstanceState.getInt("totalResult"); // 合計点数取得

        if (timeDiff >= TIME) {
            gameEnd();
        } else if (timeDiff == 0) {
            imgCountDown.setVisibility(View.INVISIBLE);
            reDraw = false;
            nowOnTime = false;
            imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_04));
            imgNokotta01.setVisibility(View.INVISIBLE);
            imgNokotta02.setVisibility(View.INVISIBLE);
            imgHokori01.setVisibility(View.INVISIBLE);
            imgHokori02.setVisibility(View.INVISIBLE);
            tapNum = 0;
            totalResult = 0;
            timeDiff = 0;
            time = TIME;
            btnReStart.setVisibility(View.VISIBLE);
            btnReStart.setEnabled(true);
            btnReturn.setVisibility(View.VISIBLE);
            btnReturn.setEnabled(true);
        } else { // 経過時間がTIMEより小さければ一時停止
            imgOverView01.setImageDrawable(getResources().getDrawable(R.drawable.pause_01));
            imgOverView01.setVisibility(View.VISIBLE);
            imgCountDown.setVisibility(View.INVISIBLE);
            nowOnTime = false; // 時間内 false
            reDraw = true; // 再描画 true
            btnReStart.setVisibility(View.VISIBLE);
            btnReStart.setEnabled(true);
            btnReturn.setVisibility(View.VISIBLE);
            btnReturn.setEnabled(true);
        }
    }

    // ボタンを押された時
    public void onBtnStartClick() {
        imgOverView01.setVisibility(View.INVISIBLE);
        imgOverView02.setVisibility(View.INVISIBLE);
        btnReStart.setVisibility(View.INVISIBLE);
        btnReStart.setEnabled(false);
        btnReturn.setVisibility(View.INVISIBLE);
        btnReturn.setEnabled(false);

        if (!reDraw) { // 再描画 false
            tapNum = 0; // タップ回数リセット
            totalResult = 0; // 合計点数リセット
            timeDiff = 0; // 経過時間リセット
            timerHandler.post(countDownRunnable);
        } else { // 再描 true
            time = (int) (time - timeDiff); // 終了時間をdayDiff分追加
            gameStart();
        }
        Log.i("TapDetection", "timeDiffは" + timeDiff + "、timeは" + time);
    }

    // カウントダウンアニメーション
    public void countDown(int num1, int num2, int num3, int num4, int num5) {
        fadeOutAnim.setDuration(1000);

        Runnable countDownRunnable04 = new Runnable() {
            @Override
            public void run() {
                imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_04));
                imgCountDown.setVisibility(View.VISIBLE);
                imgCountDown.startAnimation(fadeOutAnim);
            }
        };
        Runnable countDownRunnable03 = new Runnable() {
            @Override
            public void run() {
                imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_03));
                imgCountDown.startAnimation(fadeOutAnim);
            }
        };
        Runnable countDownRunnable02 = new Runnable() {
            @Override
            public void run() {
                imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_02));
                imgCountDown.startAnimation(fadeOutAnim);
            }
        };
        Runnable countDownRunnable01 = new Runnable() {
            @Override
            public void run() {
                imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_01));
                imgCountDown.setVisibility(View.VISIBLE);
            }
        };
        Runnable gameStartRunnable = new Runnable() {
            @Override
            public void run() {
                gameStart();
                imgCountDown.setVisibility(View.INVISIBLE);
            }
        };
        timerHandler.postDelayed(countDownRunnable04, num1);
        timerHandler.postDelayed(countDownRunnable03, num2);
        timerHandler.postDelayed(countDownRunnable02, num3);
        timerHandler.postDelayed(countDownRunnable01, num4);
        timerHandler.postDelayed(gameStartRunnable, num5);
    }

    // ゲームスタート
    public void gameStart() {
        nowOnTime = true;
        btnReturn.setVisibility(View.INVISIBLE);
        btnReturn.setEnabled(false);
        mediaPlayer.start();
        Log.i("TapDetection", "mediaPlayerスタート");

        startTime = timeProcessing.nowTime(); // 開始時間 取得
        endTime = timeProcessing.endTime(timeProcessing.nowTime(), time); // 終了時間
        Log.i("TapDetection", "開始時刻は\t" + startTime + " です。");
        Log.i("TapDetection", "終了時刻は\t" + endTime + " を予定しています。");

        fadeOutAnim.setDuration(750);
        hokoriRunnable = new Runnable() {
            @Override
            public void run() {
                if (hokoriCount % 2 == 0) {
                    imgHokori02.startAnimation(fadeOutAnim);
                    imgHokori02.setVisibility(View.VISIBLE);
                } else {
                    imgHokori01.startAnimation(fadeOutAnim);
                    imgHokori01.setVisibility(View.VISIBLE);
                }
                hokoriCount += 1;
                timerHandler.postDelayed(this, 750);
            }
        };
        timerHandler.post(hokoriRunnable);
        fighterMoveRunnable = new Runnable() {
            @Override
            public void run() {
                imgTapAreaX = imgTapArea.getX(); //X座標

                if (fighterCount == 2 || fighterCount % 7 == 0) {
                    if (fighterBool) {
                        fighterBool = false;
                    } else {
                        fighterBool = true;
                    }
                }
                if (fighterBool) {
                    imgTapArea.setTranslationX(imgTapAreaX + 10);
                } else {
                    imgTapArea.setTranslationX(imgTapAreaX - 10);
                }
                fighterCount += 1;
                timerHandler.postDelayed(this, 200);
            }
        };
        timerHandler.post(fighterMoveRunnable);

        // time時間経過後の処理
        gameEndRunnable = new Runnable() {
            @Override
            public void run() {
                gameEnd();
            }
        };
        timerHandler.postDelayed(gameEndRunnable, time);
    }

    // ゲーム結果表示
    public void gameEnd() {
        reDraw = false;
        nowOnTime = false;
        timeDiff = 0;
        time = TIME;
        firstPass = true;
        imgNokotta01.setVisibility(View.INVISIBLE);
        imgNokotta02.setVisibility(View.INVISIBLE);
        imgHokori01.setVisibility(View.INVISIBLE);
        imgHokori02.setVisibility(View.INVISIBLE);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        imgOverView01.setImageDrawable(getResources().getDrawable(R.drawable.stop_01));
        imgOverView01.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
        Log.i("TapDetection", "mediaPlayerストップ");

        resultRunnable = new Runnable() {
            @Override
            public void run() {
                // 勝敗の判定
                if (judgment.JudgmentResult(totalResult, passLine)) {
                    imgOverView01.setImageDrawable(getResources().getDrawable(R.drawable.result_01));
                    Log.i("TapDetection", "終了時刻になりました。最終点数は" +
                            totalResult + "点でした。あなたの勝ちです");
                } else {
                    imgOverView01.setImageDrawable(getResources().getDrawable(R.drawable.result_02));
                    imgOverView02.setImageDrawable(getResources().getDrawable(R.drawable.roose_01));
                    imgOverView02.setVisibility(View.VISIBLE);
                    setAnimRoose.addAnimation(setFadeOutAnim);
                    setAnimRoose.addAnimation(setScaleAnim);
                    setAnimRoose.setDuration(4000);
                    setAnimRoose.setFillAfter(true);
                    imgOverView02.startAnimation(setAnimRoose);

                    Log.i("TapDetection", "終了時刻になりました。最終点数は" +
                            totalResult + "点でした。あなたの負けです");
                }
            }
        };
        buttonRunnable = new Runnable() {
            @Override
            public void run() {
                fadeInAnim.setDuration(1000);
                btnReStart.startAnimation(fadeInAnim);
                btnReturn.startAnimation(fadeInAnim);
                btnReStart.setVisibility(View.VISIBLE);
                btnReStart.setEnabled(true);
                btnReturn.setVisibility(View.VISIBLE);
                btnReturn.setEnabled(true);
                imgCountDown.setImageDrawable(getResources().getDrawable(R.drawable.num_04));
                tapNum = 0;
                totalResult = 0;
            }
        };
        timerHandler.postDelayed(resultRunnable, 1500);
        timerHandler.postDelayed(buttonRunnable, 2500);
    }

    // タップ数のカウント
    public void onImgTapAreaClick() {
        if (nowOnTime) {
            tapNum += 1;
            if (tapNum % 2 == 0) {
                imgNokotta01.setVisibility(View.VISIBLE);
                imgNokotta02.setVisibility(View.INVISIBLE);
            } else {
                imgNokotta01.setVisibility(View.INVISIBLE);
                imgNokotta02.setVisibility(View.VISIBLE);
            }

            totalResult = judgment.JudgmentPoint(tapNum, totalResult, pointNum);
            totalResult = judgment.JudgmentBonusPoint(tapNum, totalResult, bonusLine, bonusNum);

            fadeOutAnim.setDuration(300);

            // ボーナスポイント時のアニメーション

            if (totalResult < passLine && judgment.JudgmentBonus(tapNum, bonusLine)) {
                if (tapNum % (bonusLine * 2) == 0) {
                    imgOverView03.setImageDrawable(getResources().getDrawable(R.drawable.skill_02));
                } else {
                    imgOverView03.setImageDrawable(getResources().getDrawable(R.drawable.skill_01));
                }
                imgOverView04.setImageDrawable(getResources().getDrawable(R.drawable.skill_cat_01));
                imgOverView03.setVisibility(View.VISIBLE);
                imgOverView04.setVisibility(View.VISIBLE);
                translateBonusAnim.setDuration(500);
                imgOverView04.startAnimation(translateBonusAnim);

                bonusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        imgOverView03.setAnimation(fadeOutAnim);
                        imgOverView04.setAnimation(fadeOutAnim);
                        imgOverView03.setVisibility(View.INVISIBLE);
                        imgOverView04.setVisibility(View.INVISIBLE);
                    }
                };
                timerHandler.postDelayed(bonusRunnable, 700);
            }
            // 勝機！！
            if (totalResult >= passLine && firstPass) {

                imgOverView03.setImageDrawable(getResources().getDrawable(R.drawable.skill_03));
                imgOverView04.setImageDrawable(getResources().getDrawable(R.drawable.skill_cat_02));
                imgOverView03.setVisibility(View.VISIBLE);
                imgOverView04.setVisibility(View.VISIBLE);
                translateBonusAnim.setDuration(500);
                imgOverView04.startAnimation(translateBonusAnim);

                bonusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        imgOverView03.setAnimation(fadeOutAnim);
                        imgOverView04.setAnimation(fadeOutAnim);
                        imgOverView03.setVisibility(View.INVISIBLE);
                        imgOverView04.setVisibility(View.INVISIBLE);
                    }
                };
                timerHandler.postDelayed(bonusRunnable, 1200);
                firstPass = false;
            }
            Log.i("TapDetection", tapNum + "回タップ！現在の点数は" + totalResult + "点です。");
        }
    }

    // トップへ戻る
    public void onBtnReturnClick() {
        // timerHandlerを停止
        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        mediaPlayer.stop();
        Log.i("TapDetection", "mediaPlayerストップ");
        finish();
    }
}