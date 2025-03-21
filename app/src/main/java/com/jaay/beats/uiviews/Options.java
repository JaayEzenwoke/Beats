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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.tools.BlurUtils;
import com.jaay.beats.types.Audio;

import java.io.File;

public class Options extends Slate {

    public View add_favourite;
    public View set_ringtone;
    public Stack options;
    public View blocker;
    public View delete;

    public Options(@NonNull Context context) {
        super(context);
    }

    public Options(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.options;
        LayoutInflater.from(context).inflate(resource, this);

        add_favourite = findViewById(R.id.add_favourite);
        set_ringtone = findViewById(R.id.set_ringtone);
        options = findViewById(R.id.options_list);
        blocker = findViewById(R.id.blocker);
        delete = findViewById(R.id.delete);
    }

    public Options(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.options;
        LayoutInflater.from(context).inflate(resource, this);

        add_favourite = findViewById(R.id.add_favourite);
        set_ringtone = findViewById(R.id.set_ringtone);
        options = findViewById(R.id.options_list);
        blocker = findViewById(R.id.blocker);
        delete = findViewById(R.id.delete);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        BlurUtils.blurViewBackground(blocker, 10F, 0.5F);
        blocker.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                setVisibility(GONE);
                return true;
            }
        });

        options.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public boolean delete(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public void setRingtone(Context context, String path) {
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, "Custom Ringtone");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);

        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void addFavourite(Audio audio) {
        audio.setFavourite(true);
    }
}
