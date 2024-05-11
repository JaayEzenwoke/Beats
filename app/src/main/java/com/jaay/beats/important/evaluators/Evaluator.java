package com.jaay.beats.important.evaluators;

import android.animation.TypeEvaluator;

public class Evaluator<T extends Object> implements TypeEvaluator<T> {

    private T value;

    @Override
    public T evaluate(float fraction, T start_value, T end_value) {
        return value;
    }
}
