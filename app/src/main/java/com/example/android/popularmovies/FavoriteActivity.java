package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MoviesViewModel;
import com.example.android.popularmovies.Utils.Mode;
import com.example.android.popularmovies.Adapters.MovieOfflineDivider;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private static final int GRID_COLUMNS_NUMBER = 2;
    private MovieAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewModel();
    }

    /**
     * Check if the phone is connected to internet
     */
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    private void setupViewModel() {
        MoviesViewModel moviesViewModel = new MoviesViewModel(getApplication());
        moviesViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                ProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);
                initMoviesRecycler(movies);
            }
        });
    }

    /**
     * Configure the movies'RecyclerView
     */
    private void initMoviesRecycler(List<Movie> movies) {
        RecyclerView moviesRecycler = findViewById(R.id.movie_recycler);
        moviesRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        Mode mode;
        if (isConnected()) {
            mode = Mode.ONLINE;
            layoutManager = new GridLayoutManager(this, GRID_COLUMNS_NUMBER);
        } else {
            mode = Mode.OFFLINE;
            layoutManager = new LinearLayoutManager(this);
            MovieOfflineDivider divider = new MovieOfflineDivider(this);
            moviesRecycler.addItemDecoration(divider);
        }

        moviesRecycler.setLayoutManager(layoutManager);
        moviesAdapter = new MovieAdapter(this, movies, mode);
        moviesRecycler.setAdapter(moviesAdapter);
    }
}
