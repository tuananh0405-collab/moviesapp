package com.example.mock.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.database.AlarmDatabaseHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper(context);

        // GET INTENT DATA FROM REMINDER
        long alarmId = intent.getLongExtra("alarmId", -1);
        String posterPath = intent.getStringExtra("image");
        String title = intent.getStringExtra("title");
        String overview = intent.getStringExtra("overview");
        int movieId = intent.getIntExtra("movieId", -1);

        if (alarmId != -1) {

            // SET UP CLICK NOTIFICATION TO OPEN MOVIE DETAIL
            Intent nextActivity = new Intent(context, MainActivity.class);
            nextActivity.putExtra("movieId", movieId);
            nextActivity.putExtra("open_movie_detail", true);
            nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // CREATE PENDING INTENT
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);

            // COLLAPSED NOTIFICATION VIEW
            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            notificationLayout.setTextViewText(R.id.tvNotificationTitle, title);
            notificationLayout.setTextViewText(R.id.tvNotificationContent, "Time to go get the ticket !");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String strDate = sdf.format(new Date());
            notificationLayout.setTextViewText(R.id.tvNotificationTime, strDate);

            // EXPANDED NOTIFICATION VIEW
            RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.custome_notification_expanded);
            notificationLayoutExpanded.setTextViewText(R.id.tvNotificationTitleExpanded, title);
            notificationLayoutExpanded.setTextViewText(R.id.tvNotificationContentExpanded, overview);

            // LOAD IMAGE INTO NOTIFICATION
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + posterPath).into(new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    notificationLayoutExpanded.setImageViewBitmap(R.id.imgCustomNotificationExpanded, bitmap);

                    // CREATE NOTIFICATION
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setCustomContentView(notificationLayout)
                            .setCustomBigContentView(notificationLayoutExpanded)
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    notificationManager.notify((int) alarmId, builder.build());
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.e("AlarmReceiver", "Failed to load image: " + e.getMessage());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });

            // DELETE ALARM FROM DATABASE AFTER NOTIFYING
            dbHelper.deleteAlarmByMovieId(alarmId);
        } else {
            Log.d("AlarmReceiver", "alarmId not received.");
        }
    }

}