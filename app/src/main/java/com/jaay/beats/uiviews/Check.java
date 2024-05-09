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

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;

public class Check extends Image {

    private boolean checked;

    public Check(Context context) {
        super(context);
    }

    public Check(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
    }

    public Check(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setSelection(boolean checked) {
        Background background = getWallpaper();
        int shade = background.getShade();

        if(checked) {
            background.setShade(shade);
            setImageResource(R.drawable.check);
            setWallpaper(background);
            setChecked(true);
        }else {
            background.setShade(0x00000000);
            setImageResource(0x00000000);
            setWallpaper(background);
            setChecked(false);
        }
    }
}
