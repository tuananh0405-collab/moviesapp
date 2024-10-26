package com.example.testmock.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingData;

import com.example.testmock.api.MovieApiService;
import com.example.testmock.api.model.CreditsResponse;
import com.example.testmock.api.model.MovieResponse;
import com.example.testmock.database.MovieDao;
import com.example.testmock.model.Movie;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MovieRepository {
    private MovieApiService apiService;
    private final MovieDao movieDao;

    public final MutableLiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();
    public final MutableLiveData<Movie> movie = new MutableLiveData<>();

    public MovieRepository(MovieApiService apiService, MovieDao movieDao) {
        this.apiService = apiService;
        this.movieDao = movieDao;

        loadFavoriteMovies();
    }

    public Single<Movie> fetchMovieDetails(int movieId, String apiKey) {
        return apiService.getMovieDetails(movieId, apiKey);
    }

    public Single<CreditsResponse> fetchMovieCredits(int movieId, String apiKey) {
        return apiService.getMovieCredits(movieId, apiKey);
    }

    @SuppressLint("CheckResult")
    public void insert(Movie movie) {
        Completable.fromAction(() -> movieDao.insertMovie(movie))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
                        }, // OnComplete
                        throwable -> throwable.printStackTrace() // OnError
                );
    }

    @SuppressLint("CheckResult")
    public void update(Movie movie) {
        Completable.fromAction(() -> movieDao.updateMovie(movie))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
                        }, // OnComplete
                        throwable -> throwable.printStackTrace() // OnError
                );
    }

    @SuppressLint("CheckResult")
    public void loadFavoriteMovies() {
        Single.fromCallable(() -> movieDao.getFavoriteMovies())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(
                        favoriteMovies::postValue,
                        throwable -> throwable.printStackTrace()
                );
    }

    @SuppressLint("CheckResult")
    public void deleteMovieById(int id) {
        Completable.fromAction(() -> movieDao.deleteMovieById(id))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
                        }, // OnComplete
                        throwable -> throwable.printStackTrace() // OnError
                );
    }

    public MutableLiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}