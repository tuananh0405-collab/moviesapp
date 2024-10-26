package com.example.testmock.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.testmock.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Query("UPDATE reminders SET reminderTime = :reminderTime WHERE movieId = :movieId")
    void updateReminderByMovieId(long reminderTime, int movieId);

    @Query("DELETE FROM reminders WHERE movieId = :movieId")
    void deleteReminderByMovieId(int movieId);

    @Query("SELECT * FROM reminders ORDER BY reminderTime ASC")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminders WHERE movieId = :movieId LIMIT 1")
    Reminder getReminderByMovieId(int movieId);
}

