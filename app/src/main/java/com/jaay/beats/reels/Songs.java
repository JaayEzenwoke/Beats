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
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import com.jaay.beats.activities.Beats;
import com.jaay.beats.types.Audio;
import com.jaay.beats.types.Playlist;
import com.jaay.beats.uiviews.Dots;
import com.jaay.beats.uiviews.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Songs extends RecyclerView {

    public static class Mode {
        public static final int repeat_current = 0x8000;
        public static final int repeat_all = 0x0400;
        public static final int shuffle = 0x0020;
        public static final int all = 0x0001;
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        public class Holder extends RecyclerView.ViewHolder {

            public Dots options;
            public TextView title;
            public View separator;
            public TextView song;

            public Holder(View view) {
                super(view);

                separator = view.findViewById(R.id.separator);
                title = view.findViewById(R.id.title);
                song = view.findViewById(R.id.song);
                options = view.findViewById(R.id.options);

                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (listener != null) listener.onClick(view, getAdapterPosition());

                        // Store previous selection
                        int previous_position = selected_position;

                        // Update selected position
                        selected_position = getAdapterPosition();

                        // Notify changes to update UI
                        notifyItemChanged(previous_position);
                        notifyItemChanged(selected_position);
                    }
                });

                view.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        if (listener != null) listener.onLongClick(view, getAdapterPosition());
                        return true;
                    }
                });

            }
        }

        public interface Listener {
            void onClick(View view, int position);
            void onLongClick(View view, int position);
        }

        private LayoutInflater inflater;
        public ArrayList<Audio> tracks;
        public ArrayList<Audio> recent;
        private Listener listener;

        private int selected_position = RecyclerView.NO_POSITION;

        public Adapter(Context context, ArrayList<Audio> tracks) {
            this.tracks = tracks;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View view = inflater.inflate(R.layout.song, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.Holder holder, int position) {
            holder.title.setText(tracks.get(position).getTitle());
            holder.song.setText(tracks.get(position).getArtist());

            // Apply selection color logic
            if (position == selected_position) {
                holder.title.setTextColor(holder.itemView.getResources().getColor(R.color.beat_color));
                holder.song.setTextColor(holder.itemView.getResources().getColor(R.color.beat_color));
            } else {
                holder.title.setTextColor(holder.itemView.getResources().getColor(R.color.grey2));
                holder.song.setTextColor(holder.itemView.getResources().getColor(R.color.grey1));
            }


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

        public void setClickListener(Listener listener) {
            this.listener = listener;
        }

        public void setSelectedPosition(int position) {
            int previousSelected = selected_position;
            selected_position = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selected_position);
        }

        public ArrayList<Audio> getTracks() {
            return tracks;
        }
    }

    public static int mode;

    private Playing.NowPlaying now_playing;
    private Playing playing;

    public ArrayList<Audio> tracks;
    private MediaPlayer player;
    private Activity activity;
    public Adapter adapter;
    private Audio previous;
    private Audio current;
    private Audio next;

    private int current_position;
    private int previous_position;
    
    boolean is_playing;

    public Songs(@NonNull Context context) {
        super(context);
    }

    public Songs(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
    }

    public Songs(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);
    }

    {
        setMode(Mode.repeat_current);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void initialize(Beats context) {
        setLayoutManager(new LinearLayoutManager(context));
        adapter = new Adapter(context, tracks);
        setAdapter(adapter);
    }

    public Playlist recentlyAdded(Context context) {
        ArrayList<Audio> recent = tracks;

        Collections.sort(recent, new Comparator<Audio>() {

            @Override
            public int compare(Audio track_1, Audio track_2) {
                long date_1 = Long.parseLong(track_1.getDate());
                long date_2 = Long.parseLong(track_2.getDate());
                return Long.compare(date_1, date_2);
            }
        });

        Playlist latest = new Playlist("Recently Addded");
        latest.setTracks(recent);
        latest.setSongCount(recent.size());

        return latest;
    }

    public void playAudio(Audio audio) {
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

    public void pauseMediaPlayer() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void releaseAudioFocus(AudioManager manager, AudioManager.OnAudioFocusChangeListener listener) {
        if (manager != null) {
            manager.abandonAudioFocus(listener);
        }
    }

    public void resumeMediaPlayer() {
        if (!is_playing) return;
        if (player != null) {
            player.setVolume(1.0f, 1.0f); // Restore volume
            player.start();
        }
    }

    public void stopMediaPlayer(AudioManager manager, AudioManager.OnAudioFocusChangeListener listener) {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        releaseAudioFocus(manager, listener);
    }

    public void playPrev() {
        int current = getCurrentPosition();
        current--;
        if (current > -1) {
            getPlaying().initialize(current, tracks);
        } else {
            player.reset();
        }
        setCurrentPosition(current);
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public Playing.NowPlaying getNow_playing() {
        return now_playing;
    }

    public void setNow_playing(Playing.NowPlaying now_playing) {
        this.now_playing = now_playing;
    }

    public void setPlaying(Playing playing) {
        this.playing = playing;
    }

    public Playing getPlaying() {
        return playing;
    }

    public void setPrevious(Audio previous) {
        this.previous = previous;
    }

    public void setCurrent(Audio current) {
        this.current = current;
    }

    public void setNext(Audio next) {
        this.next = next;
    }

    public Audio getPrevious() {
        return previous;
    }

    public Audio getCurrent() {
        return current;
    }

    public Audio getNext() {
        return next;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
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

    public static void setMode(int set_mode) {
        mode = set_mode;
    }

    public static int getMode() {
        return mode;
    }
}