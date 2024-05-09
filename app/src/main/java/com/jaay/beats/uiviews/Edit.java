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
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class Edit extends EditText {

    public Edit(Context context) {
        super(context);
    }

    public Edit(Context context, AttributeSet attributes) {
        super(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
    }

    public Edit(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        setBackground(getTile(context, attributes).getWalllpaper());
    }
}
