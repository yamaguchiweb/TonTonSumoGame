package com.example.tontonsumogame;

import java.util.Calendar;
import java.util.Date;

/**
 * 時間の制御をするクラス
 */
public class TimeProcessing {

    // 現在時刻を返す
    public Date nowTime() {
        Date nowTime = new Date();

        return nowTime;
    }

    // 開始時刻から計算した終了時刻を返す（加算時間）
    public Date resultTime(Date nowTime, int plusTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.MILLISECOND, plusTime); // plusTimeミリ秒後
        Date resultTime = calendar.getTime();

        return resultTime;
    }

    // 開始時刻から現在時刻を引いて、経過時間を返す
    public long remainingTime(Date startTime){
        long dateTimeTo = startTime.getTime(); //　開始時刻の時間を取得
        long dateTimeFrom = this.nowTime().getTime(); // 現在時間の時間を取得
        long timeDiff = (dateTimeFrom - dateTimeTo); //　経過時間ミリ秒

        return timeDiff;
    }
}