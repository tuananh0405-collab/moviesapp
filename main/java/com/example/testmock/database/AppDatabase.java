package com.example.testmock.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

import com.example.testmock.model.Movie;
import com.example.testmock.model.Reminder;

@Database(entities = {Movie.class, Reminder.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();

    public abstract ReminderDao reminderDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "movie_database")
                            .fallbackToDestructiveMigration()  // Thêm điều này để tự động cập nhật schema khi thay đổi phiên bản
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
