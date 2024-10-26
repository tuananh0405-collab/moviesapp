package com.example.mock.model.response;

import com.example.mock.model.Movie;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }
}
