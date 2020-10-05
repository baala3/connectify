package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TRIGGER();
            }
        }, SPLASH_SCREEN_TIME_OUT);


    }
    void TRIGGER()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(this, MainActivity2.class);
            startActivity(i);
            finish();
        }
        else
        {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
            finish();
        }
    };

}