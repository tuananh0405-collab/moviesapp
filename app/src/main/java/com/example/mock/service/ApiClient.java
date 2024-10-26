package com.example.mock.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static volatile Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return instance;
    }
}
