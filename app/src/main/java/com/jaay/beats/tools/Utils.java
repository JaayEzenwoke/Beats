/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;

public class Utils {

    public static Background getTile(Context context, AttributeSet attributes){
        Background background = new Background();
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Beats);
        try{
            float radius = stylable.getDimensionPixelSize(R.styleable.Beats_radius, 0);
            float left = stylable.getDimensionPixelSize(R.styleable.Beats_left_radius, 0);
            float top = stylable.getDimensionPixelSize(R.styleable.Beats_top_radius, 0);
            float right = stylable.getDimensionPixelSize(R.styleable.Beats_right_radius, 0);
            float bottom = stylable.getDimensionPixelSize(R.styleable.Beats_bottom_radius, 0);

            int stroke = stylable.getDimensionPixelSize(R.styleable.Beats_stroke, 0);
            int stroke_shade = stylable.getColor(R.styleable.Beats_stroke_shade, 0);
            int shade = stylable.getColor(R.styleable.Beats_shade, 0);
            float[] radii = new float[]{left, top, right, bottom};

            background.setStroke(stroke, stroke_shade);
            background.setRadius(radius, radii);
            background.setShade(shade);
            background.setWallpaper();
            return background;
        }finally {
            stylable.recycle();
        }
    }
}
