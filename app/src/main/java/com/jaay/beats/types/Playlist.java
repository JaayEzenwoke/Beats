/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.types;

import java.util.ArrayList;

public class Playlist {

    private ArrayList<Audio> tracks;
    private String Songs;
    private String name;

    private int songs_count;

    public Playlist(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSongs() {
        return Songs;
    }

    public void setSongs(String songs) {
        Songs = songs;
    }

    public int getSongCount() {
        return tracks.size();
    }

    public void setSongCount(int songs_count) {
        this.songs_count = songs_count;
    }

    public ArrayList<Audio> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Audio> tracks) {
        this.tracks = tracks;
    }
}
