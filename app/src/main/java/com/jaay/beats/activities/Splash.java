/*
 * Copyright 2024 Justice Ezenwoke Chukwuemeka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package com.jaay.beats.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.jaay.beats.R;
import com.jaay.beats.concurrency.Runner;
import com.jaay.beats.playground.Debbugger;
import com.jaay.beats.playground.TestActivity;
import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Playlist;

import java.io.File;
import java.util.ArrayList;

public class Splash extends Beats {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAndRequestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        // Check if our permission request was granted
        if (requestCode == 1) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                setUp();
            }
        }
    }

    public void setUp() {
        // Permission was granted, set up the player
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Runner runner = new Runner();
        runner.setData(this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent;
                intent = new Intent(Splash.this, Base.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("runner", runner);
                startActivity(intent);
                finish();
            }
        }, 1200);
    }

    private void checkAndRequestPermissions() {
        // Check if we already have the necessary permissions
        if (Utils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                Utils.checkPermission(this, Manifest.permission.READ_MEDIA_AUDIO)) {
            // We have permission, proceed with setup
            setUp();
        } else {
            // We don't have permission, request it
            Utils.requestPermission(this);
        }
    }

//    private void showPermissionDeniedMessage() {
//        // Create and show an alert dialog
//        new AlertDialog.Builder(this)
//                .setTitle("Permission Required")
//                .setMessage("Storage permission is required to access music files. Without this permission, the app cannot function properly.")
//                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Try requesting permission again
//                        Utils.requestPermission(Splash.this);
//                    }
//                })
//                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Close the app
//                        finish();
//                    }
//                })
//                .setCancelable(false)
//                .show();


}