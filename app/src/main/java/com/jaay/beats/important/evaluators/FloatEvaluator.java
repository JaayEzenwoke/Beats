package com.jaay.beats.important.evaluators;

import com.jaay.beats.tools.Utils;


public class FloatEvaluator extends Evaluator<Float> {

    public FloatEvaluator(Float... values) {
        this.values = values;
    }
    @Override
    public Float evaluate(float time, Float start_value, Float end_value) {
        return (end_value -  start_value) * time + start_value;
    }
}
