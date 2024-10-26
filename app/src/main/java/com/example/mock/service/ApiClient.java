package com.example.mock.service;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "e7631ffcb8e766993e5ec0c1f4245f93";

    private static volatile Retrofit retrofit;

//    public static Retrofit getInstance() {
//        if (instance == null) {
//            synchronized (ApiClient.class) {
//                if (instance == null) {
//                    instance = new Retrofit.Builder()
//                            .baseUrl(BASE_URL + "?api_key=" + API_KEY)
//                            .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//                }
//            }
//        }
//        return instance;
//    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Tạo một Interceptor để thêm api_key vào mọi request
            Interceptor apiKeyInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    HttpUrl originalUrl = originalRequest.url();

                    // Thêm api_key vào URL
                    HttpUrl urlWithApiKey = originalUrl.newBuilder()
                            .addQueryParameter("api_key", API_KEY)
                            .build();

                    // Tạo request mới với URL đã có api_key
                    Request newRequest = originalRequest.newBuilder()
                            .url(urlWithApiKey)
                            .build();

                    return chain.proceed(newRequest);
                }
            };

            // Tạo OkHttpClient với Interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(apiKeyInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
