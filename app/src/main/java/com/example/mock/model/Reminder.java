package com.example.mock.model;

public class Reminder {
    private long id;
    private String title;
    private String releaseDate;
    private float rating;
    private String reminderTime;
    private String posterPath;

    public Reminder(long id, String title, String releaseDate, float rating, String reminderTime, String posterPath) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.reminderTime = reminderTime;
        this.posterPath = posterPath;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
