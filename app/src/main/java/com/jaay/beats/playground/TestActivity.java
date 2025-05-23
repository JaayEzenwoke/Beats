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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.jaay.beats.R;
import com.jaay.beats.core.Beat;
import com.jaay.beats.important.Animator;
import com.jaay.beats.important.evaluators.FloatEvaluator;
import com.jaay.beats.important.evaluators.IntergerEvaluator;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.Image;
import com.jaay.beats.uiviews.Seekbar;
import com.jaay.beats.uiviews.Slate;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private MediaPlayer player;
    private Slate drag_test;
    private Stack play_box;
    private Stack smaller;
    private Seekbar seekbar;
    private Slate tester;

    private Text equalizer;
    private Text play;
    private Text pause;
    private Text cut;

    private int smaller_height;
    private int offset;
    private int height;

    private Image back;



    private static final String TAG = "MainActivity";
    private Handler handler;
    private Runnable loggingRunnable;
    private int counter = 0;


    private View.OnTouchListener top_toucher;
    private Grid grid;
    private Text debugger;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Start the background service to keep the app alive
//        Intent serviceIntent = new Intent(this, BackgroundService.class);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent);
//        }

        // Initialize the logging handler and runnable
        handler = new Handler();
        loggingRunnable = new Runnable() {

            @Override
            public void run() {
//                Log.d(TAG, "Counter value: " + counter++);
                handler.postDelayed(this, 1000); // Log every second
            }
        };

        // Start logging values immediately
        handler.post(loggingRunnable);

//        if(true) return;
        grid = findViewById(R.id.grid);

        debugger = findViewById(R.id.debugger);
        drag_test = findViewById(R.id.drag_test);
        play_box = findViewById(R.id.play_box);
        seekbar = findViewById(R.id.test_seekbar);
        equalizer = findViewById(R.id.equalizer);
        smaller = findViewById(R.id.smaller);
        play = findViewById(R.id.test_play);
        tester = findViewById(R.id.tester);
        pause = findViewById(R.id.pause);
        back = findViewById(R.id.back);
        cut = findViewById(R.id.cut);

        Beat beat = new Beat(getApplicationContext());
        ArrayList<Audio> tracks = beat.getTracks();
        final View content = findViewById(android.R.id.content);

        // Add a global layout listener to get the height after layout is drawn
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Get the height of the root view
                height = content.getHeight();

                // Remove the listener to avoid being called multiple times
                content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
        }

//        tester.setOnClickListener(new View.OnClickListener() {
//            Animator animator;
//            {
//                animator = new Animator( 0, 200, 800) {
//                    @Override
//                    public void onStart() {
//                        tester.setBackgroundColor(0xFF00FF00);
//                    }
//
//                    @Override
//                    public void onUpdate(float animation_value) {
//                        Utils.debug("animation_value: " + animation_value);
//                        tester.setTranslationX(animation_value);
//                        tester.setBackgroundColor(0xFFFF0000);
//                    }
//
//                    @Override
//                    public void onEnd() {
//                        tester.setBackgroundColor(0xFF00FFFF);
////                        reverse();
//                    }
//                };
//            }
//
//            @Override
//            public void onClick(View view) {
//                animator.start();
//            }
//        });

        seekbar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                seekbar.setAdditions();
            }
        });

        View.OnClickListener back_listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.debug("=================here================");
                drag_animator.play();
            }
        };

        View.OnTouchListener back_toucher = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        drag_animator.play();
                        back.setOnTouchListener(null);
                        smaller.setOnTouchListener(top_toucher);
                    }break;
                }
                return false;
            }
        };

        top_toucher = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        drag_animator.reverse();
                        smaller.setOnTouchListener(null);
                        back.setOnTouchListener(back_toucher);
                    }break;
                }
                return false;
            }
        };

        back.setOnTouchListener(back_toucher);

        smaller.setOnTouchListener(top_toucher);

        drag_test.post(new Runnable() {

            private int top;

            @Override
            public void run() {

                smaller_height = smaller.getHeight();
                offset = height - smaller_height;
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) drag_test.getLayoutParams();
                params.topMargin = offset;
                drag_test.requestLayout();

                drag_animator = new Animator(null, new IntergerEvaluator(0, offset), new FloatEvaluator(1F, 0.5F, 0.0F, 0F), new FloatEvaluator(0F, 1F)) {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onUpdate(Object[] animation_value) {
                        int value_x = (Integer) animation_value[0];
                        float value_1 = (Float) animation_value[1];
                        float value_2 = (Float) animation_value[2];
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) drag_test.getLayoutParams();
                        params.topMargin = (int) value_x;
//                        drag_test.setLayoutParams(params);
                        drag_test.setTop((int) value_x);
                        play_box.setAlpha(value_1);
                        smaller.setAlpha(value_2);
                    }

                    @Override
                    public void onEnd() {
                        drag_test.requestLayout();
//                        top = 0;
//                        Utils.debug("before-top: " + top + " after-top: " + drag_test.getTop() + " offset: " + offset);
                    }
                };
                drag_animator.setDuration(300);

            }
        });


    }

    private Animator grid_animator;
    private Animator drag_animator;
    private Animator drooper;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {

            {

            }
            @Override
            public void run() {
                Utils.debug("height: " + grid.getHeight());
                debugger.setText("" + grid.getHeight());
                grid_animator = new Animator(new AnticipateOvershootInterpolator(), new IntergerEvaluator(grid.getHeight(), 0)) {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onUpdate(Object[] animation_value) {
                        ViewGroup.LayoutParams params = grid.getLayoutParams();
                        params.height = (int) animation_value[0];
                        grid.setLayoutParams(params);
                    }

                    @Override
                    public void onEnd() {

                    }
                };

                grid_animator.setDuration(5000);

            }
        }, 1000);

        debugger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid_animator.play();
            }
        });
    }

    @SuppressLint("Range")
    private ArrayList<Audio> getTracks(Context context) {
        ArrayList<Audio> tracks = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                String artist     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String title      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String path       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                Audio audio = new Audio(artist, title, path);
                audio.setDate(date_added);
                tracks.add(audio);
            }
            cursor.close();
            Collections.sort(tracks, new Comparator<Audio>() {

                @Override
                public int compare(Audio track_1, Audio track_2) {
                    return track_1.getTitle().compareTo(track_2.getTitle());
                }
            });

            return tracks;
        } else {
            return tracks;
        }
    }

    public static boolean hasWriteStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestWriteStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


