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

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
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
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.types.Playlist;

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

                // Store previous selection
                int previous_position = selected_position;

                // Update selected position
                selected_position = getAdapterPosition();

                // Notify changes to update UI
                notifyItemChanged(previous_position);
                notifyItemChanged(selected_position);
            }
        }

        public interface Listener {
            void onClick(View view, int position);
        }

        private LayoutInflater inflater;
        private ArrayList<Audio> tracks;
        private ArrayList<Audio> recent;
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

    private AudioManager audioManager;
    public ArrayList<Audio> tracks;
    private MediaPlayer player;
    private Activity activity;
    private Adapter adapter;
    private Audio previous;
    private Audio current;
    private Audio next;

    private int current_position;
    private int previous_position;
    private int mode;

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Another app has taken audio focus permanently (e.g., another music player)
                    stopMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Temporarily lost audio focus (e.g., incoming call)
                    pauseMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lower volume if another app (like a notification) is playing audio
                    if (player != null) {
                        player.setVolume(0.3f, 0.3f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Regained audio focus, resume playback
                    resumeMediaPlayer();
                    break;
            }
        }
    };

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

        adapter.setClickListener(new Adapter.Listener() {

            @Override
            public void onClick(View view, int position) {
                if(position < 0) return;

                setCurrentPosition(position);
                Audio audio = adapter.getItem(position);
                playAudio(audio);
                getPlaying().setVisibility(VISIBLE);
                getNow_playing().initialize();

                if(position > 0) {
                    setPrevious(adapter.getItem(position - 1));
                }

                setCurrent(audio);
                if (adapter.tracks.size() > position) {
                    setNext(adapter.getItem(position + 1));
                }

                getNow_playing().setImage(getContext(), getCurrent().getPath());
                getNow_playing().setTitle(audio.getTitle(), audio.getArtist());

                getPlaying().initialize(tracks, position);
                Utils.setImage(getContext(), getCurrent().getPath(), getPlaying().thumbnail);

                adapter.setSelectedPosition(position);


            }
        });
    }

    public void initialize(Context context) {
        tracks = getTracks(context);
        setLayoutManager(new LinearLayoutManager(context));
        adapter = new Adapter(context, tracks);
        setAdapter(adapter);
    }

    private Playing.NowPlaying now_playing;
    private Playing playing;

    @SuppressLint("Range")
    private ArrayList<Audio> getTracks(Context context) {
        ArrayList<Audio> tracks = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

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

        audioManager = (AudioManager)  getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.start();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer player) {
                switch (mode) {
                    case Mode.repeat_current: {
                        repeatCurrent();
                    }break;
                    case Mode.repeat_all: {
                        repeatAll();
                    }break;
                    case Mode.shuffle: {
                        shuffle();
                    }break;
                    case Mode.all: {
                        playNext();
                    }break;
                }
            }
        });
    }

    private void pauseMediaPlayer() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    private void releaseAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }

    private void resumeMediaPlayer() {
        if (player != null) {
            player.setVolume(1.0f, 1.0f); // Restore volume
            player.start();
        }
    }

    private void stopMediaPlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        releaseAudioFocus();
    }

    public void repeatCurrent() {
        int current = getCurrentPosition();
        Audio audio = adapter.getItem(current);
        playAudio(audio);
        getNow_playing().initialize();
        setCurrentPosition(current);
    }

    private void repeatAll() {
        int current = getCurrentPosition();
        current++;
        if (current < adapter.getItemCount()) {
            Audio audio = adapter.getItem(current);
            playAudio(audio);
            getNow_playing().initialize();
        } else {
            current = 0;
            playAudio(adapter.getItem(current));
        }
        setCurrentPosition(current);
    }

    private void shuffle() {
        int current = getCurrentPosition();
        int max = adapter.getItemCount();
        Audio audio = adapter.getItem(current);
        playAudio(audio);
        getNow_playing().initialize();
        setCurrentPosition(getRandom(0, max));
    }

    private void playNext() {
        int current = getCurrentPosition();
        current++;
        if (current < adapter.getItemCount()) {
            Audio audio = adapter.getItem(current);
            playAudio(audio);
            getNow_playing().initialize();
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

    public void setMode(int mode) {
        this.mode = mode;
    }
}
