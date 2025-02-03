package com.jaay.beats.uiviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RollingText extends androidx.appcompat.widget.AppCompatTextView {

    private final float fadeEdgeLength = 50F; // Length of fade effect in pixels

    private boolean isScrolling = false;
    private boolean isTextTooLong = false;
    private int scrollDuration = 6000;
    private int scrollDelay = 2000; // Delay before starting scroll
    private boolean scrollLeftToRight = false;
    private Handler handler;

    private LinearGradient right_gradient;;
    private LinearGradient left_gradient;

    public RollingText(Context context) {
        super(context);
        initialize();
    }

    public RollingText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RollingText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {

        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);

        // Add touch listener for pause/resume


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pauseScrolling();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isTextTooLong) {
                            resumeScrolling();
                        }
                        return true;
                }
                return false;
            }
        });
    }

//    {
//        left_gradient = new LinearGradient(
//                fadeEdgeLength, 0, getWidth() - fadeEdgeLength, 0,
//                new int[]{0x00FFFFFF, getCurrentTextColor(), getCurrentTextColor(), 0x00FFFFFF},
//                new float[]{0f, 0.15f, 0.85f, 1f},
//                Shader.TileMode.CLAMP
//        );
//
//        right_gradient = new LinearGradient(
//                0, 0, getWidth(), 0,
//                new int[]{0x00FFFFFF, getCurrentTextColor(), getCurrentTextColor(), 0x00FFFFFF},
//                new float[]{0f, 0.15f, 0.85f, 1f},
//                Shader.TileMode.CLAMP
//        );
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkIfTextIsTooLong();
    }

    private void checkIfTextIsTooLong() {
        if (getPaint().measureText(getText().toString()) > getWidth()) {
            isTextTooLong = true;
            if (!isScrolling) {
                startScrollingWithDelay();
            }
        } else {
            isTextTooLong = false;
            stopScrolling();
        }
    }

    private void startScrollingWithDelay() {
        if(handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScrolling();
            }
        }, scrollDelay);
    }

    public void startScrolling() {
        if (isTextTooLong) {
            isScrolling = true;
            setSelected(true);
            invalidate();
        }
    }

    public void stopScrolling() {
        isScrolling = false;
        setSelected(false);
        invalidate();
    }

    public void pauseScrolling() {
        isScrolling = false;
        invalidate();
    }

    public void resumeScrolling() {
        if (isTextTooLong) {
            startScrollingWithDelay();
        }
    }

    @Override
    public boolean isFocused() {
        return isScrolling;
    }

    public void setScrollDuration(int duration) {
        this.scrollDuration = duration;
    }

    public void setScrollDelay(int delay) {
        this.scrollDelay = delay;
    }

    public void setScrollDirection(boolean leftToRight) {
        this.scrollLeftToRight = leftToRight;
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Initialize gradients after we have the width
        if (w > 0) {
            left_gradient = new LinearGradient(
                    fadeEdgeLength, 0, w - fadeEdgeLength, 0,
                    new int[]{0x00FFFFFF, getCurrentTextColor(), getCurrentTextColor(), 0x00FFFFFF},
                    new float[]{0f, 0.15f, 0.85f, 1f},
                    Shader.TileMode.CLAMP
            );

            right_gradient = new LinearGradient(
                    0, 0, w, 0,
                    new int[]{0x00FFFFFF, getCurrentTextColor(), getCurrentTextColor(), 0x00FFFFFF},
                    new float[]{0f, 0.15f, 0.85f, 1f},
                    Shader.TileMode.CLAMP
            );
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw fade effect on edges
        if (isTextTooLong && isScrolling) {
            getPaint().setShader(scrollLeftToRight ? left_gradient : right_gradient);
        } else {
            getPaint().setShader(null);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        checkIfTextIsTooLong();
    }
}
