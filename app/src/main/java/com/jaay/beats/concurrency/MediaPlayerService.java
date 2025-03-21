package com.jaay.beats.concurrency;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.jaay.beats.R;
import com.jaay.beats.activities.Base;

public class MediaPlayerService extends Service {

    private static final String CHANNEL_ID = "media_playback_channel";
    private static final int NOTIFICATION_ID = 1;

    // Action constants for media controls
    public static final String ACTION_PLAY = "com.jaay.beats.action.PLAY";
    public static final String ACTION_PAUSE = "com.jaay.beats.action.PAUSE";
    public static final String ACTION_PREVIOUS = "com.jaay.beats.action.PREVIOUS";
    public static final String ACTION_NEXT = "com.jaay.beats.action.NEXT";

    private NotificationManager manager;
    private boolean is_playing = false;
    private String title = "Unknown Track";
    private String artist = "Unknown Artist";
    private Bitmap album_art;

    // Binder for activity communication
    private final IBinder binder = new MediaPlayerBinder();

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PLAY:
                        // Forward to activity
                        Intent playIntent = new Intent("MEDIA_PLAYER_PLAY");
                        sendBroadcast(playIntent);
                        break;
                    case ACTION_PAUSE:
                        // Forward to activity
                        Intent pauseIntent = new Intent("MEDIA_PLAYER_PAUSE");
                        sendBroadcast(pauseIntent);
                        break;
                    case ACTION_PREVIOUS:
                        // Forward to activity
                        Intent prevIntent = new Intent("MEDIA_PLAYER_PREVIOUS");
                        sendBroadcast(prevIntent);
                        break;
                    case ACTION_NEXT:
                        // Forward to activity
                        Intent nextIntent = new Intent("MEDIA_PLAYER_NEXT");
                        sendBroadcast(nextIntent);
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Playback",
                    NotificationManager.IMPORTANCE_LOW); // Low importance prevents sound
            channel.setDescription("Shows media playback controls");
            channel.setShowBadge(false);
            manager.createNotificationChannel(channel);
        }

        // Register broadcast receiver for notification controls
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PREVIOUS);
        filter.addAction(ACTION_NEXT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }else {
            getApplicationContext().registerReceiver(receiver, filter);
        }

        // Initialize default album art
        album_art = BitmapFactory.decodeResource(getResources(), R.drawable.disc_icon);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create an initial notification to start as foreground service
        updateNotification();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    // Update the media info and notification
    public void updateMediaInfo(String title, String artist, Bitmap art, boolean playing) {
        this.title = title;
        this.artist = artist;
        is_playing = playing;
        if (art != null) {
            album_art = art;
        }
        updateNotification();
    }

    // Update just the play state
    public void updatePlayState(boolean playing) {
        is_playing = playing;
        updateNotification();
    }

    // Create and show the notification
    private void updateNotification() {
        // Create a custom notification view
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);

        // Set content to the RemoteViews
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setTextViewText(R.id.artist, artist);
        notificationLayout.setImageViewBitmap(R.id.album_art, album_art);

        // Set the icons based on the playback state
        if (is_playing) {
            notificationLayout.setImageViewResource(R.id.play_pause, R.drawable.pause);
        } else {
            notificationLayout.setImageViewResource(R.id.play_pause, R.drawable.play);
        }

        // Set up the pending intents for the actions
        PendingIntent playPauseIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(is_playing ? ACTION_PAUSE : ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent prevIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(ACTION_PREVIOUS),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent nextIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(ACTION_NEXT),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Attach the pending intents to buttons
        notificationLayout.setOnClickPendingIntent(R.id.play_pause, playPauseIntent);
        notificationLayout.setOnClickPendingIntent(R.id.prev, prevIntent);
        notificationLayout.setOnClickPendingIntent(R.id.next, nextIntent);

        // Create pending intent for opening the app when notification is clicked
        Intent contentIntent = new Intent(this, Base.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                this,
                0,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this)
                    .setPriority(Notification.PRIORITY_LOW);
        }

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification = builder
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(contentPendingIntent)
                    .setCustomContentView(notificationLayout)
                    .setOngoing(true)
                    .build();
        }

        // Show the notification and make the service foreground
        startForeground(NOTIFICATION_ID, notification);
    }
}