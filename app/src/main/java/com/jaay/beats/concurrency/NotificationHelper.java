package com.jaay.beats.concurrency;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import com.jaay.beats.R;

public class NotificationHelper {

//    private static final Context appContext = getContext(); // Replace with actual context provider
//
//    public static Notification createNotification(String title, String text, String channelID, Bitmap bitmap) {
//        NotificationCompat.Builder notificationBuilder = createDefaultBuilder(title, text, channelID);
//        try {
//            setCustomNotification(notificationBuilder, title, text, bitmap);
//        } catch (Exception exception) {
//            // Use the default builder as fallback
//            setBigPictureStyle(notificationBuilder, title, text, bitmap);
//        }
//        return notificationBuilder.build();
//    }
//
//    private static NotificationCompat.Builder createDefaultBuilder(String title, String text, String channelID) {
//        createNotificationChannelIfNecessary(channelID); // Ensure the notification channel exists
//
//        return new NotificationCompat.Builder(appContext, channelID)
//                .setSmallIcon(R.drawable.ic_notification) // Required
//                .setPriority(NotificationCompat.PRIORITY_MIN)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setContentTitle(title) // Required
//                .setContentText(text); // Required
//    }
//
//    private static void setCustomNotification(NotificationCompat.Builder builder, String title, String text, Bitmap bitmap) {
//        // Inflate the layout and set the values to our UI IDs
//        RemoteViews remoteViews = new RemoteViews(appContext.getPackageName(), R.layout.now_playing);
//        remoteViews.setImageViewBitmap(R.id.image, bitmap);
//        remoteViews.setTextViewText(R.id.title, title);
//        remoteViews.setTextViewText(R.id.text, text);
//
//        builder.setCustomContentView(remoteViews);
//    }
//
//    private static void setBigPictureStyle(NotificationCompat.Builder builder, String title, String text, Bitmap bitmap) {
//        builder.setLargeIcon(bitmap)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(bitmap)
//                        .setBigContentTitle(title)
//                        .setSummaryText(text));
//    }
//
//    private static void createNotificationChannelIfNecessary(String channelID) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
//                NotificationChannel channel = new NotificationChannel(channelID, "Channel Name", NotificationManager.IMPORTANCE_LOW);
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//    }
}

