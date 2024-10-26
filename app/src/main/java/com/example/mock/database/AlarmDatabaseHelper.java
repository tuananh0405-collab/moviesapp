package com.example.mock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "alarms";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final String COLUMN_MOVIE_ID = "movie_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_POSTER_PATH = "poster_path";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE_TIME + " INTEGER, " +
                COLUMN_MOVIE_ID + " INTEGER," +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_RELEASE_DATE + " TEXT, " +
                COLUMN_RATING + " REAL, " +
                COLUMN_POSTER_PATH + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAlarmById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public long upsertAlarm(long dateTime, int movieId, String title, String releaseDate, float rating, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_TIME, dateTime);
        values.put(COLUMN_MOVIE_ID, movieId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_RELEASE_DATE, releaseDate);
        values.put(COLUMN_RATING, rating);
        values.put(COLUMN_POSTER_PATH, posterPath);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }


    public Cursor getAllAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE_TIME + " DESC");
    }

    public void deleteAlarmById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAlarmByMovieId(long movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});
        db.close();
    }

}
