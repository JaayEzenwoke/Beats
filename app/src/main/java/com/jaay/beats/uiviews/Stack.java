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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jaay.beats.R;

public class Stack extends LinearLayout {

    private boolean reverse;

    public Stack(Context context) {
        super(context);
    }

    public Stack(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Linear);
        try{
            reverse = stylable.getBoolean(R.styleable.Linear_reverse, false);
        }finally {
            stylable.recycle();
        }
        setBackground(getTile(context, attributes).getWalllpaper());
    }

    public Stack(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Linear);
        try{
            reverse = stylable.getBoolean(R.styleable.Linear_reverse, false);
        }finally {
            stylable.recycle();
        }
    }

//    TODO revisit
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        if(reverse) {
//            int children = getChildCount();
//            int xtop = getPaddingTop();
//            for (int index = 0; index < children; index++) {
//                View child = getChildAt(index);
//                if (child.getVisibility() == GONE) continue;
//
//                LayoutParams params = (LayoutParams) child.getLayoutParams();
//                int childWidth = child.getMeasuredWidth();
//                int childHeight = child.getMeasuredHeight();
//
//                int xleft = getPaddingLeft() + params.leftMargin;
//                int xright = xleft + childWidth;
//                int xbottom = xtop + childHeight;
//
//                child.layout(xleft, xtop + params.topMargin, xright, xbottom + params.bottomMargin);
//
//                // Adjust the xtop for the next child, taking into account margins
//                xtop = xbottom + params.bottomMargin;
//                break;
//            }

//            switch (getOrientation()) {
//                case HORIZONTAL :
//
//                    break;
//                case VERTICAL : {
//
//                }
//            }
//        }else {
            super.onLayout(changed, left, top, right, bottom);
//        }
    }
}
