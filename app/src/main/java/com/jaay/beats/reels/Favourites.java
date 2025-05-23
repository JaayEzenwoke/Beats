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
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaay.beats.R;
import com.jaay.beats.types.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Favourites extends RecyclerView {

    public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private LayoutInflater inflater;
        private ArrayList<Audio> tracks;
        private Listener listener;


        public interface Listener {
            void onClick(View view, int position);
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView title;
            public View separator;
            public TextView song;

            public Holder(View view) {
                super(view);

                separator = view.findViewById(R.id.separator);
                title = view.findViewById(R.id.title);
                song = view.findViewById(R.id.song);

                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(view, getAdapterPosition());
            }
        }

        public Adapter(Context context, ArrayList<Audio> tracks) {
            this.tracks = tracks;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View view = inflater.inflate(R.layout.song, parent, false);
            return new Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.Holder holder, int position) {
            holder.title.setText(tracks.get(position).getTitle());
            holder.song.setText(tracks.get(position).getArtist());

            if (position == tracks.size()) {
                holder.separator.setVisibility(INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }

        public Audio getItem(int position) {
            return tracks.get(position);
        }

        void setClickListener(Adapter.Listener listener) {
            this.listener = listener;
        }
    }

    public ArrayList<Audio> tracks;
    private MediaPlayer player;
    private Adapter adapter;

    public Favourites(@NonNull Context context) {
        super(context);
    }

    public Favourites(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
    }

    public Favourites(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
    }

    {
        tracks = new ArrayList<>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (tracks.size() != 0) {
            adapter.setClickListener(new Adapter.Listener() {

                @Override
                public void onClick(View view, int position) {
                    playAudio(adapter.getItem(position));
                }
            });
        }
    }

    public void initialize(Context context) {
        setLayoutManager(new LinearLayoutManager(context));
        adapter = new Adapter(context, tracks);
        setAdapter(adapter);
    }

    private void playAudio(Audio audio) {
        if (player.isPlaying()) {
            player.stop();
            player.reset();
        }

        try {
            player.setDataSource(audio.getPath());
            player.prepare();
            player.start();
        } catch (IOException exception) {
        }
    }

    public ArrayList<Audio> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Audio> tracks) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).isFavourite()) {
                this.tracks.add(tracks.get(i));
            }
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
