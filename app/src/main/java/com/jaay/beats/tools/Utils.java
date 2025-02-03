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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.jaay.beats.R;
import com.jaay.beats.design.Background;
import com.jaay.beats.types.Audio;

import java.io.File;
import java.io.IOException;
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

    public static int mix(int left, int right, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio)); // Clamp ratio to 0-1 range

        int alpha1 = Color.alpha(left);
        int red1 = Color.red(left);
        int green1 = Color.green(left);
        int blue1 = Color.blue(left);

        int alpha2 = Color.alpha(right);
        int red2 = Color.red(right);
        int green2 = Color.green(right);
        int blue2 = Color.blue(right);

        int alpha = (int) (alpha1 * ratio + alpha2 * (1 - ratio));
        int red = (int) (red1 * ratio + red2 * (1 - ratio));
        int green = (int) (green1 * ratio + green2 * (1 - ratio));
        int blue = (int) (blue1 * ratio + blue2 * (1 - ratio));

        return Color.argb(alpha, red, green, blue);
    }

    public static int getTransluscency(int shade, int transluscence) {
        int alpha = Math.max(0, Math.min(255, transluscence)); // Ensure alpha is within 0-255
        return (alpha << 24) | (shade & 0x00FFFFFF); // Apply new alpha while keeping RGB
    }

    public static void setImage (Context context, String path, ImageView thumbnail) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.parse(path));

        byte[] artwork = retriever.getEmbeddedPicture();
        if (artwork != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            thumbnail.setImageBitmap(bitmap);
        }

        try {
            retriever.release();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
