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

import static com.jaay.beats.reels.Songs.*;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
//import androidx
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jaay.beats.R;
import com.jaay.beats.activities.Beats;
import com.jaay.beats.concurrency.MediaPlayerService;
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
import com.jaay.beats.uiviews.Text;

import java.io.IOException;
import java.util.ArrayList;

public class Playing extends Slate {

    public static class  NowPlaying extends Stack {

        public Image track_thumbnail;
        public MediaPlayer player;
        public Stack artist_title;
        public TextView title;
        public Seekbar seeker;
        public TextView song;
        public Image play;

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

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            play.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(player != null) {
                        Playing playing = (Playing) getParent().getParent();
                        if(player.isPlaying()) {
                            float current_position = (float) player.getCurrentPosition() / 1000;
                            current_position /= duration;
                            seeker.pause(current_position);
                            playing.seek_bar.pause(current_position);
                            playing.play.setImage(R.drawable.play);
                            play.setImage(R.drawable.play);
                            player.pause();
                        }else {
                            seeker.setAdditions(duration);
                            playing.seek_bar.setAdditions(duration);
                            playing.play.setImage(R.drawable.pause);
                            play.setImage(R.drawable.pause);
                            player.start();
                        }
                    }
                }
            });
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
            timeString += String.format("%02d:%02d", minutes % 60, seconds % 60);

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

    private MediaPlayerService mediaPlayerService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
            mediaPlayerService = binder.getService();
            isBound = true;

            // Update the service with current track info
//            if (playlist != null && !playlist.isEmpty()) {
//                updateNotificationInfo();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public View.OnTouchListener top_toucher;
    private ArrayList<Audio> favorites;
    private ArrayList<Audio> tracks;
    public MediaPlayer player;
    private Runnable runnable;
    public NowPlaying dropper;
    public RollingText title;
    public Animator animator;
    private Handler handler;
    public Seekbar seek_bar;
    public Image thumbnail;
    public Stack play_box;
    public TextView artist;
    public Text play_time;
    public Text end_time;
    public Image shuffle;
    public Image repeat;
    private Songs songs;
    public Image like;
    public Image next;
    public Image back;
    public Image prev;
    public Image play;

    private Audio previous_audio;
    private Audio current_audio;
    private Audio next_audio;

    private int current_position;
    private int previous_position;

    public int duration;
    public int offset;

    public boolean is_playing;

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
        artist = findViewById(R.id.artist);

        play_time = findViewById(R.id.play_time);
        end_time = findViewById(R.id.end_time);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        like = findViewById(R.id.like);
        play = findViewById(R.id.play);
        back = findViewById(R.id.back);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);

        handler = new Handler(); // Initialize handler here
    }

    public Playing(Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
    }

    @SuppressLint("SetTextI18n")
    public void initialize(int position, ArrayList<Audio> tracks) {
        this.tracks = tracks;

        seek_bar.additions = 0;
        dropper.seeker.additions = 0;

        setCurrentPosition(position);
        Audio audio = tracks.get(position);

        play(audio);

        setVisibility(VISIBLE);
        dropper.initialize();

        if(position > 0) {
            setPreviousAudio(tracks.get(position - 1));
        }

        setCurrentAudio(audio);
        if (tracks.size() > position) {
            setNextAudio(tracks.get(position + 1));
        }

        dropper.setTitle(audio.getTitle(), audio.getArtist());

        setTitle(audio.getTitle(), audio.getArtist());

        play.setImage(R.drawable.pause);
        dropper.play.setImage(R.drawable.pause);

        if (Utils.getImage(getContext(), getCurrentAudio().getPath()) == null) {
            dropper.track_thumbnail.setImage(R.drawable.disc_icon);
            thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnail.setImage(R.drawable.disc_icon);
        }else {
            Utils.setImage(getContext(), getCurrentAudio().getPath(), thumbnail);
            dropper.setImage(getContext(), getCurrentAudio().getPath());
        }

        is_playing = true;

        seek_bar.setPlayer(player);
        duration = player.getDuration();
        Utils.debug(" duration: " + duration);

        // Convert duration to seconds for the seekbar
        float duration_in_seconds = duration / 1000F;
        seek_bar.handler.removeCallbacks(seek_bar.seeker);

        // Format end time properly
        end_time.setText(Utils.formatTime(duration));

        // Update the play time to 0:00 initially
        play_time.setText("0:00");

        // Start updating the time display
        updatePlayTime();
        startUpdatingTime();

        seek_bar.setAdditions(duration_in_seconds);
        dropper.seeker.setAdditions(duration_in_seconds);

        title.startScrolling();

        // Optional: Control the scrolling speed
        title.setScrollDuration(8000);
        title.setScrollDelay(2000);
        title.setScrollDirection(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updatePlayTime();
        setRepeat();

        play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(player != null) {
                    if(player.isPlaying()) {
                        float current_position = (float) player.getCurrentPosition() / 1000;
                        current_position /= duration / 1000f; // Convert duration to seconds first

                        seek_bar.pause(current_position);
                        dropper.seeker.pause(current_position);

                        play.setImage(R.drawable.play);
                        dropper.play.setImage(R.drawable.play);

                        player.pause();

                        title.pauseScrolling();
                        stopUpdatingTime();
                    }else {
                        seek_bar.setAdditions(duration / 1000f); // Convert duration to seconds
                        dropper.seeker.setAdditions(duration / 1000f);

                        play.setImage(R.drawable.pause);
                        dropper.play.setImage(R.drawable.pause);

                        player.start();

                        title.resumeScrolling();
                        startUpdatingTime();
                    }
                }
            }
        });

        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                player.stop();
                if(songs.getCurrentPosition() < songs.tracks.size() && songs.getCurrentPosition() > -1) {
                    int current = songs.getCurrentPosition();
                    int max = songs.adapter.getItemCount();
                    int position = getRandom(0, max);
                    if(mode == Mode.shuffle) {
                        initialize(position, songs.tracks);
                    }else {
                        initialize(songs.getCurrentPosition() + 1, songs.tracks);
                    }
                }
            }
        });

        prev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                player.stop();
                if(songs.getCurrentPosition() < songs.tracks.size() && songs.getCurrentPosition() > 0) {
                    int current = songs.getCurrentPosition();
                    int max = songs.adapter.getItemCount();
                    int position = getRandom(0, max);
                    if(mode == Mode.shuffle) {
                        initialize(position, songs.tracks);
                    }else {
                        initialize(songs.getCurrentPosition() - 1, songs.tracks);
                    }
                }
            }
        });

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

        like.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(songs.getCurrent().isFavourite()) {
                    like.setImage(R.drawable.heart);
                    songs.getCurrent().setFavourite(false);
//                    favorites.remove(songs.getCurrentPosition());//Todo fix
                }else {
                    like.setImage(R.drawable.like);
                    songs.getCurrent().setFavourite(true);
//                    favorites.add(songs.getCurrent());//Todo fix
                }

            }
        });

        shuffle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Songs.setMode(Mode.shuffle);
                setRepeat();
            }
        });

        repeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                int mode = songs.getMode();

                if(mode == Mode.shuffle) {
                    Songs.setMode(Mode.all);
                } else if (mode == Mode.all) {
                    Songs.setMode(Mode.repeat_all);
                } else if (mode == Mode.repeat_all) {
                    Songs.setMode(Mode.repeat_current);
                }

                setRepeat();
            }
        });

    }

    public void releaseService(Beats beats) {
        beats.unregisterReceiver(mediaPlayerService.receiver);

        // Unbind from service
        if (isBound) {
            beats.unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void updateNotificationInfo() {
        if (isBound && mediaPlayerService != null) {

            Drawable drawable = thumbnail.getDrawable();

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null) {
                int drawableId = R.drawable.disc_icon;
                bitmap = BitmapFactory.decodeResource(getResources(), drawableId)
            }

            mediaPlayerService.updateMediaInfo(
                    getCurrentAudio().getTitle(),
                    getCurrentAudio().getArtist(),
                    bitmap,
                    is_playing
            );
        }
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

                Intent serviceIntent = new Intent(getContext(), MediaPlayerService.class);
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

    }

    public int getOffset() {
        return offset;
    }



    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void play(Audio audio) {
        if (player.isPlaying()) {
            player.stop();
            player.reset();
        }

        try {
            player.reset();
            player.setDataSource(audio.getPath());
            player.prepare();
            player.start();
        } catch (IOException exception) {

        }
    }


    public void setRepeat() {
        switch (Songs.getMode()) {
            case Mode.repeat_current: {
                repeat.setColorFilter(Color.BLACK);
                repeat.setImage(R.drawable.repeat_current);
                shuffle.setColorFilter(0x10000000);
            }break;
            case Mode.repeat_all: {
                repeat.setColorFilter(Color.BLACK);
                repeat.setImage(R.drawable.repeat);
                shuffle.setColorFilter(0x10000000);
            }break;
            case Mode.shuffle: {
                repeat.setImage(R.drawable.repeat);
                repeat.setColorFilter(0x10000000);
                shuffle.setColorFilter(Color.BLACK);
            }break;
            case Mode.all: {
                repeat.setImage(R.drawable.repeat);
                repeat.setColorFilter(0x10000000);
                shuffle.setColorFilter(R.color.grey1);
            }break;
        }
    }

    private void updatePlayTime() {
        Utils.debug(" updatePlayTime: ");
        runnable = new Runnable() {

            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int current_time = player.getCurrentPosition(); // Get current time in milliseconds

                    // Update the seekbar position
                    float progress = (float) current_time / duration;
                    int seekbar_position = (int) (progress * seek_bar.getWidth());
                    seek_bar.additions = seekbar_position;
                    dropper.seeker.additions = seekbar_position;

                    // Force redraw of seekbars
                    seek_bar.invalidate();
                    dropper.seeker.invalidate();

                    // Format and update the time display
                    play_time.setText(Utils.formatTime(current_time));

                    // Continue the loop
                    handler.postDelayed(this, 100); // Update more frequently for smoother movement
                }
            }
        };
    }

    public void startUpdatingTime() {
        Utils.debug("startUpdatingTime: ");
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable); // Remove any existing callbacks
            handler.post(runnable);
        }
    }

    public void stopUpdatingTime() {
        Utils.debug("stopUpdatingTime: ");
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }



    public void setSongs(Songs songs) {
        this.songs = songs;
    }

    public void setTitle(String title, String artist) {
        this.title.setText(title);
        this.artist.setText(artist);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public void repeatCurrent() {
        int current = getCurrentPosition();
        initialize(current, tracks);
        setCurrentPosition(current);
    }

    public void repeatAll() {
        int current = getCurrentPosition();
        current++;
        if (current < tracks.size()) {
            Audio audio = tracks.get(current);
            initialize(current, tracks);
        } else {
            current = 0;
            initialize(current, tracks);
        }
        setCurrentPosition(current);
    }

    public void shuffle() {
        int current = getCurrentPosition();
        int max = tracks.size();
        setCurrentPosition(getRandom(0, max));
        initialize(getRandom(0, max), tracks);
    }

    public void playNext() {
        int current = getCurrentPosition();
        current++;
        if (current < tracks.size()) {
            initialize(current, tracks);
        } else {
            player.reset();
        }
        setCurrentPosition(current);
    }



    public void setCurrentPosition(int current_position) {
        this.current_position = current_position;
    }

    public int getCurrentPosition() {
        return current_position;
    }

    public void setPreviousPosition(int previous_position) {
        this.previous_position = previous_position;
    }

    public int getPreviousPosition() {
        return previous_position;
    }

    public void setPreviousAudio(Audio previous_audio) {
        this.previous_audio = previous_audio;
    }

    public Audio getPreviousAudio() {
        return previous_audio;
    }

    public void setCurrentAudio(Audio current_audio) {
        this.current_audio = current_audio;
    }

    public Audio getCurrentAudio() {
        return current_audio;
    }

    public void setNextAudio(Audio next_audio) {
        this.next_audio = next_audio;
    }

    public Audio getNextAudio() {
        return next_audio;
    }
}