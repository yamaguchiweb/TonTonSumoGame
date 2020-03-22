package com.example.tontonsumogame;

import android.util.Log;

/**
 * ゲームの勝敗を決めるクラス
 */
public class Judgment {


    // 通常獲得点数の計算（現在の総合得点, 基本の取得得点）
    public static int JudgmentPoint(int totalResult, int pointNum){
        totalResult += pointNum;

        return totalResult;
    }

    // ボーナスが加算されるか判定（タップ回数, ボーナスライン）
    public static boolean JudgmentBonus(int tapNum, int bonusLine){
        if(tapNum % bonusLine == 0) {
            return true;
        } else {
            return false;
        }
    }

    // ボーナスの計算（タップ回数, 現在の総合得点, ボーナスライン, ボーナス得点）
    public static int JudgmentBonusPoint(int tapNum, int totalResult, int bonusLine, int bonusNum){

        // タップ回数がボーナスラインの倍数の時、ボーナス得点を加算
        if(JudgmentBonus(tapNum, bonusLine)){
            totalResult += bonusNum;
            Log.i("TapMemo", "--------破ッ(　･ω･)=⊃⊃　ボーナス加算！！" + totalResult + "点！--------");
        }
        return totalResult;
    }


    // 勝敗判定（現在の総合得点, 勝利ライン）
    public static boolean JudgmentResult(int totalResult, int passLine){
        if(totalResult > passLine){
            return true;
        }  else {
            return false;
        }
    }
}
