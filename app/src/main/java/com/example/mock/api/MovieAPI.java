package com.example.mock.api;

import com.example.mock.model.Movie;
import com.example.mock.model.response.CastResponse;
import com.example.mock.model.response.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<CastResponse> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

}

