package com.example.android.popularmovies.Utils;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/popular")
    Call<Response> getPopularMovie(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<Response> getTopRatedMovies(@Query("api_key") String apiKey);
}
