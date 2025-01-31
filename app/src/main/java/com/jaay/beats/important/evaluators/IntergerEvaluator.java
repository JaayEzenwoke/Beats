package com.jaay.beats.important.evaluators;

import com.jaay.beats.tools.Utils;


public class IntergerEvaluator extends Evaluator<Integer> {

    public IntergerEvaluator(Integer... values) {
        this.values = values;
    }
    @Override
    public Integer evaluate(float time, Integer start_value, Integer end_value) {
        return (int) ((end_value -  start_value) * time + start_value);
    }
}
