package com.jaay.beats.important;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.jaay.beats.important.evaluators.Evaluator;
import com.jaay.beats.tools.Utils;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public abstract class Animator implements Choreographer.FrameCallback {

    private float current_time;
    private long start_time;

    private Float start_value;
    private Float end_value;

    private float fraction;
    private long duration;

    private boolean reverse;
    private boolean reversed;
    private Evaluator[] evaluators;
    private Object[] values;

    // Add interpolator field with a default linear interpolator
    private Interpolator interpolator;

    private CountDownLatch latch;

    public Animator(float start_value, float end_value) {
        this.start_value = start_value;
        this.end_value = end_value;
        this.interpolator = new LinearInterpolator(); // Default linear interpolation
    }

    public Animator(Interpolator interpolator, Evaluator... evaluators) {
        this.evaluators = evaluators;
        latch = new CountDownLatch(evaluators.length);
        values = new Object[evaluators.length];
        setInterpolator(interpolator);
    }

    @Override
    public void doFrame(long nano_time) {
        if(reverse) {
            current_time = (nano_time - start_time) / 1_000_000F;
            fraction = current_time / duration;
            fraction = 1 - fraction;

            if (fraction == 1) {
                onStart();
                reversed = false;
            }

            // Apply interpolation to the reversed fraction
            float interpolated_fraction = interpolator.getInterpolation(fraction);

            if (fraction > 0) {
                evaluate(interpolated_fraction * duration);
                Choreographer.getInstance().postFrameCallback(this);
            }

            if (fraction <= 0) {
                first();
                onEnd();
                reversed = true;
            }

        } else {
            current_time = (nano_time - start_time) / 1_000_000F;
            fraction = current_time / duration;

            if(fraction == 0) {
                onStart();
            }

            // Apply interpolation to the forward fraction
            float interpolated_fraction = interpolator.getInterpolation(Math.min(fraction, 1.0f));

            if(fraction < 1) {
                evaluate(interpolated_fraction * duration);
                Choreographer.getInstance().postFrameCallback(this);
            }
            if(fraction >= 1) {
                last();
                onEnd();
            }
        }
    }

    // Add setter for interpolator
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator != null ? interpolator : new LinearInterpolator();
    }

    // Rest of the methods remain the same
    public void start() {
        start_time = System.nanoTime();
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void play() {
        reverse = false;
        start();
    }

    public void reverse() {
        reverse = true;
        start();
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void evaluate(float current_time) {
        Handler handler = new Handler(Looper.getMainLooper());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntStream.range(0, evaluators.length)
                    .parallel()
                    .forEach(index -> {
                        values[index] = evaluators[index].calculate(current_time, duration);
                        latch.countDown();

                        if (latch.getCount() == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (this) {
                                        onUpdate(values);
                                    }
                                }
                            });
                        }
                    });
        }
    }

    public void last() {
        Utils.debug("values-length: " + values[values.length - 1]);
        for (int i = 0; i < evaluators.length; i++) {
            Evaluator evaluator = evaluators[i];
            values[i] = evaluator.values[evaluator.values.length - 1];
            onUpdate(values);
        }
    }

    public void first() {
        for (int i = 0; i < evaluators.length; i++) {
            Evaluator evaluator = evaluators[i];
            values[i] = evaluator.values[0];
            onUpdate(values);
        }
    }

    public abstract void onStart();
    public abstract void onUpdate(Object[] animation_value);
    public abstract void onEnd();
}