package com.suez.uni.petroleum.engineering.production.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suez.uni.petroleum.engineering.production.R;

import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        mContext = SplashActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        TextView doctorLayout = findViewById(R.id.activity_splash_screen_doctor_text_view);
        LinearLayout studentLayout = findViewById(R.id.activity_splash_screen_student_layout);




        Animation animationFromLeftToRight = AnimationUtils.loadAnimation(this, R.anim.animation_from_left_to_right);
        Animation animationFromRightToLeft = AnimationUtils.loadAnimation(this, R.anim.animation_from_right_to_left);

        doctorLayout.setAnimation(animationFromLeftToRight);

        int SPLASH_SCREEN = 1500;



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                studentLayout.setVisibility(View.VISIBLE);
                studentLayout.setAnimation(animationFromRightToLeft);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_SCREEN);

            }
        }, SPLASH_SCREEN);

    }



}








