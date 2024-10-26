package com.example.testmock.pagingsource;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.testmock.api.MovieApiService;
import com.example.testmock.api.model.MovieResponse;
import com.example.testmock.builder.SettingsBuilder;
import com.example.testmock.model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {
    private static final String TAG = "TAGTAGTAG";
    private final MovieApiService apiService;
    private final String apiKey;
    private final SettingsBuilder settingsBuilder;
    private final List<Movie> favoriteMovies;

    public MoviePagingSource(MovieApiService apiService,
                             String apiKey,
                             SettingsBuilder settingsBuilder,
                             List<Movie> favoriteMovies) {
        this.apiService = apiService;
        this.apiKey = apiKey;
        this.settingsBuilder = settingsBuilder;
        this.favoriteMovies = favoriteMovies;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> params) {
        int page = params.getKey() != null ? params.getKey() : 1;
        List<Single<MovieResponse>> apiCalls = new ArrayList<>();
        Log.d(TAG, "loadSingle: " + settingsBuilder.toString());
        for (int i = 0; i < settingsBuilder.getPagesPerLoading(); i++) {
            apiCalls.add(apiService.getMoviesByCategory(
                    settingsBuilder.getMovieCategoryFilter(),
                    apiKey,
                    page + i
            ).subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()));
        }

        return Single.zip(apiCalls, objects -> {
            List<Movie> movies = new ArrayList<>();

            for (Object obj : objects) {
                if (obj instanceof MovieResponse) {
                    MovieResponse response = (MovieResponse) obj;
                    if (response.getResults() != null) {
                        movies.addAll(response.getResults());
                    }
                } else {
                    // Handle unexpected object type
                    throw new IllegalStateException("Unexpected type in Single.zip result");
                }
            }

            // Merge with favorite movies
            if (favoriteMovies != null) {
                for (Movie movie : movies) {
                    for (Movie favorite : favoriteMovies) {
                        if (movie.getId() == favorite.getId()) {
                            movie.setFavorite(true);
                            break;
                        }
                    }
                }
            }

            if ("rating".equals(settingsBuilder.getSortOption())) {
                Collections.sort(movies, Comparator.comparingDouble(Movie::getVoteAverage).reversed());
            } else if ("release_date".equals(settingsBuilder.getSortOption())) {
                Collections.sort(movies, Comparator.comparing(Movie::getReleaseDate).reversed());
            }
            Log.d(TAG, "Loaded movies size after filtering: " + movies.size());
            return new LoadResult.Page<>(movies, page == 1 ? null : page - settingsBuilder.getPagesPerLoading(), movies.isEmpty() ? null : page + settingsBuilder.getPagesPerLoading());
        });
    }


    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> pagingState) {
        return pagingState.getAnchorPosition();
    }
}
