package com.example.testmock.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testmock.database.AppDatabase;
import com.example.testmock.database.ReminderDao;
import com.example.testmock.model.Reminder;
import com.example.testmock.workmanager.ReminderWorker;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ReminderViewModel extends ViewModel {
    private MutableLiveData<List<Reminder>> remindersLiveData = new MutableLiveData<>();
    private ReminderDao reminderDao;
    private Application application;
    AppDatabase db;

    public void setApplication(Application application) {
        this.application = application;
        db = AppDatabase.getDatabase(application);
        loadReminder();
    }

    public ReminderViewModel() {

    }

    void loadReminder() {
        new Thread(() -> {
            reminderDao = db.reminderDao();
            remindersLiveData.postValue(reminderDao.getAllReminders());  // Lấy LiveData từ Room
        }).start();
    }


    public LiveData<List<Reminder>> getReminders() {
        return remindersLiveData;
    }

    public void insertReminder(Reminder reminder) {
        new Thread(() -> {
            reminderDao.insert(reminder);
            List<Reminder> updatedReminders = reminderDao.getAllReminders();
            remindersLiveData.postValue(updatedReminders);
        }).start();
    }

    public void updateReminder(Reminder reminder) {
        new Thread(() -> {
            reminderDao.updateReminderByMovieId(reminder.getReminderTime(), reminder.getMovieId());
            List<Reminder> updatedReminders = reminderDao.getAllReminders();
            remindersLiveData.postValue(updatedReminders);
        }).start();
    }

    public void deleteReminder(int movieId) {
        new Thread(() -> {
            reminderDao.deleteReminderByMovieId(movieId);
            List<Reminder> updatedReminders = reminderDao.getAllReminders();
            remindersLiveData.postValue(updatedReminders);
        }).start();
    }

}
