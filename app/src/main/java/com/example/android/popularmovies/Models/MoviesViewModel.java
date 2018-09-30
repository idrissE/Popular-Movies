package com.example.android.popularmovies.Models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.database.AppDatabase;

import java.util.List;

public class MoviesViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> movies;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        movies = AppDatabase.getInstance(application.getApplicationContext())
                .movieDAO().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
