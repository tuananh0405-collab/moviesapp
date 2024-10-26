package com.example.testmock.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.testmock.api.MovieApiService;
import com.example.testmock.repository.MovieRepository;

public class MoviesViewModelFactory implements ViewModelProvider.Factory {

    private final MovieRepository repository;
    private final MovieApiService movieApiService;

    public MoviesViewModelFactory(MovieRepository repository, MovieApiService movieApiService) {
        this.repository = repository;
        this.movieApiService = movieApiService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MoviesViewModel.class)) {
            return (T) new MoviesViewModel(repository, movieApiService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
