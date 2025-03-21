package com.jaay.beats.important.evaluators;

import android.animation.TypeEvaluator;

import com.jaay.beats.tools.Utils;

public class Evaluator<T extends Object> implements TypeEvaluator<T> {

    protected T value;
    public T[] values;

    @SafeVarargs
    public Evaluator(T... values) {
        this.values = values;
    }

    @Override
    public T evaluate(float fraction, T start_value, T end_value) {
        return null;
    }

    public T calculate(float current_time, long duration) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("Array must contain at least 2 values");
        }

        // Calculate which segment of the animation we're in
        float normalized_time = current_time / duration;
        int segments = values.length - 1;
        float time_per_segment = 1.0F / segments;

        // Find the current segment
        int current_segment = Math.min((int)(normalized_time / time_per_segment), segments - 1);

        // Calculate the time within the current segment
        float time = (normalized_time - (current_segment * time_per_segment)) / time_per_segment;

        // Get the start and end values for the current segment
        T start = values[current_segment];
        T end = values[current_segment + 1];

        return evaluate(time, start, end);
    }

}
