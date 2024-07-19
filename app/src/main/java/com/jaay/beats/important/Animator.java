package com.jaay.beats.important;

import android.animation.ValueAnimator;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.View;
import android.view.animation.Interpolator;

public abstract class Animator implements Choreographer.FrameCallback {

    private  float start_value;
    
    private long start_time;
    private float end_value;
    private float fraction;
    private long duration;

    public Animator(float start_value, float end_value, long duration) {
        this.start_value = start_value;
        this.end_value = end_value;
        this.duration = duration;
    }

    float current_time;
    
    @Override
    public void doFrame(long nano_time) {
        current_time = (nano_time - start_time) / 1_000_000F;
        float animation_value = calculate(current_time);
        fraction = current_time / duration;
        if(fraction == 0) {
            onStart();
        }
        if(fraction < 1) {
            onUpdate(animation_value);
            Choreographer.getInstance().postFrameCallback(this);
        }
        if(fraction >= 1) {
            onEnd();
        }
    }
    public float calculate(float current_time) {
        return (end_value - start_value) * (current_time / duration);
    }

    public void start() {
        start_time = System.nanoTime();
        Choreographer.getInstance().postFrameCallback(this);
    }


    public abstract void onStart();
    public abstract void onUpdate(float animation_value);
    public abstract void onEnd();

}
