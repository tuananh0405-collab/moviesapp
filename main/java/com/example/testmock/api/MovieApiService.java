package com.example.testmock.api;

import com.example.testmock.api.model.CreditsResponse;
import com.example.testmock.api.model.MovieResponse;
import com.example.testmock.model.Movie;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("movie/{category}")
    Single<MovieResponse> getMoviesByCategory(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Single<Movie> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Single<CreditsResponse> getMovieCredits(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
