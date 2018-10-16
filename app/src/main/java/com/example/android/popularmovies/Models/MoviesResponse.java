package com.example.android.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse {
    @SerializedName("total_pages")
    private int total_pages;
    @SerializedName("results")
    private List<Movie> results;

    public int getTotal_pages() {
        return total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }
}
