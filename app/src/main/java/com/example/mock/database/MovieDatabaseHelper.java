package com.example.mock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mock.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_OVERVIEW = "overview";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MOVIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_RELEASE_DATE + " TEXT, " +
                    COLUMN_RATING + " REAL, " +
                    COLUMN_POSTER_PATH + " TEXT, " +
                    COLUMN_OVERVIEW + " TEXT" +
                    ");";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

    public void addFavoriteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, movie.getId());
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(COLUMN_RATING, movie.getRating());
        values.put(COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(COLUMN_OVERVIEW, movie.getOverview());

        db.insert(TABLE_MOVIES, null, values);
        db.close();
    }

    public List<Movie> getAllFavoriteMovies() {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)));
                movie.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OVERVIEW)));
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return movies;
    }

    public boolean isFavorite(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOVIES,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(movieId)},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();

        return exists;
    }

    public void removeFavoriteMovie(int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, COLUMN_ID + "=?", new String[]{String.valueOf(movieId)});
        db.close();
    }
}
