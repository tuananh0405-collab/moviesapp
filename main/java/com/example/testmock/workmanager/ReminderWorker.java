package com.example.testmock.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.testmock.MainActivity;
import com.example.testmock.R;
import com.example.testmock.database.AppDatabase;
import com.example.testmock.database.ReminderDao;
import com.example.testmock.model.Reminder;

import java.util.List;

public class ReminderWorker extends Worker {


    public static final String CHANNEL_ID = "reminder_channel";
    public static final String ACTION_REMINDER_DELETED = "com.example.testmock.REMINDER_DELETED";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        ReminderDao reminderDao = db.reminderDao();
        List<Reminder> reminders = reminderDao.getAllReminders();

        for (Reminder reminder : reminders) {
            sendNotification(reminder);
            deleteReminder(reminder, reminderDao);
        }

        return Result.success();
    }

    private void sendNotification(Reminder reminder) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_email)
                .setContentTitle("Reminder")
                .setContentText("Reminder for movie ID: " + reminder.getMovieId())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reminder Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(reminder.getId(), builder.build());

    }

    private void deleteReminder(Reminder reminder, ReminderDao reminderDao) {
        new Thread(() -> {
            reminderDao.deleteReminderByMovieId(reminder.getMovieId());

            // Send a broadcast to notify the ViewModel or Activity
            Intent intent = new Intent(ACTION_REMINDER_DELETED);
            intent.putExtra("movieId", reminder.getMovieId());
            getApplicationContext().sendBroadcast(intent);
        }).start();
    }
}
