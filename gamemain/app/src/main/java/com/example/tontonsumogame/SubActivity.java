package com.example.tontonsumogame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * サブアクティビティ（ゲーム画面）
 */
public class SubActivity extends AppCompatActivity {

    // フィールド変数
    private Button btnReStart, btnReturn; // ゲーム再スタートボタン、トップへ戻るボタン

    private ImageView imgOverView01, imgOverView02; // 前画面画像
    Map<String, Drawable> overViewImages; // 前画面画像のリソースファイルを格納しておくコレクション

    private BackGroundMusic bgm = new BackGroundMusic(); // BGMクラス
    private ImageView btnVolume; // 音量ボタン
    private boolean volumeStatus; // 音量の状態を格納
    Map<String, Drawable> btnVolumeImages; // ボリュームボタンのリソースファイルを格納しておくコレクション

    private final Handler timerHandler = new Handler(); // タイマー処理をするクラス

    // カウントダウンアニメーション用
//    private Runnable countDownRunnable; // カウントダウンのRunnable
    private boolean reStart = false;
    private Runnable countDownRunnable04;
    private Runnable countDownRunnable03;
    private Runnable countDownRunnable02;
    private Runnable countDownRunnable01;
//    private ImageView imgCountDown; // カウントダウン画像
    private boolean countDownHide; // カウントダウンを非表示判定
    private int[] countTime = {1000, 2000, 3000, 4000, 4750}; // 秒数を決めておく
    private AlphaAnimation cdFadeOut = new AlphaAnimation(1, 0); // フェードアウト

    // ゲームの時間制御用
    private Runnable gameStartRunnable; // ゲームスタートのRunnable
    private Runnable gameEndRunnable; // ゲーム終了のRunnable
    private Runnable resultRunnable; // ゲーム結果のRunnable
    private Runnable buttonRunnable; // ボタン表示のRunnable

    // ほこりと力士のエフェクト画像のアニメーション用
    private Runnable hokoriRunnable; // ほこり画像のRunnable
    private ImageView imgHokori01, imgHokori02; // ほこり画像
    private ImageView imgNokotta01, imgNokotta02; // のこった吹き出し
    private int hokoriCount = 1;  // ほこり画像の動いた回数

    private Runnable fighterMoveRunnable; // 力士画像のRunnable
    private ImageView imgTapArea; // 力士画像
    private float imgTapAreaX; // 力士画像のX座標
    private int fighterCount = 1;  // 力士画像の動いた回数
    private boolean fighterBool = true; // 力士画像の動く方向

    // 時間管理用
    private TimeProcessing timeProcessing = new TimeProcessing(); // 時間取得クラス
    private final int TIME = 15000; // ゲームのリミット時間
    private int time = TIME; // 時間の代入
    private Date startTime = null; // 開始時間
    private Date endTime = null; // 終了時間
    private long timeDiff; // 経過時間

    // タップ管理用
    private Judgment judgment = new Judgment(); // 勝敗判定クラス
    private boolean nowOnTime; // 時間内判定
    private int tapNum; //　タップ回数
    private int totalResult; //　トータル点数
    private int pointNum = 10; //　ポイント点数
    private int bonusLine = 20; // ボーナス付与回数
    private int bonusNum = 30; // ボーナス点数
    private int passLine = 800; // クリアライン点数
    private boolean firstPass = true; // 最初にクリアラインを超えたか
    private boolean gameResult; // 勝敗結果

    private AlphaAnimation fadeOutAnim = new AlphaAnimation(1, 0); // フェードアウト
    private AlphaAnimation fadeInAnim = new AlphaAnimation(0, 1); // フェードイン

    // ボーナスポイント時のアニメーション用
    private Runnable bonusRunnable;
    private AnimationSet setBonusAnim = new AnimationSet(true); // アニメーションセット
    private TranslateAnimation translateBonusAnim = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f);// スライドイン
    private AlphaAnimation bonusFadeOutAnim = new AlphaAnimation(1, 0); // フェードアウト

    // 勝敗結果負け時のアニメーション用
    private AnimationSet setAnimRoose = new AnimationSet(true); // アニメーションセット
    private AlphaAnimation setFadeOutAnim = new AlphaAnimation(1, 0); // フェードアウト
    private ScaleAnimation setScaleAnim = new ScaleAnimation(
            1, 0.2f, 1, 0.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f); // 縮小


    /**
     * -----------------------------------------------------------------
     * onCreateメソッド
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // ゲーム再スタート
        btnReStart = findViewById(R.id.btnReStart);
        btnReStart.setEnabled(false);
        btnReStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnStartClick();
            }
        });

        // トップへ戻る
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setEnabled(false);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnReturnClick();
            }
        });

        // 前画面画像のリソースファイルを格納しておくコレクション
        overViewImages = new HashMap<String, Drawable>();
        overViewImages.put("カウントダウン4", getResources().getDrawable(R.drawable.num_04));
        overViewImages.put("カウントダウン3", getResources().getDrawable(R.drawable.num_03));
        overViewImages.put("カウントダウン2", getResources().getDrawable(R.drawable.num_02));
        overViewImages.put("カウントダウン1", getResources().getDrawable(R.drawable.num_01));
        overViewImages.put("猫パンチ", getResources().getDrawable(R.drawable.skill_01));
        overViewImages.put("猫だまし", getResources().getDrawable(R.drawable.skill_02));
        overViewImages.put("勝機", getResources().getDrawable(R.drawable.skill_03));
        overViewImages.put("目から星", getResources().getDrawable(R.drawable.skill_cat_01));
        overViewImages.put("目から炎", getResources().getDrawable(R.drawable.skill_cat_02));
        overViewImages.put("白星", getResources().getDrawable(R.drawable.result_01));
        overViewImages.put("黒星", getResources().getDrawable(R.drawable.result_02));
        overViewImages.put("倒れる", getResources().getDrawable(R.drawable.roose_01));
        overViewImages.put("一時停止", getResources().getDrawable(R.drawable.pause_01));
        overViewImages.put("そこまで", getResources().getDrawable(R.drawable.stop_01));

        imgOverView01 = findViewById(R.id.imgOverView01);
        imgOverView02 = findViewById(R.id.imgOverView02);

        // BGM準備
        bgm.onCreate(this);

        Intent intent = getIntent();
        volumeStatus = intent.getBooleanExtra("volumeStatus", volumeStatus);


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

        // カウントダウンアニメーション
//        imgCountDown = findViewById(R.id.imgCountDown);
//        countDownRunnable = new Runnable() {
//            @Override
//            public void run() {
//                countDown(countTime[0], countTime[1], countTime[2], countTime[3]);
//            }
//        };
        countDownRunnable04 = new Runnable() {
            @Override
            public void run() {
                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン4"));
                imgOverView01.setVisibility(View.VISIBLE);
                cdFadeOut.setDuration(1000);
                cdFadeOut.setFillAfter(false);
                imgOverView01.startAnimation(cdFadeOut);
            }
        };
        countDownRunnable03 = new Runnable() {
            @Override
            public void run() {
                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン3"));
                imgOverView01.setVisibility(View.VISIBLE);
                cdFadeOut.setDuration(1000);
                cdFadeOut.setFillAfter(false);
                imgOverView01.startAnimation(cdFadeOut);
            }
        };
        countDownRunnable02 = new Runnable() {
            @Override
            public void run() {
                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン2"));
                imgOverView01.setVisibility(View.VISIBLE);
                cdFadeOut.setDuration(1000);
                cdFadeOut.setFillAfter(false);
                imgOverView01.startAnimation(cdFadeOut);
            }
        };
        countDownRunnable01 = new Runnable() {
            @Override
            public void run() {
                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン1"));
                imgOverView01.setVisibility(View.VISIBLE);
            }
        };
        gameStartRunnable = new Runnable() {
            @Override
            public void run() {
                gameStart();
                imgOverView01.setVisibility(View.INVISIBLE);
            }
        };
//        timerHandler.post(countDownRunnable);
        timerHandler.postDelayed(countDownRunnable04, countTime[0]);
        timerHandler.postDelayed(countDownRunnable03, countTime[1]);
        timerHandler.postDelayed(countDownRunnable02, countTime[2]);
        timerHandler.postDelayed(countDownRunnable01, countTime[3]);
        timerHandler.postDelayed(gameStartRunnable, countTime[4]);

        imgHokori01 = findViewById(R.id.imgHokori01);
        imgHokori02 = findViewById(R.id.imgHokori02);
        imgNokotta01 = findViewById(R.id.imgNokotta01);
        imgNokotta02 = findViewById(R.id.imgNokotta02);

        // 力士画像タップ
        imgTapArea = findViewById(R.id.imgTapArea);
        imgTapArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImgTapAreaClick();
            }
        });
    }


    /**
     * カウントダウンアニメーションのメソッド
     */
//    public void countDown(int num1, int num2, int num3, int num4) {
//        Runnable countDownRunnable04 = new Runnable() {
//            @Override
//            public void run() {
//                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン4"));
//                imgOverView01.setVisibility(View.VISIBLE);
//                cdFadeOut.setDuration(1000);
//                cdFadeOut.setFillAfter(false);
//                imgOverView01.startAnimation(cdFadeOut);
//            }
//        };
//        Runnable countDownRunnable03 = new Runnable() {
//            @Override
//            public void run() {
//                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン3"));
//                imgOverView01.setVisibility(View.VISIBLE);
//                cdFadeOut.setDuration(1000);
//                cdFadeOut.setFillAfter(false);
//                imgOverView01.startAnimation(cdFadeOut);
//            }
//        };
//        Runnable countDownRunnable02 = new Runnable() {
//            @Override
//            public void run() {
//                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン2"));
//                imgOverView01.setVisibility(View.VISIBLE);
//                cdFadeOut.setDuration(1000);
//                cdFadeOut.setFillAfter(false);
//                imgOverView01.startAnimation(cdFadeOut);
//            }
//        };
//        Runnable countDownRunnable01 = new Runnable() {
//            @Override
//            public void run() {
//                imgOverView01.setImageDrawable(overViewImages.get("カウントダウン1"));
//                imgOverView01.setVisibility(View.VISIBLE);
//            }
//        };
//        timerHandler.postDelayed(countDownRunnable04, num1);
//        timerHandler.postDelayed(countDownRunnable03, num2);
//        timerHandler.postDelayed(countDownRunnable02, num3);
//        timerHandler.postDelayed(countDownRunnable01, num4);
//    }

    /**
     * 画面新規表示時メソッド
     */
    public void onStart() {
        super.onStart();
    }

    /**
     * 画面表示時メソッド
     */
    public void onResume() {
        super.onResume();
        // 画面表示時のBGM状態
        if (volumeStatus) {
            btnVolume.setBackground(btnVolumeImages.get("on"));
        } else {
            btnVolume.setBackground(btnVolumeImages.get("off"));
        }

        if(reStart){
            imgOverView01.setImageDrawable(overViewImages.get("一時停止"));
            imgOverView01.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 画面一時停止時メソッド
     */
    public void onPause() {
        super.onPause();
        // 全てのRunnableとBGMを停止
//        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(countDownRunnable04);
        timerHandler.removeCallbacks(countDownRunnable03);
        timerHandler.removeCallbacks(countDownRunnable02);
        timerHandler.removeCallbacks(countDownRunnable01);
        timerHandler.removeCallbacks(gameStartRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        bgm.onPause();

        Log.i("TapMemo", "onPauseされました");

        // 画面停止時に、ゲームの残りの時間を計算
        if (startTime != null && nowOnTime) {
            timeDiff = timeProcessing.remainingTime(startTime); // 現在時間からスタート時間を引く
            time = (int) (time - timeDiff); // 残り時間から経過時間を引く
        }

        nowOnTime = false;
        reStart = true;

        // 一時停止時の画面表示セット
        imgOverView01.setImageDrawable(null);
        imgOverView02.setVisibility(View.INVISIBLE);
//        imgCountDown.setVisibility(View.INVISIBLE);
        btnReStart.setVisibility(View.VISIBLE);
        btnReStart.setEnabled(true);
        btnReturn.setVisibility(View.VISIBLE);
        btnReturn.setEnabled(true);
    }

    /**
     * 画面完全停止時メソッド
     */
    public void onStop() {
        super.onStop();
    }

    /**
     * 画面が破棄される前の情報保存
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (startTime == null) {
            /* 開始前〜カウントダウン途中の場合（終了までの残り時間がMAX）
             * [各値の状態]
             * カウントダウン非表示：false
             * タップ可能時間内：false
             * - スタート時間：startTime
             * - 終了時間：endTime
             * 経過時間：0
             * 終了までの秒数：MAX
             * タップ回数：0
             * 合計点数：0
             * 勝利ラインへの到達：初めて
             * - BGM：一時停止
             * BGM状態：volumeStatus
             **/
            outState.putBoolean("countDownHide", false);
            outState.putBoolean("nowOnTime", false);
            outState.putLong("timeDiff", 0);
            outState.putInt("time", TIME);
            outState.putInt("tapNum", 0);
            outState.putInt("totalResult", 0);
            outState.putBoolean("firstPass", true);
            outState.putBoolean("volumeStatus", volumeStatus);
        } else {
            /* ゲームスタート後の場合（終了までの秒数：time < TIME）
             * [各値の状態]
             * カウントダウン非表示：true
             * タップ可能時間内：false
             * スタート時間：startTime
             * - 終了時間：endTime
             * 経過時間：timeDiff
             * 終了までの秒数：time
             * タップ回数：tapNum
             * 合計点数：totalResult
             * 勝利ラインへの到達：firstPass
             * - BGM：一時停止
             * BGM状態：volumeStatus
             **/
            outState.putBoolean("countDownHide", true);
            outState.putBoolean("nowOnTime", false);
            outState.putLong("startTime", startTime.getTime());
            outState.putLong("timeDiff", timeDiff);
            outState.putInt("time", time);
            outState.putInt("tapNum", tapNum);
            outState.putInt("totalResult", totalResult);
            outState.putBoolean("firstPass", firstPass);
            outState.putBoolean("volumeStatus", volumeStatus);
        }
    }

    /**
     * 画面復帰時の情報呼び出し
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // 全てのRunnableとBGMを停止
//        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(countDownRunnable04);
        timerHandler.removeCallbacks(countDownRunnable03);
        timerHandler.removeCallbacks(countDownRunnable02);
        timerHandler.removeCallbacks(countDownRunnable01);
        timerHandler.removeCallbacks(gameStartRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        bgm.onPause();

        Log.i("TapMemo", "情報が復元されました");

        /**
         * [各値の状態]
         * カウントダウン非表示：countDownHide
         * タップ可能時間内：nowOnTime
         * スタート時間：startTime
         * - 終了時間：endTime
         * 経過時間：timeDiff
         * 終了までの秒数：time
         * タップ回数：tapNum
         * 合計点数：totalResult
         * 勝利ラインへの到達：firstPass
         * - BGM：一時停止
         * BGM状態：volumeStatus
         **/
        countDownHide = savedInstanceState.getBoolean("countDownHide");
        nowOnTime = savedInstanceState.getBoolean("nowOnTime");
        startTime = new Date(savedInstanceState.getLong("startTime"));
        timeDiff = savedInstanceState.getLong("timeDiff");
        time = savedInstanceState.getInt("time");
        tapNum = savedInstanceState.getInt("tapNum");
        totalResult = savedInstanceState.getInt("totalResult");
        firstPass = savedInstanceState.getBoolean("firstPass");
        volumeStatus = savedInstanceState.getBoolean("volumeStatus");

        nowOnTime = false;
        reStart = true;

        // 一時停止時の画面表示セット
        imgOverView02.setVisibility(View.INVISIBLE);
//        imgCountDown.setVisibility(View.INVISIBLE);
        btnReStart.setVisibility(View.VISIBLE);
        btnReStart.setEnabled(true);
        btnReturn.setVisibility(View.VISIBLE);
        btnReturn.setEnabled(true);
    }


    /**
     * スタートボタンを押したときのメソッド
     */
    public void onBtnStartClick() {
        // 前面画像とボタンの非表示
        imgOverView01.setVisibility(View.INVISIBLE);
        imgOverView02.setVisibility(View.INVISIBLE);
        btnReStart.setVisibility(View.INVISIBLE);
        btnReStart.setEnabled(false);
        btnReturn.setVisibility(View.INVISIBLE);
        btnReturn.setEnabled(false);

        if (!countDownHide) {
            /**
             * [各値の状態]
             * - カウントダウン非表示：false
             * - タップ可能時間内：false
             * スタート時間：空
             * - 終了時間：空
             * - 経過時間：0
             * 終了までの秒数：MAX
             * タップ回数：0
             * 合計点数：0
             * 勝利ラインへの到達：初めて
             * - BGM：一時停止
             * - BGM状態：volumeStatus
             **/
            time = TIME;
            tapNum = 0;
            totalResult = 0;
            firstPass = true;

//            timerHandler.post(countDownRunnable);
            timerHandler.postDelayed(countDownRunnable04, countTime[0]);
            timerHandler.postDelayed(countDownRunnable03, countTime[1]);
            timerHandler.postDelayed(countDownRunnable02, countTime[2]);
            timerHandler.postDelayed(countDownRunnable01, countTime[3]);
            timerHandler.postDelayed(gameStartRunnable, countTime[4]);
        } else {
            /**
             * [各値の状態]
             * - カウントダウン非表示：true
             * - タップ可能時間内：false
             * - スタート時間：startTime
             * - 終了時間：endTime
             * - 経過時間：timeDiff
             * - 終了までの秒数：time
             * - タップ回数：tapNum
             * - 合計点数：totalResult
             * - 勝利ラインへの到達：firstPass
             * - BGM：一時停止
             * - BGM状態：volumeStatus
             **/
            // time = (int) (time - timeDiff);
            gameStart();
        }
        Log.i("TapMemo", "■■■timeDiff■■■ " + timeDiff + " ■■■time■■■ " + time);
    }

    /**
     * ゲームスタート後のメソッド
     */
    public void gameStart() {
        /**
         * [各値の状態]
         * カウントダウン非表示：true
         * タップ可能時間内：true
         * スタート時間：取得
         * 終了時間：取得
         * - 経過時間：timeDiff
         * - 終了までの秒数：time
         * - タップ回数：tapNum
         * - 合計点数：totalResult
         * - 勝利ラインへの到達：firstPass
         * BGM：再生開始
         * - BGM状態：volumeStatus
         **/
        countDownHide = true;
        nowOnTime = true;
        startTime = timeProcessing.nowTime(); // 開始時間
        endTime = timeProcessing.resultTime(timeProcessing.nowTime(), time); // 終了時間

        Log.i("TapMemo", "開始時刻【" + startTime + "】");
        Log.i("TapMemo", "終了時刻【" + endTime + "】");

        if (volumeStatus) {
            bgm.onStart();
        }

        // ほこりと力士のエフェクト画像のアニメーション
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

        // time時間経過後、ゲーム結果の表示メソッドを呼ぶ
        gameEndRunnable = new Runnable() {
            @Override
            public void run() {
                gameEnd();
            }
        };
        timerHandler.postDelayed(gameEndRunnable, time);
    }


    /**
     * ゲーム結果の表示メソッド
     */
    public void gameEnd() {

        // エフェクト画像の非表示、ほこりと力士のエフェクト画像のアニメーション停止
        imgNokotta01.setVisibility(View.INVISIBLE);
        imgNokotta02.setVisibility(View.INVISIBLE);
        imgHokori01.setVisibility(View.INVISIBLE);
        imgHokori02.setVisibility(View.INVISIBLE);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        timerHandler.removeCallbacks(bonusRunnable);

        imgOverView02.setImageDrawable(null);

        imgOverView01.setImageDrawable(overViewImages.get("そこまで"));
        imgOverView01.setVisibility(View.VISIBLE);

        // 勝敗画像表示
        resultRunnable = new Runnable() {
            @Override
            public void run() {

                gameResult = judgment.JudgmentResult(totalResult, passLine);

                if (gameResult) {
                    imgOverView01.setImageDrawable(overViewImages.get("白星"));
                    Log.i("TapMemo", "終了時刻です。あなたの勝ちです。" +
                            totalResult + "点");
                } else {
                    imgOverView01.setImageDrawable(overViewImages.get("黒星"));
                    imgOverView02.setImageDrawable(overViewImages.get("倒れる"));
                    imgOverView02.setVisibility(View.VISIBLE);
                    setFadeOutAnim.setDuration(4000);
                    setFadeOutAnim.setFillAfter(true);
                    setScaleAnim.setDuration(4000);
                    setScaleAnim.setFillAfter(true);
                    setAnimRoose.addAnimation(setFadeOutAnim);
                    setAnimRoose.addAnimation(setScaleAnim);
                    imgOverView02.startAnimation(setAnimRoose);

                    Log.i("TapMemo", "終了時刻です。あなたの負けです。" +
                            totalResult + "点");
                }
            }
        };

        // トップへ戻る、再スタートボタンの表示
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
                tapNum = 0;
                totalResult = 0;
            }
        };
        timerHandler.postDelayed(resultRunnable, 1500);
        timerHandler.postDelayed(buttonRunnable, 3000);

        /**
         * [各値の状態リセット]
         * カウントダウン非表示：false
         * タップ可能時間内：false
         * スタート時間：startTime
         * 終了時間：endTime
         * 経過時間：0
         * - 終了までの秒数：time
         * - タップ回数：tapNum
         * - 合計点数：totalResult
         * - 勝利ラインへの到達：firstPass
         * BGM：一時停止
         * - BGM状態：volumeStatus
         **/
        countDownHide = false;
        nowOnTime = false;
        startTime = null;
        endTime = null;
        timeDiff = 0;
        bgm.onPause();
    }


    /**
     * タップ数のカウントメソッド
     */
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

            totalResult = judgment.JudgmentPoint(totalResult, pointNum);
            totalResult = judgment.JudgmentBonusPoint(tapNum, totalResult, bonusLine, bonusNum);

            // ボーナスポイント時のアニメーション
            if (totalResult < passLine && judgment.JudgmentBonus(tapNum, bonusLine)) {
                if (tapNum % (bonusLine * 2) == 0) {
                    imgOverView01.setImageDrawable(overViewImages.get("猫だまし"));
                } else {
                    imgOverView01.setImageDrawable(overViewImages.get("猫パンチ"));
                }
                imgOverView02.setImageDrawable(overViewImages.get("目から星"));
                imgOverView01.setVisibility(View.VISIBLE);
                imgOverView02.setVisibility(View.VISIBLE);
                translateBonusAnim.setDuration(500);
                translateBonusAnim.setFillAfter(false);
                imgOverView02.startAnimation(translateBonusAnim);
                bonusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        imgOverView01.setVisibility(View.INVISIBLE);
                        imgOverView02.setVisibility(View.INVISIBLE);
                    }
                };
                timerHandler.postDelayed(bonusRunnable, 700);
            }
            // 勝機！！
            if (totalResult >= passLine && firstPass) {

                imgOverView01.setImageDrawable(overViewImages.get("勝機"));
                imgOverView02.setImageDrawable(overViewImages.get("目から炎"));
                imgOverView01.setVisibility(View.VISIBLE);
                imgOverView02.setVisibility(View.VISIBLE);
                translateBonusAnim.setDuration(500);
                translateBonusAnim.setFillAfter(false);
                imgOverView02.startAnimation(translateBonusAnim);
                bonusRunnable = new Runnable() {
                    @Override
                    public void run() {
                        imgOverView01.setVisibility(View.INVISIBLE);
                        imgOverView02.setVisibility(View.INVISIBLE);
                    }
                };
                timerHandler.postDelayed(bonusRunnable, 1200);
                firstPass = false;
            }
            Log.i("TapMemo", tapNum + "回タップ！現在の点数は" + totalResult + "点です。");
        }
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

            // ゲーム時間内であればBGM開始
            if (nowOnTime) {
                bgm.onStart();
            }
        }
    }


    /**
     * 端末の戻るボタンを押した時のメソッド
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 全てのRunnableとBGMを停止
//        timerHandler.removeCallbacks(countDownRunnable);
            timerHandler.removeCallbacks(countDownRunnable04);
            timerHandler.removeCallbacks(countDownRunnable03);
            timerHandler.removeCallbacks(countDownRunnable02);
            timerHandler.removeCallbacks(countDownRunnable01);
            timerHandler.removeCallbacks(gameStartRunnable);
            timerHandler.removeCallbacks(gameEndRunnable);
            timerHandler.removeCallbacks(buttonRunnable);
            timerHandler.removeCallbacks(hokoriRunnable);
            timerHandler.removeCallbacks(fighterMoveRunnable);
            bgm.onStop();

            Intent intent = new Intent();
            intent.putExtra("volumeStatus", volumeStatus);
            setResult(RESULT_OK, intent);
            finish();
        }
        return false;
    }

    /**
     * トップへ戻るボタンを押した時のメソッド
     */
    public void onBtnReturnClick() {
        // 全てのRunnableとBGMを停止
//        timerHandler.removeCallbacks(countDownRunnable);
        timerHandler.removeCallbacks(countDownRunnable04);
        timerHandler.removeCallbacks(countDownRunnable03);
        timerHandler.removeCallbacks(countDownRunnable02);
        timerHandler.removeCallbacks(countDownRunnable01);
        timerHandler.removeCallbacks(gameStartRunnable);
        timerHandler.removeCallbacks(gameEndRunnable);
        timerHandler.removeCallbacks(buttonRunnable);
        timerHandler.removeCallbacks(hokoriRunnable);
        timerHandler.removeCallbacks(fighterMoveRunnable);
        bgm.onStop();

        Intent intent = new Intent();
        intent.putExtra("volumeStatus", volumeStatus);
        setResult(RESULT_OK, intent);
        finish();
    }
}