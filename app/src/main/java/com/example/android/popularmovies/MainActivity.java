package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MovieResponse;
import com.example.android.popularmovies.Utils.ApiClient;
import com.example.android.popularmovies.Utils.ApiInterface;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final String apiKey = BuildConfig.ApiKey;
    private static final int MOVIES_LOADER_ID = 1;
    private RecyclerView moviesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecycler = findViewById(R.id.movie_recycler);
        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MoviesLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    private static class MoviesLoader extends AsyncTaskLoader<List<Movie>> {
        MoviesLoader(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            ApiInterface apiService = ApiClient.getClient()
                    .create(ApiInterface.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(apiKey);
            try {
                Response<MovieResponse> response = call.execute();
                return response.body() != null ? response.body().getResults() : null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }
}
