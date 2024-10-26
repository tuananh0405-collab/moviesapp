package com.example.testmock.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.testmock.api.MovieApiService;
import com.example.testmock.api.model.CastMember;
import com.example.testmock.api.model.CrewMember;
import com.example.testmock.application.MyApplication;
import com.example.testmock.builder.SettingsBuilder;
import com.example.testmock.model.Movie;
import com.example.testmock.model.Reminder;
import com.example.testmock.pagingsource.MoviePagingSource;
import com.example.testmock.repository.MovieRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviesViewModel extends ViewModel {

    private static final String TAG = "TAGTAGTAG";
    private final MutableLiveData<SettingsBuilder> settingsBuilderLiveData = new MutableLiveData<>(new SettingsBuilder());

    private final MovieRepository repository;
    private MutableLiveData<List<Movie>> favoriteMoviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> favoriteCountLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Movie> selectedMovieLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CastMember>> castMembersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CrewMember>> crewMembersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Reminder>> remindersLiveData = new MutableLiveData<>();
    private final MovieApiService movieApiService;
    private final MediatorLiveData<Flowable<PagingData<Movie>>> moviesFlowable = new MediatorLiveData<>();

    public MutableLiveData<SettingsBuilder> getSettingsBuilderLiveData() {
        return settingsBuilderLiveData;
    }

    public void updateSettings(String movieCategoryFilter, String sortOption, int pagesPerLoading) {
        SettingsBuilder builder = settingsBuilderLiveData.getValue();
        if (builder != null) {
            builder.setMovieCategoryFilter(movieCategoryFilter)
                    .setSortOption(sortOption)
                    .setPagesPerLoading(pagesPerLoading);

            settingsBuilderLiveData.setValue(builder.build()); // Update SettingsBuilder
        }
    }

    public MoviesViewModel(MovieRepository repository, MovieApiService movieApiService) {
        this.movieApiService = movieApiService;
        this.repository = repository;
        favoriteMoviesLiveData = repository.getFavoriteMovies();
        moviesFlowable.addSource(settingsBuilderLiveData, settings -> updateMoviesFlowable());
        moviesFlowable.addSource(favoriteMoviesLiveData, favorites -> updateMoviesFlowable());
    }

    private void updateMoviesFlowable() {
        moviesFlowable.setValue(PagingRx.getFlowable(new Pager<>(
                new PagingConfig(20),
                () -> new MoviePagingSource(
                        movieApiService,
                        MyApplication.API_KEY,
                        settingsBuilderLiveData.getValue(),
                        favoriteMoviesLiveData.getValue()
                )
        )));
    }


    public LiveData<Flowable<PagingData<Movie>>> getMoviesFlowable() {
        return moviesFlowable;
    }

    public LiveData<List<Movie>> getFavoriteMoviesLiveData() {
        return favoriteMoviesLiveData;
    }

    // Getter for selectedMovieLiveData
    public LiveData<Movie> getSelectedMovieLiveData() {
        return selectedMovieLiveData;
    }

    public MutableLiveData<List<CastMember>> getCastMembersLiveData() {
        return castMembersLiveData;
    }

    public void setSelectedMovie(Movie movie) {
        selectedMovieLiveData.setValue(movie);
        fetchMovieCredits(movie.getId());
    }

    private final MutableLiveData<Movie> movieDetailLiveData = new MutableLiveData<>();

    public MutableLiveData<Movie> getMovieDetailLiveData() {
        return movieDetailLiveData;
    }

    @SuppressLint("CheckResult")
    public void fetchMovieDetail(int movieId) {
        repository.fetchMovieDetails(movieId, MyApplication.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    Log.d(TAG, "fetchMovieDetail: " + movie.getPosterPath());
                    movieDetailLiveData.setValue(movie);
                }, throwable -> {
                    // Handle error
                    Log.e("MoviesViewModel", "Error fetching movie credits", throwable);
                });
    }

    @SuppressLint("CheckResult")
    private void fetchMovieCredits(int movieId) {
        repository.fetchMovieCredits(movieId, MyApplication.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(creditsResponse -> {
                    // Update cast and crew members
                    castMembersLiveData.setValue(creditsResponse.getCast());
                    crewMembersLiveData.setValue(creditsResponse.getCrew());
                }, throwable -> {
                    // Handle error
                    Log.e("MoviesViewModel", "Error fetching movie credits", throwable);
                });
    }

    public LiveData<Integer> getFavoriteCountLiveData() {
        return favoriteCountLiveData;
    }

    // Method to insert a movie into the database and update the in-memory list
    public void insertMovie(Movie movie) {
        repository.insert(movie);
        List<Movie> currentFavorites = favoriteMoviesLiveData.getValue();
        if (currentFavorites != null) {
            if (movie.isFavorite()) {
                // Add to favorites if it's marked as favorite
                boolean alreadyInFavorites = currentFavorites.stream().anyMatch(m -> m.getId() == movie.getId());
                if (!alreadyInFavorites) {
                    currentFavorites.add(movie);
                }
            } else {
                // Remove from favorites if it's not marked as favorite
                currentFavorites.removeIf(m -> m.getId() == movie.getId());
            }
            favoriteMoviesLiveData.setValue(currentFavorites); // Trigger update
        }
        updateFavoriteCount();
    }

    // Method to update a movie and update the in-memory list
    public void updateMovie(Movie movie) {
        repository.update(movie);
        updateFavoriteCount();
    }

    // Method to delete a movie from the database
    public void deleteMovie(Movie movie) {
        repository.deleteMovieById(movie.getId());
        List<Movie> currentFavorites = favoriteMoviesLiveData.getValue();
        if (currentFavorites != null) {
            currentFavorites.removeIf(m -> m.getId() == movie.getId());
            favoriteMoviesLiveData.setValue(currentFavorites); // Trigger update
        }
        updateFavoriteCount();
    }

    private void updateFavoriteCount() {
        List<Movie> favoriteMovies = favoriteMoviesLiveData.getValue();
        if (favoriteMovies != null) {
            favoriteCountLiveData.setValue(favoriteMovies.size());
        } else {
            favoriteCountLiveData.setValue(0);
        }
    }

}
