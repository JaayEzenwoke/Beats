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
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.tools.Utils;

public class Seekbar extends View implements Runnable{

    private MediaPlayer player;
    public Handler handler;
    public Runnable seeker;
    private Paint paint;

    public float additions;
    private int seek_shade;
    private boolean dotted;

    public Seekbar(Context context) {
        super(context);
        init();
    }

    public Seekbar(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        setBackground(getTile(context, attributes).getWalllpaper());
        init();
        initialize(context, attributes);
    }

    public Seekbar(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
        setBackground(getTile(context, attributes).getWalllpaper());
        init();
        initialize(context, attributes);
    }

    private void init() {
        handler = new Handler();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(seek_shade != 0 ? seek_shade : Color.WHITE);
    }

    public void initialize(Context context, @Nullable AttributeSet attributes) {
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Seekbar);
        try{
            seek_shade = stylable.getInt(R.styleable.Seekbar_seek_shade, 0);
            dotted = stylable.getBoolean(R.styleable.Seekbar_dotted, false);
            paint.setColor(seek_shade);
        }finally {
            stylable.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (player == null) return false;

                float px = event.getX();
                // Constrain to view bounds
                px = Math.max(0, Math.min(px, getWidth()));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        additions = px;
                        invalidate();
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        additions = px;
                        invalidate();
                        return true;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        additions = px;
                        // Calculate the seek position as a proportion of the view width
                        float proportion = px / getWidth();
                        int seekPosition = (int) (player.getDuration() * proportion);

                        // Seek the media player
                        player.seekTo(seekPosition);
                        invalidate();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int circle_width = height;

        int padding_left = getPaddingLeft();
        int padding_top = getPaddingTop();
        int padding_right = getPaddingRight();
        int padding_bottom = getPaddingBottom();

        width = width - padding_left - padding_right;
        height = height - padding_top - padding_bottom;

        // Ensure additions is within bounds
        additions = Math.max(padding_left, Math.min(additions, width));

        if(dotted) {
            float offset = height / 5F;

            paint.setColor(Utils.getTransluscency(seek_shade, 0x20));
            canvas.drawRoundRect(padding_left, offset, width, (padding_top + height) - offset, circle_width, circle_width, paint);

            paint.setColor(Utils.getTransluscency(seek_shade, 0x80));
            canvas.drawRoundRect(padding_left, offset, additions, (padding_top + height) - offset, height / 2F, height, paint);

            paint.setColor(seek_shade);

            float cx = additions > (circle_width / 2F) ? (additions - (circle_width / 2F)) : padding_left;
            cx = Math.max(padding_left, Math.min(cx, width - padding_right - circle_width));

            canvas.drawRoundRect(cx, 0, cx + circle_width, circle_width, circle_width, circle_width, paint);
        } else {
            // Draw background track
            paint.setColor(Utils.getTransluscency(seek_shade, 0x20));
            canvas.drawRoundRect(padding_left, padding_top, width, padding_top + height, height/2, height/2, paint);

            // Draw progress track
            paint.setColor(seek_shade);
            canvas.drawRoundRect(padding_left, padding_top, additions, padding_top + height, height/2, height/2, paint);
        }
    }

    // This method is now used differently - the playing class controls the seeking
    public void setAdditions(float duration) {
        // Store the duration for calculations
        if (player != null) {
            // This is simply initializing the seekbar for a new track
            additions = 0;
            invalidate();
        }
    }

    public void pause(float current_position) {
        // Convert position from 0-1 to pixel coordinates
        additions = current_position * getWidth();
        invalidate();

        // Remove any automatic updates
        if (handler != null && seeker != null) {
            handler.removeCallbacks(seeker);
        }
    }

    public void seekTo(int position) {
        if (player != null) {
            player.seekTo(position);
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        // This method is not used anymore - the Playing class handles the updates
    }
}