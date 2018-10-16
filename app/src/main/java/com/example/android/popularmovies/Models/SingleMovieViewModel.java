package com.example.android.popularmovies.Models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.database.AppDatabase;

public class SingleMovieViewModel extends ViewModel {
    private LiveData<Movie> movie;

    public SingleMovieViewModel(AppDatabase db, int id) {
        movie = db.movieDAO().getMovieById(id);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
