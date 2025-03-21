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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jaay.beats.R;
import com.jaay.beats.activities.Beats;
import com.jaay.beats.core.FFT;
import com.jaay.beats.design.Background;
import com.jaay.beats.types.Audio;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public static Bitmap setImage (Context context, String path, ImageView thumbnail) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.parse(path));

        byte[] artwork = retriever.getEmbeddedPicture();
        if (artwork != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            thumbnail.setImageBitmap(bitmap);
            return bitmap;
        }

        try {
            retriever.release();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static byte[] getImage (Context context, String path) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(context, Uri.parse(path));

            return retriever.getEmbeddedPicture();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  static boolean checkPermission(Beats beats, String permission) {
        int result = ContextCompat.checkSelfPermission(beats, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public  static void requestPermission(Beats beats) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            ActivityCompat.requestPermissions(beats, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
        } else {
            ActivityCompat.requestPermissions(beats, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public static ArrayList<Audio> getTracks(Beats context) {
        ArrayList<Audio> tracks =  new ArrayList<>();

        Utils.debug("checkPermission");
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED
        };

        // Add some debugging
        Utils.debug("Starting query for audio files");

        try {
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            Utils.debug("Cursor returned: " + (cursor != null ? "not null" : "null"));
            if (cursor != null) {
                Utils.debug("Cursor count: " + cursor.getCount());

                int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int dateIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

                while (cursor.moveToNext()) {
                    String date_added = cursor.getString(dateIdx);
                    String artist = cursor.getString(artistIdx);
                    String title = cursor.getString(titleIdx);
                    String path = cursor.getString(dataIdx);

                    Audio audio = new Audio(artist, title, path);
                    audio.setDate(date_added);
                    tracks.add(audio);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Utils.debug("Error querying media: " + e.getMessage());
            e.printStackTrace();
        }

        return tracks;
    }

    @SuppressLint("DefaultLocale")
    public static String abbreviateNumber(long value) {
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1_000_000) {
            return String.format("%.1fK", value / 1000.0);
        } else if (value < 1_000_000_000) {
            return String.format("%.1fM", value / 1_000_000.0);
        } else if (value < 1_000_000_000_000L) {
            return String.format("%.1fB", value / 1_000_000_000.0);
        } else {
            return String.format("%.1fT", value / 1_000_000_000_000.0);
        }
    }

    public static byte[] shortArrayToByteArray(short[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        buffer.asShortBuffer().put(data);
        return buffer.array();
    }

    public static short[] byteArrayToShortArray(byte[] data) {
        int length = data.length / 2;
        short[] result = new short[length];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(result);
        return result;
    }

    public static void processWithFFT(short[] samples, FFT fft, double[] real, double[] imag) {
        int fftSize = real.length;
        int numFrames = samples.length / fftSize;

        for (int frame = 0; frame < numFrames; frame++) {
            // Convert PCM to floating-point values
            for (int i = 0; i < fftSize; i++) {
                real[i] = samples[frame * fftSize + i] / 32768.0; // Normalize to [-1, 1]
                imag[i] = 0; // No imaginary part
            }

            // Perform FFT
            fft.fft(real, imag);

            // Spectral Subtraction (Reduce Noise)
            for (int i = 0; i < fftSize / 2; i++) {
                double magnitude = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);

                // Noise thresholding
                if (magnitude < 0.01) { // Adjust noise floor threshold
                    real[i] = 0;
                    imag[i] = 0;
                }
            }

            // Perform Inverse FFT
            fft.ifft(real, imag);

            // Convert back to PCM
            for (int i = 0; i < fftSize; i++) {
                samples[frame * fftSize + i] = (short) Math.max(-32768, Math.min(32767, real[i] * 32768)); // Convert back to 16-bit
            }
        }
    }

    public static String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%d:%02d", minutes, remainingSeconds);
    }

}
