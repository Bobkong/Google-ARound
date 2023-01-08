package com.google.ar.core.codelabs.arlocalizer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.codelabs.arlocalizer.consts.Configs;
import com.google.ar.core.codelabs.arlocalizer.R;
import com.google.ar.core.codelabs.arlocalizer.utils.FileUtil;
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;

import qiu.niorgai.StatusBarCompat;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, this.getResources().getColor(R.color.transparent_theme_color));

        new Handler().postDelayed(() -> {
            // determine go to MainActivity or OnboardingActivity
            if (PreferenceUtils.getUserId().length() == 0){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            finish();
        }, 300);
    }
}
