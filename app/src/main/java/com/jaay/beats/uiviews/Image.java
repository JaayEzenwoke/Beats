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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jaay.beats.design.Background;

@SuppressLint("AppCompatCustomView")
public class Image extends ImageView {

    private Background background;

    private int source;

    public Image(Context context) {
        super(context);
    }

    public Image(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        background = getTile(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
        setClipToOutline(true);
    }

    public Image(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        setBackground(getTile(context, attributes).getWalllpaper());
        setClipToOutline(true);
    }

    public void setImage(int image) {
        setImageResource(image);
        invalidate();
    }

    public Background getWallpaper() {
        return background;
    }

    public void setWallpaper(Background background) {
        this.background = background;
    }
}
