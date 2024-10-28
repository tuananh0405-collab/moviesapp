package com.example.mock.api;

import com.example.mock.model.Movie;
import com.example.mock.model.response.CastResponse;
import com.example.mock.model.response.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {

//    @GET("movie/{movie_id}")
//    Call<Movie> getMovieDetails(
//            @Path("movie_id") int movieId,
//            @Query("api_key") String apiKey
//    );

    @GET("movie/{movie_id}?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId
    );

//    @GET("movie/{movie_id}/credits")
//    Call<CastResponse> getMovieCredits(
//            @Path("movie_id") int movieId,
//            @Query("api_key") String apiKey
//    );

    @GET("movie/{movie_id}/credits?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Call<CastResponse> getMovieCredits(
            @Path("movie_id") int movieId
    );


//    @GET("movie/{category}")
//    Call<MovieResponse> getMoviesByCategory(
//            @Path("category") String category,
//            @Query("page") int page
//    );
    @GET("movie/{category}?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Call<MovieResponse> getMoviesByCategory(
            @Path("category") String category,
            @Query("page") int page
    );
}

