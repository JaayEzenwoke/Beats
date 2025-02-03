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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaay.beats.uiviews.Slate;

public class Grid extends Slate {

    private Paint paint;
    private int size = 80;

    public Grid(@NonNull Context context) {
        super(context);
        initialize();
    }

    public Grid(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        initialize();
    }

    public Grid(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        initialize();
    }

    private void initialize() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw vertical lines
        for (int y = 0; y <= width; y += size) {
            canvas.drawLine(y, 0, y, height, paint);
        }

        // Draw horizontal lines
        for (int x = 0; x <= height; x += size) {
            canvas.drawLine(0, x, width, x, paint);
        }

    }

    public void setSize(int size) {
        this.size = size;
        invalidate(); // Redraw view
    }
}
