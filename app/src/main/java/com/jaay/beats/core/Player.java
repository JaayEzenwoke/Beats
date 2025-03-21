package com.jaay.beats.core;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaay.beats.R;
import com.jaay.beats.reels.Playing;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;
import com.jaay.beats.types.Playlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


public class Player extends MediaPlayer {

    public static class Mode {
        public static final int repeat_current = 0x8000;
        public static final int repeat_all = 0x0400;
        public static final int shuffle = 0x0020;
        public static final int all = 0x0001;
    }

    private static final String TAG = "Beat";

    @SuppressLint("SetTextI18n")
    public void initialize(ArrayList<Audio> tracks, Playing playing, int position, RecyclerView recycler) {
        playing.seek_bar.additions = 0;
        playing.dropper.seeker.additions = 0;

//        recycler.getAdapter()

    }

    public void play(Audio audio) {
        if (isPlaying()) {
            stop();
            reset();
        }

        try {
            reset();
            setDataSource(audio.getPath());
            prepare();
            start();
        } catch (IOException exception) {

        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (isPlaying()) {
            super.pause();
        }
    }

    //    public void pause() {
//
//
//
//    }
}
