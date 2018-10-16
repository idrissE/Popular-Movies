package com.example.android.popularmovies.Models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.popularmovies.database.AppDatabase;

public class SingleMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final int movieId;

    public SingleMovieViewModelFactory(AppDatabase mDb, int movieId) {
        this.mDb = mDb;
        this.movieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new SingleMovieViewModel(mDb, movieId);
    }
}
