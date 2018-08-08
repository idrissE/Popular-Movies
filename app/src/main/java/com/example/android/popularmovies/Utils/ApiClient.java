package com.example.android.popularmovies.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return retrofit;
    }
}
