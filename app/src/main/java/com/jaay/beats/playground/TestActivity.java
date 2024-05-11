/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.playground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jaay.beats.R;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.uiviews.Slate;

public class TestActivity extends AppCompatActivity {

    private Slate tester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tester = findViewById(R.id.tester);
        tester.setOnClickListener(new View.OnClickListener() {
            Animator animator;
            {
                animator = new Animator( 0, 200, 800) {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onUpdate(float animation_value) {
                        Utils.debug("animation value: " + animation_value);
                        tester.setTranslationX(animation_value);
                    }

                    @Override
                    public void onEnd() {

                    }
                };
            }
            @Override
            public void onClick(View view) {
                animator.start();
            }
        });
    }
}