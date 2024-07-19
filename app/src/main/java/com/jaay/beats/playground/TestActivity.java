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

import static com.jaay.beats.tools.Utils.getExtension;
import static com.jaay.beats.tools.Utils.getTrimmed;
import static com.jaay.beats.tools.Utils.getUniqueFilename;

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
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.jaay.beats.R;
import com.jaay.beats.core.Beat;
import com.jaay.beats.important.Animator;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.Seekbar;
import com.jaay.beats.uiviews.Slate;
import com.jaay.beats.uiviews.Text;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private MediaPlayer player;
    private Seekbar seekbar;
    private Slate tester;

    private Text equalizer;
    private Text play;
    private Text pause;
    private Text cut;
    private int addon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tester = findViewById(R.id.tester);
        seekbar = findViewById(R.id.test_seekbar);
        equalizer = findViewById(R.id.equalizer);
        pause = findViewById(R.id.pause);
        play = findViewById(R.id.play);
        cut = findViewById(R.id.cut);

        Beat beat = new Beat(getApplicationContext());
        ArrayList<Audio> tracks = beat.getTracks();

        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }

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

            @Override
            public void onClick(View view) {
                animator.start();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                beat.play(tracks.get(22).getPath());
                Utils.debug("path: " + Utils.getUniqueFilename(tracks.get(22), tracks));
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                beat.pause();
            }
        });

        for (int i = 0; i < tracks.size(); i++) {
            Audio audio = tracks.get(i);
            Utils.debug("pathxx: " + audio.getPath());
        }
        cut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Audio audio = tracks.get(22);
//                Utils.debug("duration: " + beat.getDuration(audio.getPath()));
//                String input = getUniqueFilename(audio, tracks);
//                Utils.debug("title: " + beat.getDuration(audio.getPath()));
//                Utils.debug("title: " + audio.getPath() + " | extension " + getExtension(audio.getPath())  + " | trimmed " + getTrimmed(audio.getPath()));
                beat.cutAndSaveAudio(audio, 300, 2000);
                beat.resume();
            }
        });



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


