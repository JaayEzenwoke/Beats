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
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.types.Playlist;
import com.jaay.beats.uiviews.Check;
import com.jaay.beats.uiviews.Dots;
import com.jaay.beats.uiviews.Edit;
import com.jaay.beats.uiviews.Slate;
import com.jaay.beats.uiviews.Stack;
import com.jaay.beats.uiviews.Options;
import com.jaay.beats.uiviews.Image;
import com.jaay.beats.uiviews.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class AllPlaylists extends Slate {

    public static class Playlists extends RecyclerView {

        public static class PlaylistTracks extends Stack {

            public static class Contents extends Songs {

                public static class Adapter extends Songs.Adapter {

                    public Adapter(Context context, ArrayList<Audio> tracks) {
                        super(context, tracks);
                    }

                    public void setTracks(ArrayList<Audio> tracks) {
                        this.tracks = tracks;
                    }
                }

                public Contents(@NonNull Context context) {
                    super(context);
                }

                public Contents(@NonNull Context context, @Nullable AttributeSet attributes) {
                    super(context, attributes);
                }

                public Contents(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
                    super(context, attributes, style);
                }

                @Override
                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();

                }
            }

            public Contents contents;
            private Text name;

            public PlaylistTracks(Context context) {
                super(context);
            }

            public PlaylistTracks(Context context, @Nullable AttributeSet attributes) {
                super(context, attributes);

                int resource = R.layout.playlist_tracks;
                LayoutInflater.from(context).inflate(resource, this);

                contents = findViewById(R.id.contents);
                name = findViewById(R.id.name);
            }

            public PlaylistTracks(Context context, @Nullable AttributeSet attributes, int style) {
                super(context, attributes, style);

                int resource = R.layout.playlist_tracks;
                LayoutInflater.from(context).inflate(resource, this);

                contents = findViewById(R.id.contents);
                name = findViewById(R.id.name);
            }

            public void setName(String name) {
                this.name.setText(name);
            }

            public void initialize(Context context) {
//                contents.initialize(context);
            }

        }

        public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

            public class Holder extends ViewHolder implements OnClickListener {

                public TextView song_count;
                public View separator;
                public TextView name;
                public Dots dots;

                public Holder(View view) {
                    super(view);

                    separator = view.findViewById(R.id.separator);
                    song_count = view.findViewById(R.id.song);
                    dots = view.findViewById(R.id.options);
                    name = view.findViewById(R.id.title);

                    view.setOnClickListener(this);

                    dots.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            all_playlists.options.setVisibility(VISIBLE);
                        }
                    });
                }

                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onClick(view, getAdapterPosition());
                }

                public AllPlaylists all_playlists;
                public AllPlaylists getAll_playlists() {
                    return all_playlists;
                }

                public void setAll_playlists(AllPlaylists all_playlists) {
                    this.all_playlists = all_playlists;
                }

            }

            public interface Listener {
                void onClick(View view, int position);
            }

            public ArrayList<Playlist> playlists;
            private LayoutInflater inflater;
            private Listener listener;
            private Holder holder;

            public Adapter(Context context, ArrayList<Playlist> tracks) {
                this.playlists = tracks;
                inflater = LayoutInflater.from(context);
            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
                View view = inflater.inflate(R.layout.song, parent, false);
                holder = new Holder(view);
                return holder;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull Holder holder, int position) {
                holder.song_count.setText(playlists.get(position).getSongCount() + " tracks");
                holder.name.setText(playlists.get(position).getName());

                if (position == playlists.size()) {
                    holder.separator.setVisibility(INVISIBLE);
                }
            }

            @Override
            public int getItemCount() {
                return playlists.size();
            }

            public Playlist getItem(int position) {
                return playlists.get(position);
            }

            void setClickListener(Listener listener) {
                this.listener = listener;
            }

            public Holder getHolder() {
                return holder;
            }
        }

        public AllPlaylists all_playlists;
        public ArrayList<Playlist> playlists;
        private Adapter adapter;

        public Playlists(@NonNull Context context) {
            super(context);
        }

        public Playlists(@NonNull Context context, @Nullable AttributeSet attributes) {
            super(context, attributes);
        }

        public Playlists(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);
        }

        public void initialize(Context context) {
            playlists = getPlaylists();
            setLayoutManager(new LinearLayoutManager(context));
            adapter = new Adapter(context, playlists);

            adapter.setClickListener(new Adapter.Listener() {

                @Override
                public void onClick(View view, int position) {

                    all_playlists.create.setVisibility(GONE);
                    all_playlists.add_songs.setVisibility(GONE);
                    all_playlists.add.setVisibility(GONE);
                    all_playlists.playlists.setVisibility(GONE);

//                    all_playlists.playlist_tracks.contents.setTracks(playlists.get(position).getTracks());
                    all_playlists.playlist_tracks.initialize(context);
                    all_playlists.playlist_tracks.setName(adapter.getItem(position).getName());

                    all_playlists.playlist_tracks.setVisibility(VISIBLE);
                }
            });

            setAdapter(adapter);
        }

        public AllPlaylists getAll_playlists() {
            return all_playlists;
        }

        public void setAll_playlists(AllPlaylists all_playlists) {
            this.all_playlists = all_playlists;
        }

        public void recentlyAdded() {
        }

        public void recentlyPlayed() {

        }

        public void mostPlayed() {

        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            Utils.debug(" [1] adapter: " + all_playlists.playlists.adapter.hashCode() + " | playlists: " + all_playlists.playlists.hashCode());
        }

        public void setPlaylists(ArrayList<Playlist> playlists) {
            this.playlists = playlists;
        }

        private ArrayList<Playlist> getPlaylists() {
            return playlists;
        }

        public void refreshPlaylists() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                post(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            }
        }

    }

    public static class CreatePlaylist extends Slate {
        public AllPlaylists all_playlists;
        private Text playlist_name;
        private Edit playlist_bar;
        private Playlist playlist;
        private ArrayList<Audio> tracks;
        private Text added_number;
        private Stack create_box;
        private Text add_tracks;
        private String name;
        private Text done;


        public CreatePlaylist(@NonNull Context context) {
            super(context);
        }

        public CreatePlaylist(@NonNull Context context, @Nullable AttributeSet attributes) {
            super(context, attributes);

            int resource = R.layout.create_playlist;
            LayoutInflater.from(context).inflate(resource, this);

            playlist_name = findViewById(R.id.playlist_name);
            playlist_bar  = findViewById(R.id.playlist_bar);
            added_number  = findViewById(R.id.added_number);
            add_tracks     = findViewById(R.id.add_tracks);
            done          = findViewById(R.id.done);

        }

        public CreatePlaylist(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);

            int resource = R.layout.create_playlist;
            LayoutInflater.from(context).inflate(resource, this);

            playlist_name = findViewById(R.id.playlist_name);
            playlist_bar  = findViewById(R.id.playlist_bar);
            added_number  = findViewById(R.id.added_number);
            add_tracks     = findViewById(R.id.add_songs);
            create_box  = findViewById(R.id.create_box);
            done          = findViewById(R.id.done);

        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            playlist_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE) {
                        name = playlist_bar.getText().toString();
                        playlist_name.setText(name + "");
                        hideKeyboard(playlist_bar);
                        return true;
                    }
                    return false;
                }
            });

            playlist_bar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    playlist_bar.getWindowVisibleDisplayFrame(r);
                    int screenHeight = playlist_bar.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // Adjust threshold if needed
                        added_number.setVisibility(GONE);
                        add_tracks.setVisibility(GONE);
                        done.setVisibility(GONE);
                    } else {
                        added_number.setVisibility(VISIBLE);
                        add_tracks.setVisibility(VISIBLE);
                        done.setVisibility(VISIBLE);
                    }
                }
            });

            add_tracks.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    all_playlists.playlists.setVisibility(GONE);
                    all_playlists.create.setVisibility(GONE);
                    all_playlists.add_songs.setVisibility(VISIBLE);
                }
            });

            done.setOnClickListener(new OnClickListener() {

                {
                    playlist = new Playlist(name);
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    name = playlist_bar.getText().toString();
                    playlist = new Playlist(name);
                    playlist_name.setText(name + "");
                    playlist.setTracks(tracks);

                    all_playlists.playlists.adapter.playlists.add(playlist);
                    all_playlists.playlists.adapter.notifyDataSetChanged();

                    setVisibility(GONE);
                    all_playlists.playlists.setVisibility(VISIBLE);

                }
            });
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);

            if(tracks != null) {
                if(!tracks.isEmpty()) {
                    added_number.setText(Utils.abbreviateNumber(tracks.size()) + " songs added");
                }
            }
        }

        private void hideKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        public AllPlaylists getAll_playlists() {
            return all_playlists;
        }

        public void setAll_playlists(AllPlaylists all_playlists) {
            this.all_playlists = all_playlists;
        }

    }

    public static class AddSongs extends Stack {

        public static class Selection extends RelativeLayout {

            public Check check;

            public Selection(Context context) {
                super(context);


                int resource = R.layout.selection;
                LayoutInflater.from(context).inflate(resource, this);

                check = findViewById(R.id.check);
            }

            public Selection(Context context, @Nullable AttributeSet attributes) {
                super(context, attributes);

                int resource = R.layout.selection;
                LayoutInflater.from(context).inflate(resource, this);

                check = findViewById(R.id.check);
                check.setChecked(false);
            }

            public Selection(Context context, @Nullable AttributeSet attributes, int style) {
                super(context, attributes, style);


                int resource = R.layout.selection;
                LayoutInflater.from(context).inflate(resource, this);

                check = findViewById(R.id.check);
                check.setChecked(false);
            }

        }

        public static class Addlist extends RecyclerView {

            public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

                public class Holder extends ViewHolder implements OnClickListener {

                    public View separator;
                    public TextView title;
                    public TextView song;
                    public Check check;

                    public Holder(View view) {
                        super(view);

                        separator = view.findViewById(R.id.separator);
                        title = view.findViewById(R.id.title);
                        check = view.findViewById(R.id.check);
                        song = view.findViewById(R.id.song);

                        view.setOnClickListener(this);
                    }

                    @Override
                    public void onClick(View view) {
                        if (listener != null) listener.onClick(view, getAdapterPosition());
                    }

                }

                public interface Listener {
                    void onClick(View view, int position);

                }

                private LayoutInflater inflater;
                private ArrayList<Audio> tracks;
                private Listener listener;
                private HashSet<Integer> selected_tracks;

                public Adapter(Context context, ArrayList<Audio> tracks) {
                    this.tracks = tracks;
                    inflater = LayoutInflater.from(context);
                    selected_tracks = new HashSet<>();
                }

                @NonNull
                @Override
                public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
                    View view = new Selection(parent.getContext());
                    return new Holder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull Holder holder, int position) {
                    holder.title.setText(tracks.get(position).getTitle());
                    holder.song.setText(tracks.get(position).getArtist());

                    // Set the check state based on selectedPositions
                    boolean isSelected = selected_tracks.contains(position);
                    holder.check.setSelection(isSelected);

                    // Update the Audio object's marked state to match
                    tracks.get(position).setMarked(isSelected);

                    if (position == tracks.size()) {
                        holder.separator.setVisibility(INVISIBLE);
                    } else {
                        holder.separator.setVisibility(VISIBLE);
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

                @SuppressLint("NotifyDataSetChanged")
                public void setTracks(ArrayList<Audio> tracks) {
                    this.tracks = tracks;
                    notifyDataSetChanged();
                }

                // Add methods to handle selection state
                public void toggleSelection(int position) {
                    if (selected_tracks.contains(position)) {
                        selected_tracks.remove(position);
                    } else {
                        selected_tracks.add(position);
                    }
                    notifyItemChanged(position);
                }

                public void selectAll() {
                    selected_tracks.clear();
                    for (int i = 0; i < tracks.size(); i++) {
                        selected_tracks.add(i);
                    }
                    notifyDataSetChanged();
                }

                public void clearSelections() {
                    selected_tracks.clear();
                    notifyDataSetChanged();
                }

                public ArrayList<Audio> getSelectedTracks() {
                    ArrayList<Audio> selected_songs = new ArrayList<>();
                    for (Integer position : selected_tracks) {
                        if (position < tracks.size()) {
                            selected_songs.add(tracks.get(position));
                        }
                    }
                    return selected_songs;
                }

                public boolean isAllSelected() {
                    return selected_tracks.size() == tracks.size();
                }

            }

            public ArrayList<Audio> temporary;
            private ArrayList<Audio> tracks;
            private Adapter adapter;

            public Addlist(@NonNull Context context) {
                super(context);
            }

            public Addlist(@NonNull Context context, @Nullable AttributeSet attributes) {
                super(context, attributes);
            }

            public Addlist(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
                super(context, attributes, style);
            }

            public void initialize(Context context) {
                tracks = getTracks();

                setLayoutManager(new LinearLayoutManager(context));
                adapter = new Adapter(context, tracks);

                adapter.setClickListener(new Adapter.Listener() {

                    @Override
                    public void onClick(View view, int position) {
                        // Toggle selection state
                        adapter.toggleSelection(position);

                        // Update the "select all" checkbox state
                        AddSongs parentView = (AddSongs) getParent();
                        if (parentView != null) {

                        }
                    }
                });
                setAdapter(adapter);
            }

            public void setTracks(ArrayList<Audio> tracks) {
                this.tracks = tracks;
            }

            public ArrayList<Audio> getTracks() {
                return tracks;
            }

            public ArrayList<Audio> getSelectedTracks() {
                return adapter.getSelectedTracks();
            }

            public void selectAll() {
                adapter.selectAll();
            }

            public void clearSelections() {
                adapter.clearSelections();
            }
        }

        public ArrayList<Audio> playlist_tracks;
        RecyclerView.LayoutManager manager;
        public AllPlaylists all_playlists;
        public Addlist addlist;
        private Image back;
        public Check check;
        public Text done;

        {
            playlist_tracks = new ArrayList<>();
        }
        public AddSongs(Context context) {
            super(context);
        }

        public AddSongs(Context context, @Nullable AttributeSet attributes) {
            super(context, attributes);

            int resource = R.layout.add_songs;
            LayoutInflater.from(context).inflate(resource, this);

            addlist  = findViewById(R.id.addlist);
            check = findViewById(R.id.check);
            done  = findViewById(R.id.done);
            back  = findViewById(R.id.back);

            check.setSelection(false);

        }

        public AddSongs(Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);

            int resource = R.layout.add_songs;
            LayoutInflater.from(context).inflate(resource, this);

            check = findViewById(R.id.check);
            done  = findViewById(R.id.done);
            back  = findViewById(R.id.back);

            check.setSelection(false);

        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            check.setOnClickListener(new OnClickListener() {
                private Background background;
                private int shade;

                {
                    background = check.getWallpaper();
                    shade = background.getShade();
                }

                @Override
                public void onClick(View view) {
                    Check check = (Check) view;

                    if (check.isChecked()) {
                        check.setSelection(false);
                        addlist.clearSelections();
                    } else {
                        check.setSelection(true);
                        addlist.selectAll();
                    }
                }
            });

            done.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    all_playlists.create.setVisibility(VISIBLE);
                    AddSongs.this.setVisibility(GONE);
                    setPlaylistTracks();
                }
            });

            back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    setPlaylistTracks();
                    all_playlists.create.tracks = playlist_tracks;
                    all_playlists.create.setVisibility(VISIBLE);
                    Utils.debug("playlistall_playlists.create.tracks_tracks: " + playlist_tracks.size());
                    AddSongs.this.setVisibility(GONE);
                    // TODO: 3/3/2025 remove from here after test
                }
            });

        }

        public AllPlaylists getAll_playlists() {
            return all_playlists;
        }

        public void setAll_playlists(AllPlaylists all_playlists) {
            this.all_playlists = all_playlists;
        }

        private void markAll(ArrayList<Audio> tracks) {
            addlist.selectAll();
        }

        private void unmarkAll(ArrayList<Audio> tracks) {
            addlist.clearSelections();
        }

        public ArrayList<Audio> getPlaylistTracks() {
            return playlist_tracks;
        }

        public void setPlaylistTracks() {
            // Clear previous selections first
            this.playlist_tracks.clear();
            // Add newly selected tracks
            this.playlist_tracks.addAll(addlist.getSelectedTracks());
        }

    }

    public Playlists.PlaylistTracks playlist_tracks;
    public CreatePlaylist create;
    public Playlists playlists;
    public AddSongs add_songs;
    public Options options;
    public Image add;

    public AllPlaylists(@NonNull Context context) {
        super(context);
    }

    public AllPlaylists(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        int resource = R.layout.playlists;
        LayoutInflater.from(context).inflate(resource, this);

        playlist_tracks = findViewById(R.id.playlist_tracks);
        playlists       = findViewById(R.id.all_playlists);
        add_songs       = findViewById(R.id.add_songs);
        create          = findViewById(R.id.create);
        add             = findViewById(R.id.add);
    }

    public AllPlaylists(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
        super(context, attributes, style);

        int resource = R.layout.playlists;
        LayoutInflater.from(context).inflate(resource, this);

        playlist_tracks = findViewById(R.id.playlist_tracks);
        playlists       = findViewById(R.id.all_playlists);
        add_songs       = findViewById(R.id.add_songs);
        create          = findViewById(R.id.create);
        add             = findViewById(R.id.add);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                playlist_tracks.setVisibility(GONE);
                create.setVisibility(VISIBLE);
            }
        });

        post(new Runnable() {

            @Override
            public void run() {
//                all_.contents.adapter.setAll_playlists(AllPlaylists.this);
                playlists.setAll_playlists(AllPlaylists.this);
                add_songs.setAll_playlists(AllPlaylists.this);
                create.setAll_playlists(AllPlaylists.this);
            }
        });

    }

    public Playlists.PlaylistTracks getPlaylist_tracks() {
        return playlist_tracks;
    }

    public void setPlaylist_tracks(Playlists.PlaylistTracks playlist_tracks) {
        playlist_tracks = playlist_tracks;
    }

    public CreatePlaylist getCreate() {
        return create;
    }

    public void setCreate(CreatePlaylist create) {
        this.create = create;
    }

    public Playlists getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
    }

    public AddSongs getAdd_songs() {
        return add_songs;
    }

    public void setAdd_songs(AddSongs add_songs) {
        this.add_songs = add_songs;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        options = options;
    }

}