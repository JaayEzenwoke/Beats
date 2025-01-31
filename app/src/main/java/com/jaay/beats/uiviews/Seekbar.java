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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jaay.beats.tools.Utils;

public class Seekbar extends View {

    private MediaPlayer player;
    public Handler handler;
    public Runnable seeker;
    private Paint paint;

    private float additions;

    public Seekbar(Context context) {
        super(context);
    }

    public Seekbar(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
    }

    public Seekbar(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        setBackground(getTile(context, attributes).getWalllpaper());
    }

    {
        handler = new Handler();
        paint = new Paint() {
            {
                setAntiAlias(true);
                setColor(Color.RED);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float px = event.getX();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        additions = px;
                        invalidate();
                        
                    }break;
                    case MotionEvent.ACTION_MOVE: {
                        additions = px;
                        int time;
                        invalidate();
                    }break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        additions = px;
                        int duration = player.getDuration() / 1000;
                        int position = (int) (additions * duration);
                        player.seekTo(position);
                        invalidate();
                    }break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int padding_left = getPaddingLeft();
        int padding_top = getPaddingTop();
        int padding_right = getPaddingRight();
        int padding_bottom = getPaddingBottom();

        width = width - padding_left - padding_right;
        height = height - padding_top - padding_bottom;

        canvas.drawRoundRect(padding_left, padding_top, additions, padding_top + height,  0, height / 2F, paint);
    }

    public void setAdditions(int time) {
        float unit = (float) getWidth() / time;

        seeker = new Runnable() {

            @Override
            public void run() {
                if (additions < getWidth()) {
                    additions += unit;
                    invalidate();
                    Utils.debug("additions: " + additions + " width: " + getWidth());
                    handler.postDelayed(this, 1000);
                }else {
                    additions = 0;
                    Utils.debug("XX additions: " + additions + " width: " + getWidth());
                    invalidate();
                }

            }
        };

        handler.post(seeker);
    }

    public void pause(float current_position) {
        handler.removeCallbacks(seeker);
    }

    public void seekTo(int position) {

    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
