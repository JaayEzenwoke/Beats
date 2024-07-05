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

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;

import com.jaay.beats.R;
import com.jaay.beats.important.Animator;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.Seekbar;
import com.jaay.beats.uiviews.Slate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestActivity extends AppCompatActivity {

    private MediaPlayer player;
    private Seekbar seekbar;
    private Slate tester;
    private int addon = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tester = findViewById(R.id.tester);
        seekbar = findViewById(R.id.test_seekbar);
        tester.setOnClickListener(new View.OnClickListener() {
            Animator animator;
            {
                animator = new Animator( 0, 200, 800) {
                    @Override
                    public void onStart() {
                        tester.setBackgroundColor(0xFF00FF00);
                    }

                    @Override
                    public void onUpdate(float animation_value) {
                        Utils.debug("animation value: " + animation_value);
                        tester.setTranslationX(animation_value);
                        tester.setBackgroundColor(0xFFFF0000);
                    }

                    @Override
                    public void onEnd() {
                        tester.setBackgroundColor(0xFF00FFFF);
                    }
                };
            }
            ArrayList<Audio> tracks = getTracks(TestActivity.this);
            @Override
            public void onClick(View view) {
                animator.start();
                try {
                    player.setDataSource(tracks.get(0).getPath());
//                    player.prepare();
//                    player.start();
                    int duration = player.getDuration() * 1000;
                    Utils.debug("duration: " + duration);
                    Utils.debug("duration: " + player.getDuration());
                    seekbar.setAdditions(180);
                    Utils.debug("here I am");
                } catch (IOException e) {
                    Utils.debug(e.getStackTrace());
                    throw new RuntimeException(e);
                }
            }
        });

        player = new MediaPlayer();
        seekbar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
}