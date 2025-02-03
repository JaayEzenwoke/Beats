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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
//import androidx
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.important.Animator;
import com.jaay.beats.important.evaluators.FloatEvaluator;
import com.jaay.beats.important.evaluators.IntergerEvaluator;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.RollingText;
import com.jaay.beats.uiviews.Seekbar;
import com.jaay.beats.uiviews.Slate;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Image;

import java.io.IOException;
import java.util.ArrayList;

public class Playing extends Slate {

    public static class  NowPlaying extends Stack {
 
        private Image track_thumbnail;
        private MediaPlayer player;
        private Stack artist_title;
        private TextView title;
        private Seekbar seeker;
        private TextView song;
        private Image play;

        int duration;

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
            artist_title = findViewById(R.id.artist_title);
            seeker = findViewById(R.id.seeker);
            title = findViewById(R.id.title);
            song = findViewById(R.id.song);
            play = findViewById(R.id.play);
        }

        public void initialize() {
            seeker.setPlayer(player);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration = player.getDuration();
                    Utils.debug("duration: " + duration + " player: " + player);
                    duration = duration / 1000;
                    seeker.handler.removeCallbacks(seeker.seeker);
                    seeker.setAdditions(duration);
                }
            });

            play.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(player != null) {
                        if(player.isPlaying()) {
                            float current_position = (float) player.getCurrentPosition() / 1000;
                            current_position /= duration;
                            seeker.pause(current_position);
                            play.setImage(R.drawable.play);
                            player.pause();
                        }else {
                            seeker.setAdditions(duration);
                            play.setImage(R.drawable.pause);
                            player.start();
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

    private MediaPlayer player;
    public NowPlaying dropper;
    private RollingText title;
    public Seekbar seek_bar;
    public Image thumbnail;
    private Stack play_box;
    private TextView song;
    private Image next;
    private Image back;
    private Image prev;
    private Image play;

    private View.OnTouchListener top_toucher;
    private Animator animator;

    private int duration;
    private int offset;

    public Playing(Context context) {
        super(context);
    }

    public Playing(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.playing;
        LayoutInflater.from(context).inflate(resource, this);
        thumbnail = findViewById(R.id.thumbnail);
        play_box = findViewById(R.id.play_box);
        seek_bar = findViewById(R.id.seek_bar);
        dropper = findViewById(R.id.dropper);
        title = findViewById(R.id.title);
        song = findViewById(R.id.song);
        play = findViewById(R.id.play);
        back = findViewById(R.id.back);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        back = findViewById(R.id.back);
    }

    public Playing(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.playing;
        LayoutInflater.from(context).inflate(resource, this);

        seek_bar = findViewById(R.id.seek_bar);
        title = findViewById(R.id.title);
        song = findViewById(R.id.song);
        play = findViewById(R.id.play);

        thumbnail = findViewById(R.id.thumbnail);
        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
    }

    public void initialize(ArrayList<Audio> tracks, int position) {
        seek_bar.setPlayer(player);
        duration = player.getDuration();
        duration = duration / 1000;
        seek_bar.handler.removeCallbacks(seek_bar.seeker);
        Utils.debug("duration: " + duration);
        seek_bar.setAdditions(duration);
        title.startScrolling();

        // Optional: Control the scrolling speed
        title.setScrollDuration(8000);
        title.setScrollDelay(2000);
        title.setScrollDirection(false);

        play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(player != null) {
                    if(player.isPlaying()) {
                        float current_position = (float) player.getCurrentPosition() / 1000;
                        current_position /= duration;
                        seek_bar.pause(current_position);
                        play.setImage(R.drawable.play);
                        player.pause();
                        title.pauseScrolling();
                    }else {
                        seek_bar.setAdditions(duration);
                        play.setImage(R.drawable.pause);
                        player.start();
                        title.resumeScrolling();
                    }
                }
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        View.OnTouchListener back_toucher = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        animator.play();
                        back.setOnTouchListener(null);
                        dropper.setOnTouchListener(top_toucher);
                    }break;
                }
                return true;
            }
        };

        top_toucher = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        animator.reverse();
                        dropper.setOnTouchListener(null);
                        back.setOnTouchListener(back_toucher);
                    }break;
                }
                return true;
            }
        };

        back.setOnTouchListener(back_toucher);
        dropper.setOnTouchListener(top_toucher);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return true;
            }
        });
    }

    public void post(int deficit, View tab) {
        post(new Runnable() {

            @Override
            public void run() {
                int height = getHeight();
                offset = height - dropper.getHeight() - deficit;
                MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
                params.topMargin = offset;
                setLayoutParams(params);

                animator = new Animator(null, new IntergerEvaluator(0, offset), new FloatEvaluator(1F, 0.5F, 0.0F, 0F), new FloatEvaluator(0F, 1F), new IntergerEvaluator(deficit, deficit/2,  0)) {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onUpdate(Object[] animation_value) {
                        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
                        params.topMargin = (int) animation_value[0];
                        requestLayout();
                        play_box.setAlpha((float) animation_value[1]);
                        dropper.setAlpha((float) animation_value[2]);
                        tab.setTranslationY((int) animation_value[3]);
                    }

                    @Override
                    public void onEnd() {
                        Utils.debug("mix: " + Integer.toHexString(Utils.mix(Color.BLACK, 0xFF704214, 0.5F)).toUpperCase());
                    }
                };
                animator.setDuration(300);
            }
        });

    }

    public int getOffset() {
        return offset;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
