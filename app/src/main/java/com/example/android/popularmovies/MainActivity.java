package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MovieResponse;
import com.example.android.popularmovies.Utils.ApiClient;
import com.example.android.popularmovies.Utils.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final String apiKey = BuildConfig.ApiKey;
    private static final int MOVIES_LOADER_ID = 1;
    private static final int GRID_COLUMNS_NUMBER = 3;
    private MovieAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMoviesRecycler();
        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    /**
     * Configure the movies'RecyclerView
     */
    private void initMoviesRecycler() {
        RecyclerView moviesRecycler = findViewById(R.id.movie_recycler);
        moviesRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS_NUMBER);
        moviesRecycler.setLayoutManager(layoutManager);
        moviesAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        moviesRecycler.setAdapter(moviesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MoviesLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies != null && !movies.isEmpty())
            moviesAdapter.addAll(movies);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        moviesAdapter.clear();
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
                return response.body().getResults();
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
