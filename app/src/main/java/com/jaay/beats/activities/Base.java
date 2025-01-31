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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaay.beats.R;
import com.jaay.beats.concurrency.Runner;
import com.jaay.beats.reels.AllPlaylists;
import com.jaay.beats.reels.Favourites;
import com.jaay.beats.reels.Playing;
import com.jaay.beats.reels.Search;
import com.jaay.beats.reels.Songs;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.uiviews.Image;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Options;
import com.jaay.beats.uiviews.Text;

import java.util.ArrayList;

public class Base extends Beats {

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

    private Image favorites_icon;
    private Image playlists_icon;
    private Image search_icon;
    private Image home_icon;

    private TextView favorites_text;
    private TextView playlists_text;
    private TextView search_text;
    private TextView home_text;

    int margin;



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
    }

    @Override
    protected void getActionAttachments() {
        super.getActionAttachments();

        MediaPlayer player = new MediaPlayer();

        songs.setActivity(this);
        playing.dropper.setPlayer(player);
        playing.setPlayer(player);
        songs.setNow_playing(playing.dropper);
        songs.setPlaying(playing);
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        tab.post(new Runnable() {
            @Override
            public void run() {
                Utils.debug("tab.post: ");
                playing.post(tab.getHeight(), tab);
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