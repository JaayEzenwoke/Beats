/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.uiviews;

import static com.jaay.beats.tools.Utils.getTile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.reels.AllPlaylists;

public class Dots extends LinearLayout {

    private static final int DOTS = 3;

    private Paint paint;

    private int diameter;
    private int spacing;


    public Dots(Context context) {
        super(context);
    }

    public Dots(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
        initialize(context, attributes);
    }

    public Dots(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        setBackground(getTile(context, attributes).getWalllpaper());
        initialize(context, attributes);
    }

    {
        paint = new Paint() {
            {
                setColor(Color.GRAY);
                setStyle(Paint.Style.FILL);
            }
        };
    }

    public void initialize(Context context, AttributeSet attributes) {
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Dots);
        try{
            diameter = stylable.getDimensionPixelSize(R.styleable.Dots_diameter, 0);
            spacing = stylable.getDimensionPixelSize(R.styleable.Dots_spacing, 0);
        }finally {
            stylable.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int start;
        int cx;
        int cy;

        int radius = diameter / 2;

        if(getOrientation() == HORIZONTAL) {
            cy = height / 2;
            start = (width - (diameter * DOTS + spacing * (3 - 1)))/2;

            cx = start + radius;
            canvas.drawCircle(cx, cy, radius, paint);

            cx = (start + (diameter + spacing) + radius);
            canvas.drawCircle(cx, cy, radius, paint);

            cx = (start + 2 * (diameter + spacing) + radius);
            canvas.drawCircle(cx, cy, radius, paint);
        }else {
            cx = width / 2;
            start = (height - (diameter * DOTS + spacing * (3 - 1)))/2;

            cy = start + radius;
            canvas.drawCircle(cx, cy, radius, paint);

            cy = (start + (diameter + spacing) + radius);
            canvas.drawCircle(cx, cy, radius, paint);

            cy = (start + 2 * (diameter + spacing) + radius);
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }
}
