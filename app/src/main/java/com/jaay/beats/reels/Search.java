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

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jaay.beats.uiviews.Edit;
import com.jaay.beats.uiviews.Slate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Search extends Slate {

    public static class SearchList extends RecyclerView {

        public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

            private LayoutInflater inflater;
            private ArrayList<Audio> tracks;
            private Listener listener;

            public interface Listener {
                void onClick(View view, int position);
            }

            public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
                public View separator;
                public TextView title;
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
                return new Holder(view);
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

            public void setClickListener(Adapter.Listener listener) {
                this.listener = listener;
            }

            public void setTracks(ArrayList<Audio> tracks) {
                this.tracks = tracks;
                notifyDataSetChanged();
            }
        }

        private ArrayList<Audio> tracks;
        private MediaPlayer player;
        private Adapter adapter;

        public SearchList(@NonNull Context context) {
            super(context);
        }

        public SearchList(@NonNull Context context, @Nullable AttributeSet attributes) {
            super(context, attributes);
        }

        public SearchList(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();


        }

        public void initialize(Context context) {
            tracks = new ArrayList<>();
            setLayoutManager(new LinearLayoutManager(context));
            adapter = new SearchList.Adapter(context, tracks);
            adapter.setClickListener(new Adapter.Listener() {

                @Override
                public void onClick(View view, int position) {
                    playAudio(adapter.getItem(position));
                }

            });

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

        public MediaPlayer getPlayer() {
            return player;
        }

        public void setPlayer(MediaPlayer player) {
            this.player = player;
        }

    }

    private ArrayList<Audio> audios;
    private ArrayList<Audio> tracks;
    public SearchList searchlist;
    private Edit searchbar;


    {
//        searchbar;
    }
    public Search(@NonNull Context context) {
        super(context);
    }

    public Search(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.search;
        LayoutInflater.from(context).inflate(resource, this);

        searchlist = findViewById(R.id.searchlist);
        searchbar = findViewById(R.id.search_bar);
    }

    public Search(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.search;
        LayoutInflater.from(context).inflate(resource, this);

        searchlist = findViewById(R.id.searchlist);
        searchbar = findViewById(R.id.search_bar);
    }

    public void initialize(Context context, ArrayList<Audio> tracks) {

        searchlist.initialize(context);

        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence characters, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence characters, int start, int before, int count) {
                updateList(characters.toString(), tracks);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void updateList(String query, ArrayList<Audio> tracks) {
        if (query.isEmpty()) {
            tracks.clear();

            searchlist.setVisibility(GONE);
            searchlist.adapter.notifyDataSetChanged();
            tracks.addAll(audios);
        } else {
            searchlist.setVisibility(VISIBLE);
            Collections.sort(tracks, new Comparator<Audio>() {
                @Override
                public int compare(Audio track_1, Audio track_2) {
                    boolean found_t1 = track_1.getTitle().toLowerCase().contains(query.toLowerCase());
                    boolean found_t2 = track_2.getTitle().toLowerCase().contains(query.toLowerCase());

                    if (found_t1 && !found_t2) {
                        return -1;
                    } else if (!found_t1 && found_t2) {
                        return 1;
                    } else {
                        // If both or neither contains the search text, compare normally
                        return track_1.getTitle().compareToIgnoreCase(track_2.getTitle());
                    }
                }
            });

            searchlist.adapter.setTracks(tracks);
        }

    }

    public void setTracks(ArrayList<Audio> tracks) {
        audios = new ArrayList<>();
        audios.addAll(tracks);
    }

}
