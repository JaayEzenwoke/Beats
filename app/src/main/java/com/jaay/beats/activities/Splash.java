/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.jaay.beats.R;
import com.jaay.beats.concurrency.Runner;
import com.jaay.beats.playground.Debbugger;
import com.jaay.beats.playground.TestActivity;
import com.jaay.beats.types.Playlist;

import java.io.File;
import java.util.ArrayList;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Debbugger debbugger = new Debbugger();
        debbugger.start();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Runner runner = new Runner();
        runner.setData(this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent intent;
                intent = new Intent(Splash.this, TestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("runner", runner);
                startActivity(intent);
                finish();
            }
        }, 1200);
    }
}