package com.example.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {


    private static final long SPLASH_TIME = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(SplashScreenActivity.this,ValidationActivity.class);
                Intent intent = new Intent(SplashScreenActivity.this,ValidationActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME);


    }
}
