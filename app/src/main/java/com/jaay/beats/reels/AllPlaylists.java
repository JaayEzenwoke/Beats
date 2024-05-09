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
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;
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

public class AllPlaylists extends Slate {

    public static class Playlists extends RecyclerView {

        public static class PlaylistTracks extends Stack {

            public static class Contents extends RecyclerView {

                public static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

                    public class Holder extends ViewHolder implements OnClickListener {

                        public View separator;
                        public TextView title;
                        public TextView song;
                        public Dots dots;

                        public Holder(View view) {
                            super(view);

                            separator = view.findViewById(R.id.separator);
                            title = view.findViewById(R.id.title);
                            song = view.findViewById(R.id.song);
                            dots = view.findViewById(R.id.options);

                            view.setOnClickListener(this);
                            dots.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AllPlaylists.options.setVisibility(VISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onClick(View view) {
                            if (listener != null) listener.onClick(view, getAdapterPosition());
                        }
                    }

                    public interface Listener {
                        void onClick(View view, int position);
                    }

                    private ArrayList<Audio> tracks;
                    private LayoutInflater inflater;
                    private Listener listener;

                    public Adapter(Context context, ArrayList<Audio> tracks) {
                        this.tracks = tracks;
                        inflater = LayoutInflater.from(context);
                    }

                    @NonNull
                    @Override
                    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
                        View view = inflater.inflate(R.layout.song, parent, false);
                        return new Holder(view);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull Holder holder, int position) {

                        if(tracks.get(position) == null) {
                        }else {
                            holder.song.setText(tracks.get(position).getArtist());
                            holder.title.setText(tracks.get(position).getTitle());
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

                    void setClickListener(Listener listener) {
                        this.listener = listener;
                    }

                }

                public ArrayList<Audio> tracks;
                private MediaPlayer player;
                private Adapter adapter;

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

                public void initialize(Context context) {
                    tracks = getTracks();
                    setLayoutManager(new LinearLayoutManager(context));
                    adapter = new Adapter(context, tracks);
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

                public ArrayList<Audio> getTracks() {
                    return tracks;
                }

                public void setTracks(ArrayList<Audio> tracks) {
                    this.tracks = tracks;
                }

                public MediaPlayer getPlayer() {
                    return player;
                }

                public void setPlayer(MediaPlayer player) {
                    this.player = player;
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
                contents.initialize(context);
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
                            AllPlaylists.options.setVisibility(VISIBLE);
                        }
                    });
                }

                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onClick(view, getAdapterPosition());
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

            @Override
            public void onBindViewHolder(@NonNull Holder holder, int position) {
                holder.song_count.setText(playlists.get(position).getSongCount() + "");
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

                    create.setVisibility(GONE);
                    add_songs.setVisibility(GONE);
                    AllPlaylists.add.setVisibility(GONE);
                    AllPlaylists.playlists.setVisibility(GONE);

                    playlist_tracks.contents.setTracks(playlists.get(position).getTracks());
                    playlist_tracks.initialize(context);
                    playlist_tracks.setName(adapter.getItem(position).getName());

                    playlist_tracks.setVisibility(VISIBLE);
                }
            });

            setAdapter(adapter);
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

        public void setPlaylists(ArrayList<Playlist> playlists) {
            this.playlists = playlists;
        }

        private ArrayList<Playlist> getPlaylists() {
            return playlists;
        }

    }

    public static class CreatePlaylist extends Slate {

        private Text playlist_name;
        private Edit playlist_bar;
        private Playlist playlist;
        private Text added_number;
        private Text add_songs;
        private Text done;

        private String name;

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
            add_songs     = findViewById(R.id.add_tracks);
            done          = findViewById(R.id.done);

        }

        public CreatePlaylist(@NonNull Context context, @Nullable AttributeSet attributes, int style) {
            super(context, attributes, style);

            int resource = R.layout.create_playlist;
            LayoutInflater.from(context).inflate(resource, this);

            playlist_name = findViewById(R.id.playlist_name);
            playlist_bar  = findViewById(R.id.playlist_bar);
            added_number  = findViewById(R.id.added_number);
            add_songs     = findViewById(R.id.add_songs);
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
                        return true;
                    }
                    return false;
                }
            });

            add_songs.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    AllPlaylists.playlists.setVisibility(GONE);
                    AllPlaylists.create.setVisibility(GONE);
                    AllPlaylists.add_songs.setVisibility(VISIBLE);
                }
            });

            done.setOnClickListener(new OnClickListener() {

                {
                    playlist = new Playlist(name);
                }
                @Override
                public void onClick(View view) {
                    playlist.setTracks(AllPlaylists.add_songs.getPlaylistTracks());
                    AllPlaylists.playlists.playlists.add(playlist);
                    CreatePlaylist.this.setVisibility(GONE);
                }
            });

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

                public Adapter(Context context, ArrayList<Audio> tracks) {
                    this.tracks = tracks;
                    inflater = LayoutInflater.from(context);
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

                    if(add_songs.check.isChecked()) {
                        holder.check.setSelection(true);
                    }
                    if(holder.check.isChecked()) {
                        holder.check.setSelection(true);
                    }else {
                        holder.check.setSelection(false);
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

                public void setTracks(ArrayList<Audio> tracks) {
                    this.tracks = tracks;
                    notifyDataSetChanged();
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

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
            }

            public void initialize(Context context) {
                tracks = getTracks();
                temporary  = new ArrayList<>(tracks.size());
                for (int i = 0; i < tracks.size(); i++) {
                    temporary.add(null);
                }

                setLayoutManager(new LinearLayoutManager(context));
                adapter = new Adapter(context, tracks);
                adapter.setClickListener(new Adapter.Listener() {

                    @Override
                    public void onClick(View view, int position) {
                        AddSongs.Selection selection = (AddSongs.Selection) view;
                        Audio audio = adapter.getItem(position);

                        if(selection.check.isChecked()) {
                            selection.check.setSelection(false);
                            audio.setMarked(false);
                            temporary.remove(position);
                        }else {
                            selection.check.setSelection(true);
                            audio.setMarked(true);
                            temporary.add(position, audio);
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
        }

        public ArrayList<Audio> playlist_tracks;
        RecyclerView.LayoutManager manager;
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

                    if(check.isChecked()) {
                        check.setSelection(false);
                        unmarkAll(addlist.tracks);
                    }else {
                        check.setSelection(true);
                        markAll(addlist.tracks);
                    }
                }
            });

            done.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    AllPlaylists.create.setVisibility(VISIBLE);
                    AddSongs.this.setVisibility(GONE);
                    setPlaylistTracks();

                }
            });

            back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    create.setVisibility(VISIBLE);
                    AddSongs.this.setVisibility(GONE);
                }
            });

        }

        private void markAll(ArrayList<Audio> tracks) {
            for (int i = 0; i < tracks.size(); i++) {
                tracks.get(i).setMarked(true);
                addlist.temporary.add(tracks.get(i));
            }
            for (int i = 0; i < addlist.getChildCount(); i++) {
                Selection selection = (Selection) addlist.getChildAt(i);
                selection.check.setSelection(true);
            }
        }

        private void unmarkAll(ArrayList<Audio> tracks) {

            for (int i = 0; i < tracks.size(); i++) {
                tracks.get(i).setMarked(false);
                addlist.temporary.remove(tracks.get(i));
            }

            for (int i = 0; i < addlist.getChildCount(); i++) {
                Selection selection = (Selection) addlist.getChildAt(i);
                selection.check.setSelection(false);
            }
        }

        public ArrayList<Audio> getPlaylistTracks() {
            return playlist_tracks;
        }

        public void setPlaylistTracks() {
            this.playlist_tracks.addAll(addlist.temporary);
        }

    }

    public static Playlists.PlaylistTracks playlist_tracks;
    public static CreatePlaylist create;
    public static Playlists playlists;
    public static AddSongs add_songs;
    public static Options options;
    public static Image add;

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
    }

    public Playlists.PlaylistTracks getPlaylist_tracks() {
        return playlist_tracks;
    }

    public void setPlaylist_tracks(Playlists.PlaylistTracks playlist_tracks) {
        AllPlaylists.playlist_tracks = playlist_tracks;
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
        AllPlaylists.options = options;
    }
}
