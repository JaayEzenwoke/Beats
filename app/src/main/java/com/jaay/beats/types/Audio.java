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

public class Audio {

    private String artist;
    private String title;

    private String path;
    private String date;

    private int played;

    private boolean favourite;
    private boolean selected;
    private boolean marked;

    public Audio(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public Audio(String artist, String title, String path) {
        this.artist = artist;
        this.title = title;
        this.path = path;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * gets the number of times it has been played.
     */
    public int getPlayCount() {
        return played;
    }

    /**
     * sets the number of times it has been played.
     * @param played number of times track has been played.
     */
    public void setPlayCount(int played) {
        this.played = played;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
