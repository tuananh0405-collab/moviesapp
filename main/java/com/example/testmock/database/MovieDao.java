package com.example.testmock.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.testmock.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Update
    void updateMovie(Movie movie);

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    List<Movie> getFavoriteMovies();

    @Query("SELECT * FROM movies WHERE id = :id")
    Movie getMovieById(int id);

    @Query("DELETE FROM movies WHERE id = :id")
    void deleteMovieById(int id);
}
