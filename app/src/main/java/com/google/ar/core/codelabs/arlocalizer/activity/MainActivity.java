package com.google.ar.core.codelabs.arlocalizer.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.ar.core.codelabs.arlocalizer.R;

import qiu.niorgai.StatusBarCompat;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, this.getResources().getColor(R.color.bg_color));
    }
}
