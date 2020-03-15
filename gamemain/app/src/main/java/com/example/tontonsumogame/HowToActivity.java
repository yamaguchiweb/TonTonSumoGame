package com.example.tontonsumogame;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HowToActivity extends AppCompatActivity {

    private Button btnReturn; // トップへ戻るボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_how_to);

        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnReturnClick();
            }
        });
    }

    // トップへ戻る
    public void onBtnReturnClick() {
        finish();
    }
}
