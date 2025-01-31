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

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.jaay.beats.R;

public class Beats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_beats);

        Window window = getWindow();
        int beat_color = getResources().getColor(R.color.beat_color);
        window.setStatusBarColor(beat_color);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(beat_color);

        getInitialization();
        getIdentification();
        getAdjustments();
        getActionAttachments();
    }

    protected void getIdentification(){

    }

    protected void getInitialization(){

    }

    protected void getAdjustments(){

    }

    protected void getActionAttachments(){

    }
}