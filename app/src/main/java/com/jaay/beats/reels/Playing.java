/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.reels;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Image;

import java.io.IOException;

public class Playing extends Stack {

    public static class  NowPlaying extends Stack {

        private Image track_thumbnail;
        private MediaPlayer player;
        private SeekBar seeker;
        private TextView title;
        private TextView song;
        private Image play;
        public NowPlaying(Context context) {
            super(context);
        }

        public NowPlaying(Context context, @Nullable AttributeSet attributes) {
            super(context, attributes);

            int resource = R.layout.now_playing;
            LayoutInflater.from(context).inflate(resource, this);

            track_thumbnail = findViewById(R.id.track_thumbnail);
            seeker = findViewById(R.id.seeker);
            title = findViewById(R.id.title);
            song = findViewById(R.id.song);
            play = findViewById(R.id.play);
        }

        public NowPlaying(Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);

            int resource = R.layout.now_playing;
            LayoutInflater.from(context).inflate(resource, this);

            track_thumbnail = findViewById(R.id.track_thumbnail);
            seeker = findViewById(R.id.seeker);
            title = findViewById(R.id.title);
            song = findViewById(R.id.song);
            play = findViewById(R.id.play);
        }

        {

        }

        public void initialize() {
            int duration = player.getDuration();
            seeker.setMax(duration / 1000);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    int current_position = player.getCurrentPosition() / 1000;
                    seeker.setProgress(current_position);
                    getHandler().postDelayed(this, 100);
                    if(!player.isPlaying()) {
                        getHandler().removeCallbacks(this);
                    }
                }
            };

            getHandler().post(runnable);

            seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seeker, int progress, boolean fromUser) {
                    if(player != null && fromUser){
                        player.seekTo(progress * 1000);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seeker) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seeker) {

                }
            });

            play.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(player != null) {
                        if(player.isPlaying()) {
                            int current_position = player.getCurrentPosition() / 1000;
                            seeker.setProgress(current_position);
                            play.setImage(R.drawable.play);
                            player.pause();
                            getHandler().removeCallbacks(runnable);
                        }else {
                            play.setImage(R.drawable.pause);
                            player.start();
                            getHandler().post(runnable);
                        }
                    }
                }
            });

        }

        private String formatTime(int milliseconds) {
            int seconds = milliseconds / 1000;
            int minutes = seconds / 60;
            int hours = minutes / 60;

            // Format time string with hours, minutes, and seconds
            String timeString = "";
            if (hours > 0) {
                timeString += String.format("%02d:", hours);
            }
            timeString += String.format("%02d:", minutes % 60);
            timeString += String.format("%02d", seconds % 60);

            return timeString;
        }

        public MediaPlayer getPlayer() {
            return player;
        }

        public void setPlayer(MediaPlayer player) {
            this.player = player;
        }

        public void setImage (Context context, String path) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(path));

            byte[] artwork = retriever.getEmbeddedPicture();
            if (artwork != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
                track_thumbnail.setImageBitmap(bitmap);
            }

            try {
                retriever.release();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        public void setTitle(String title, String song) {
            this.title.setText(title);
            this.song.setText(song);
        }
    }

    private Image track_thumbnail;
    private MediaPlayer player;
    private SeekBar seekbar;
    private TextView title;
    private TextView song;
    private Image play;


    public Playing(Context context) {
        super(context);
    }

    public Playing(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.playing;
        LayoutInflater.from(context).inflate(resource, this);

        seekbar = findViewById(R.id.seek_bar);
        title = findViewById(R.id.title);
        song = findViewById(R.id.song);
        play = findViewById(R.id.play);
    }

    public Playing(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.playing;
        LayoutInflater.from(context).inflate(resource, this);

        seekbar = findViewById(R.id.seek_bar);
        title = findViewById(R.id.title);
        song = findViewById(R.id.song);
        play = findViewById(R.id.play);

    }

    public void initialize(Activity activity) {
        int duration = player.getDuration();
        seekbar.setMax(duration / 1000);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                int current_position = player.getCurrentPosition() / 1000;
                seekbar.setProgress(current_position);
                getHandler().postDelayed(this, 100);
            }
        };

        getHandler().post(runnable);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seeker, int progress, boolean fromUser) {
                if(player != null && fromUser){
                    player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seeker) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seeker) {
                if (player != null && player.isPlaying()) {
                    player.seekTo(seeker.getProgress());
                }
            }
        });

    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

}
