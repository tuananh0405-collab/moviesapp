package com.example.testmock.application;

import android.app.Application;

import com.example.testmock.api.MovieApiService;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "e7631ffcb8e766993e5ec0c1f4245f93";

    private MovieApiService movieApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        movieApiService = retrofit.create(MovieApiService.class);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public MovieApiService getMovieApiService() {
        return movieApiService;
    }
}
