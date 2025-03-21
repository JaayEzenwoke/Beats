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
import com.jaay.beats.activities.Beats;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.uiviews.Edit;
import com.jaay.beats.uiviews.Image;
import com.jaay.beats.uiviews.Slate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Search extends Slate {

    public static class SearchList extends RecyclerView {

        public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

            public class Holder extends RecyclerView.ViewHolder {

                public TextView title;
                public View separator;
                public TextView song;

                public Holder(View view) {
                    super(view);

                    separator = view.findViewById(R.id.separator);
                    title = view.findViewById(R.id.title);
                    song = view.findViewById(R.id.song);

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
                }
            }

            public interface Listener {
                void onClick(View view, int position);
            }

            private LayoutInflater inflater;
            public ArrayList<Audio> tracks;
            public ArrayList<Audio> recent;
            private Adapter.Listener listener;

            private int selected_position = RecyclerView.NO_POSITION;

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

            public void setClickListener(Adapter.Listener listener) {
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

        private ArrayList<Audio> tracks;
        private MediaPlayer player;
        public Adapter adapter;

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

        public MediaPlayer getPlayer() {
            return player;
        }

        public void setPlayer(MediaPlayer player) {
            this.player = player;
        }

    }

    public ArrayList<Audio> searched_tracks;
    private ArrayList<Audio> audios;
    private ArrayList<Audio> tracks;

    private Playing.NowPlaying now_playing;
    private Playing playing;

    public SearchList searchlist;
    private Edit search_bar;
    private Image not_found;

    public Search(@NonNull Context context) {
        super(context);
    }

    public Search(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.search;
        LayoutInflater.from(context).inflate(resource, this);

        searchlist = findViewById(R.id.searchlist);
        search_bar = findViewById(R.id.search_bar);
        not_found = findViewById(R.id.not_found);
    }

    public Search(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.search;
        LayoutInflater.from(context).inflate(resource, this);

        searchlist = findViewById(R.id.searchlist);
        search_bar = findViewById(R.id.search_bar);
        not_found = findViewById(R.id.not_found);
    }

    public void initialize(Beats context) {
        searchlist.setLayoutManager(new LinearLayoutManager(context));
        searchlist.adapter = new SearchList.Adapter(context, audios);
        searchlist.setAdapter(searchlist.adapter);

        search_bar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence characters, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence characters, int start, int before, int count) {
                updateList(characters.toString(), audios);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList(String query, ArrayList<Audio> tracks) {
        if (query.isEmpty()) {
            tracks.clear();
            searchlist.setVisibility(GONE);
            not_found.setVisibility(VISIBLE);
            searchlist.adapter.notifyDataSetChanged();
            audios.addAll(searchlist.tracks);
        } else {
            not_found.setVisibility(GONE);
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
            searchlist.adapter.notifyDataSetChanged();
            setSearchedTracks(tracks);
        }
    }

    public void setSearchedTracks(ArrayList<Audio> tracks) {
        this.searched_tracks = tracks;
    }

    public ArrayList<Audio>getSearchedTracks( ) {
        return searched_tracks;
    }

    public void setTracks(ArrayList<Audio> tracks) {
        this.audios = tracks;

        searchlist.tracks = Utils.getTracks((Beats) getContext());
    }

    public void setPlaying(Playing playing) {
        this.playing = playing;
    }

    public Playing getPlaying() {
        return playing;
    }

    public void setNowPlaying(Playing.NowPlaying now_playing) {
        this.now_playing = now_playing;
    }

    public Playing.NowPlaying getNowPlaying() {
        return now_playing;
    }
}