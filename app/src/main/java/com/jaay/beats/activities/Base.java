/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaay.beats.R;
import com.jaay.beats.concurrency.Runner;
import com.jaay.beats.core.Beat;
import com.jaay.beats.reels.AllPlaylists;
import com.jaay.beats.reels.Favourites;
import com.jaay.beats.reels.Playing;
import com.jaay.beats.reels.Search;
import com.jaay.beats.reels.Songs;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.Image;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Options;

import java.util.ArrayList;

public class Base extends Beats {

    private Stack favourites_button;
    private Stack playlists_button;
    private AllPlaylists playlists;
    private Favourites favourites;
    private Stack search_button;
    private MediaPlayer player;
    private LinearLayout tab;
    private Search searches;
    private Options options;
    private Playing playing;
    private Runner runner;
    private View content;
    private Songs songs;
    private Stack home;

    private Image favorites_icon;
    private Image playlists_icon;
    private Image search_icon;
    private Image home_icon;

    private TextView favorites_text;
    private TextView playlists_text;
    private TextView search_text;
    private TextView home_text;


    private AudioManager manager;


    private final AudioManager.OnAudioFocusChangeListener audio_listener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Another app has taken audio focus permanently (e.g., another music player)
                    songs.stopMediaPlayer(manager, this);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Temporarily lost audio focus (e.g., incoming call)
                    songs.pauseMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lower volume if another app (like a notification) is playing audio
                    if (player != null) {
                        player.setVolume(0.3f, 0.3f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Regained audio focus, resume playback
                    songs.resumeMediaPlayer();
                    break;
            }
        }
    };

    public void setUp(MediaPlayer player) {
        playing.setSongs(songs);
        songs.tracks = Utils.getTracks(this);
        songs.initialize(Base.this);

        Songs.Adapter.Listener listener = new Songs.Adapter.Listener() {

            @Override
            public void onClick(View view, int position) {
                if(position < 0) return;
                songs.setPlaying(playing);
                songs.adapter.setSelectedPosition(position);
                ArrayList<Audio> tracks = Utils.getTracks(Base.this);
                playing.initialize(position, tracks);
            }

            @Override
            public void onLongClick(View view, int position) {
                options.setVisibility(View.VISIBLE);
            }

        };
        Search.SearchList.Adapter.Listener search_listner = new Search.SearchList.Adapter.Listener() {

            @Override
            public void onClick(View view, int position) {
                if(position < 0) return;
                searches.searchlist.adapter.setSelectedPosition(position);
                searches.setPlaying(playing);

                ArrayList<Audio> tracks = searches.getSearchedTracks();
                playing.initialize(position, tracks);

            }
        };

        playing.setHandler(new Handler(Looper.getMainLooper()));
        songs.setNow_playing(playing.dropper);

        searches.setTracks(Utils.getTracks(Base.this));
        searches.initialize(Base.this);
        playlists.getAdd_songs().addlist.setTracks(Utils.getTracks(Base.this));
        playlists.getAdd_songs().addlist.initialize(Base.this);
        playlists.setOptions(options);

        favourites.setTracks(songs.tracks);
        playlists.getPlaylists().playlists = new ArrayList<>();
        playlists.getPlaylists().initialize(Base.this);
        playlists.getPlaylists().playlists.add(songs.recentlyAdded(Base.this));
        playlists.getPlaylist_tracks().contents.setPlayer(player);
        favourites.initialize(Base.this);

        songs.adapter.setClickListener(listener);
        searches.searchlist.adapter.setClickListener(search_listner);
    }

    public void deSelect(View current) {
        if(playing.is_playing) {

        }
    }
    @Override
    protected void getInitialization() {
        player = new MediaPlayer();
        Intent intent = getIntent();
        runner = (Runner) intent.getSerializableExtra("runner");

    }

    @Override
    protected void getIdentification() {
        super.getIdentification();

        favourites_button = findViewById(R.id.favourites_button);
        playlists_button  = findViewById(R.id.playlists_button);
        content           = findViewById(android.R.id.content);
        search_button     = findViewById(R.id.search_button);
        favourites        = findViewById(R.id.favourites);
        playlists         = findViewById(R.id.playlists);
        searches          = findViewById(R.id.searches);
        options           = findViewById(R.id.options);
        playing           = findViewById(R.id.playing);
        songs             = findViewById(R.id.songs);
        home              = findViewById(R.id.home);
        tab               = findViewById(R.id.tab);

        favorites_icon    = findViewById(R.id.favorites_icon);
        playlists_icon    = findViewById(R.id.playlists_icon);
        search_icon       = findViewById(R.id.search_icon);
        home_icon         = findViewById(R.id.home_icon);

        favorites_text    = findViewById(R.id.favorites_text);
        playlists_text    = findViewById(R.id.playlists_text);
        search_text       = findViewById(R.id.search_text);
        home_text         = findViewById(R.id.home_text);

    }

    @Override
    protected void getAdjustments() {
        super.getAdjustments();
        tab.post(new Runnable() {
            @Override
            public void run() {
                Utils.debug("tab.post: ");
                playing.post(tab.getHeight(), tab);
            }
        });
    }

    @Override
    protected void getActionAttachments() {
        super.getActionAttachments();

        songs.setActivity(Base.this);
        playing.setHandler(new Handler(Looper.getMainLooper()));
        playing.dropper.setPlayer(player);
        playing.setPlayer(player);
        searches.searchlist.setPlayer(player);
        favourites.setPlayer(player);
        songs.setPlayer(player);

        ViewTreeObserver.OnGlobalLayoutListener observer = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Rect rect = new Rect();
                content.getWindowVisibleDisplayFrame(rect);
                int screenHeight = content.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    tab.setVisibility(View.GONE);
                } else {
                    tab.setVisibility(View.VISIBLE);
                }
            }
        };

        content.getViewTreeObserver().addOnGlobalLayoutListener(observer);

        int grey = getResources().getColor(R.color.grey1);
        int app_color = getResources().getColor(R.color.beat_color);

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                songs.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                playlists.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                playing.setTop(playing.getOffset());

                favorites_text.setTextColor(grey);
                playlists_text.setTextColor(grey);
                search_text.setTextColor(grey);
                home_text.setTextColor(app_color);

                favorites_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                playlists_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                search_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                home_icon.setColorFilter(new PorterDuffColorFilter(app_color, PorterDuff.Mode.SRC_IN));

                Utils.debug("home_button: ");

            }

        });

        search_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                searches.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                playlists.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
                playing.setTop(playing.getOffset());

                favorites_text.setTextColor(grey);
                playlists_text.setTextColor(grey);
                home_text.setTextColor(grey);
                search_text.setTextColor(app_color);

                favorites_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                playlists_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                home_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                search_icon.setColorFilter(new PorterDuffColorFilter(app_color, PorterDuff.Mode.SRC_IN));

                Utils.debug("search_button: ");
            }

        });

        favourites_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                favourites.setVisibility(View.VISIBLE);
                playlists.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
                playing.setTop(playing.getOffset());
                Utils.debug("favourites_button: ");

                playlists_text.setTextColor(grey);
                search_text.setTextColor(grey);
                home_text.setTextColor(grey);
                favorites_text.setTextColor(app_color);

                playlists_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                search_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                home_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                favorites_icon.setColorFilter(new PorterDuffColorFilter(app_color, PorterDuff.Mode.SRC_IN));


            }

        });

        playlists_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                playlists.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
                playing.setTop(playing.getOffset());
                Utils.debug("playlists_button: ");

                favorites_text.setTextColor(grey);
                search_text.setTextColor(grey);
                home_text.setTextColor(grey);
                playlists_text.setTextColor(app_color);

                favorites_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                search_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                home_icon.setColorFilter(new PorterDuffColorFilter(grey, PorterDuff.Mode.SRC_IN));
                playlists_icon.setColorFilter(new PorterDuffColorFilter(app_color, PorterDuff.Mode.SRC_IN));

            }

        });

        if (Utils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                Utils.checkPermission(this, Manifest.permission.READ_MEDIA_AUDIO)) {
            setUp(player);
        } else {
            Utils.requestPermission(this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        manager = (AudioManager)  this.getSystemService(Context.AUDIO_SERVICE);
        int result = manager.requestAudioFocus(audio_listener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if ((player.isPlaying()) && (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)) {
            player.start();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer player) {
                playing.stopUpdatingTime();
                switch (songs.mode) {
                    case Songs.Mode.repeat_current: {
                        playing.repeatCurrent();
                    }break;
                    case Songs.Mode.repeat_all: {
                        playing.repeatAll();
                    }break;
                    case Songs.Mode.shuffle: {
                        playing.shuffle();
                    }break;
                    case Songs.Mode.all: {
                        playing.playNext();
                    }break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getInitialization();
        getIdentification();
        getAdjustments();
        getActionAttachments();
    }
}