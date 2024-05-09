/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.concurrency;

import android.content.Context;
import android.media.MediaPlayer;

import com.jaay.beats.types.Playlist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Runner implements Serializable {

    private ArrayList<Playlist> playlists;
    private File data;

    public Runner() {
    }

    public void save(ArrayList<Playlist> playlists) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ObjectOutputStream object = new ObjectOutputStream(output)) {
            object.writeObject(playlists);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        byte[] raw_data = output.toByteArray();

        try (FileOutputStream output_stream = new FileOutputStream(data)) {
            output_stream.write(raw_data);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        if(playlists == null) {
            playlists = new ArrayList<>();
        }
        this.playlists = playlists;
    }

    public ArrayList<Playlist> getPlaylists() {
        try (FileInputStream input = new FileInputStream(data)) {
            byte[] raw_data = new byte[(int) data.length()];
//            input.read(raw_data);

            try (ObjectInputStream object = new ObjectInputStream(new ByteArrayInputStream(raw_data))) {
                playlists = (ArrayList<Playlist>) object.readObject();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return playlists;
    }

    public void setData(Context context) {
        if (data == null) {
            data = new File(context.getCacheDir(), "playlists.dat");
        }
    }

    public File getData() {
        return data;
    }

}
