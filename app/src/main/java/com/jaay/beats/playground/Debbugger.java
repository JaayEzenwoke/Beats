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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.SystemClock;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.animation.Interpolator;

public class Debbugger {

    private static final long ONE_SEC = 1_000_000_000;

    private Choreographer.FrameCallback frame_callback;
    private Choreographer choreographer;
    private Animator animator;

    private long frame_count = 0;
    private long lastframe = 0;

    private boolean calculating;

    public Debbugger() {
    }

    public void start () {
        if (calculating) {
            return;
        }
        calculating = true;
        frame_count = 0;
        lastframe = 0;

        frame_callback = new Choreographer.FrameCallback() {

            @Override
            public void doFrame(long frame_time) {
                if(lastframe != 0) {
                    long interval = frame_time - lastframe;
                    double frame_rate = ONE_SEC / (double) interval;
                }
                frame_count++;
                if (frame_time >= lastframe + ONE_SEC) {
                    // One second has elapsed, calculate the average frame rate
                    double framerate = frame_count / (double) (frame_time - lastframe) * ONE_SEC;
                    // Do something with the averageFrameRate, like logging or displaying it
                    Log.d("frame rate: ", "" + framerate);
                    stop();
                }
                lastframe = frame_time;
                Choreographer.getInstance().postFrameCallback(this);
            }
        };
    }

    public double getFramerate(double rate){
        return rate;
    }

    public void stop() {
        if (frame_callback != null) {
            Choreographer.getInstance().removeFrameCallback(frame_callback);
            frame_callback = null;
        }
    }
}
