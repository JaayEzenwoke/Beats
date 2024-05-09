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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.jaay.beats.R;
import com.jaay.beats.concurrency.Runner;
import com.jaay.beats.reels.AllPlaylists;
import com.jaay.beats.reels.Favourites;
import com.jaay.beats.reels.Playing;
import com.jaay.beats.reels.Search;
import com.jaay.beats.reels.Songs;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Options;

import java.util.ArrayList;

public class Base extends Beats {

    private Playing.NowPlaying now_playing;
    private Stack favourites_button;
    private Stack playlists_button;
    private AllPlaylists playlists;
    private Favourites favourites;
    private Stack search_button;
    private LinearLayout tab;
    private Search searches;
    private Options options;
    private Playing playing;
    private Runner runner;
    private View content;
    private Songs songs;
    private Stack home;

    int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getInitialization() {

        if(checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }else {
            requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

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
        now_playing       = findViewById(R.id.now_playing);
        favourites        = findViewById(R.id.favourites);
        playlists         = findViewById(R.id.playlists);
        searches          = findViewById(R.id.searches);
        options           = findViewById(R.id.options);
        playing           = findViewById(R.id.playing);
        songs             = findViewById(R.id.songs);
        home              = findViewById(R.id.home);
        tab               = findViewById(R.id.tab);

    }

    @Override
    protected void getActionAttachments() {
        super.getActionAttachments();

        MediaPlayer player = new MediaPlayer();

        songs.setActivity(this);
        now_playing.setPlayer(player);
        songs.setNow_playing(now_playing);
        songs.initialize(this);
        searches.initialize(Base.this, songs.tracks);
        playlists.getAdd_songs().addlist.setTracks(songs.tracks);
        playlists.getAdd_songs().addlist.initialize(this);
        playlists.setOptions(options);

        searches.searchlist.setPlayer(player);
        favourites.setPlayer(player);
        songs.setPlayer(player);

        searches.setTracks(songs.tracks);
        favourites.setTracks(songs.tracks);
        playlists.getPlaylists().playlists = new ArrayList<>();
        playlists.getPlaylists().initialize(this);
        playlists.getPlaylists().playlists.add(songs.recentlyAdded(Base.this));
        playlists.getPlaylist_tracks().contents.setPlayer(player);
        favourites.initialize(Base.this);

        content.getViewTreeObserver().addOnGlobalLayoutListener(

                new ViewTreeObserver.OnGlobalLayoutListener() {
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
                });

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                songs.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                playlists.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);

            }

        });
        search_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                searches.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                playlists.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
            }

        });
        favourites_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                favourites.setVisibility(View.VISIBLE);
                playlists.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);

            }

        });
        playlists_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                playlists.setVisibility(View.VISIBLE);
                favourites.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
            }

        });

        now_playing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                playing.setVisibility(View.VISIBLE);
                playlists.setVisibility(View.GONE);
                favourites.setVisibility(View.GONE);
                searches.setVisibility(View.GONE);
                songs.setVisibility(View.GONE);
                now_playing.setVisibility(View.GONE);
            }
        });
    }

    public  boolean checkPermission(Beats beats, String permission) {
        int result = ContextCompat.checkSelfPermission(beats, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    public  void requestPermission(Beats beats, String granted_permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(beats,  new String[]{granted_permission}, 101);

        }
    }

}