package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.BuildConfig;

public class Constants {
    public static final String apiKey = BuildConfig.ApiKey;

    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";

    public static final String MOVIE_THUMBNAIL_BASE_URL = MOVIE_POSTER_BASE_URL + "w185";

    public static final String MOVIE_HEADER_BASE_URL = MOVIE_POSTER_BASE_URL + "w500";

    public static final String MOVIE_ID_INTENT_KEY = "CURRENT_MOVIE";

}
