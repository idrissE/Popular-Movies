package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MoviesViewModel;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setupViewModel();
    }

    private void setupViewModel() {
        MoviesViewModel moviesViewModel = new MoviesViewModel(getApplication());
        moviesViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (!movies.isEmpty()) {
                    for (Movie movie : movies)
                        Log.v("Movie", movie.getTitle());
                }
            }
        });
    }
}
