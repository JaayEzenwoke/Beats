/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;
import com.jaay.beats.types.Audio;

import java.io.File;
import java.util.ArrayList;

public class Utils {

    public static Background getTile(Context context, AttributeSet attributes){
        Background background = new Background();
        TypedArray stylable = context.obtainStyledAttributes(attributes, R.styleable.Beats);
        try{
            float radius = stylable.getDimensionPixelSize(R.styleable.Beats_radius, 0);
            float left = stylable.getDimensionPixelSize(R.styleable.Beats_left_radius, 0);
            float top = stylable.getDimensionPixelSize(R.styleable.Beats_top_radius, 0);
            float right = stylable.getDimensionPixelSize(R.styleable.Beats_right_radius, 0);
            float bottom = stylable.getDimensionPixelSize(R.styleable.Beats_bottom_radius, 0);

            int stroke = stylable.getDimensionPixelSize(R.styleable.Beats_stroke, 0);
            int stroke_shade = stylable.getColor(R.styleable.Beats_stroke_shade, 0);
            int shade = stylable.getColor(R.styleable.Beats_shade, 0);
            float[] radii = new float[]{left, top, right, bottom};

            background.setStroke(stroke, stroke_shade);
            background.setRadius(radius, radii);
            background.setShade(shade);
            background.setWallpaper();
            return background;
        }finally {
            stylable.recycle();
        }
    }

    public static void debug(Object debug) {
        Log.d("Beats: ", " "  + debug);
    }

    public static String getTrimmed(String source) {
        Utils.debug("path: " + source);
        int length = source.length() - 1;
        StringBuilder builder = new StringBuilder();
        for (int i = length; i > 0; i--) {
            char character = source.charAt(i);
            if(character == '.') {
                length += 1;
                for (int j = i; j < length; j++) {
                    builder.append(source.charAt(j));
                }
                source = source.replace(builder.toString(), "");
                break;
            }
        }
        return source;
    }

    public static String getUniqueFilename(Audio audio, ArrayList<Audio> tracks) {
        int i = 1;
        Utils.debug("title: " + audio.getPath() + " | extension " + getExtension(audio.getPath())  + " | trimmed " + getTrimmed(audio.getPath()));
        String name = "";
        String extension = getExtension(audio.getPath());

        for (int j = 0; j < tracks.size(); j++) {
            name = audio.getTitle() + "(" + i + ")." + extension;
            if (tracks.get(i).getTitle().equals(name)) {
                i++;
            }else {
                break;
            }
        }
        return name;
    }

    public static String getTrimmed2(String source) {
        int lastDotIndex = source.lastIndexOf(".");
        if (lastDotIndex != -1) {
            source = source.substring(0, lastDotIndex);
        }
        return source;
    }

    public static String getExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // no extension found
        }
        return filename.substring(lastIndexOf + 1);
    }

}
