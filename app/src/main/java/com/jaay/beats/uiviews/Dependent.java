package com.jaay.beats.uiviews;

import static com.jaay.beats.tools.Utils.getTile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class Dependent extends RelativeLayout {

    public Dependent(Context context) {
        super(context);
    }

    public Dependent(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
    }

    public Dependent(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        setBackground(getTile(context, attributes).getWalllpaper());
    }
}