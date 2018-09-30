package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.Models.Category;
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
    private static final int GRID_COLUMNS_NUMBER = 2;
    private MovieAdapter moviesAdapter;
    private static Category category = Category.MOST_POPULAR;
    private TextView errorMsgTv;
    private ProgressBar progressBar;

    private static int CURRENT_PAGE = 1;

    private static int TOTAL_PAGES_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorMsgTv = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);

        if (isConnected()) {
            initMoviesRecycler();
            getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            errorMsgTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedCategoryId = item.getItemId();
        switch (selectedCategoryId) {
            case R.id.action_most_popular:
                changeToCategory(Category.MOST_POPULAR);
                return true;
            case R.id.action_top_rated:
                changeToCategory(Category.TOP_RATED);
                return true;
            case R.id.action_favorite:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Configure the movies'RecyclerView
     */
    private void initMoviesRecycler() {
        RecyclerView moviesRecycler = findViewById(R.id.movie_recycler);
        moviesRecycler.setHasFixedSize(true);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS_NUMBER);
        moviesRecycler.setLayoutManager(layoutManager);

        moviesAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        moviesRecycler.setAdapter(moviesAdapter);

        moviesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // check scroll down
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && TOTAL_PAGES_COUNT > CURRENT_PAGE + 1) {
                        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, MainActivity.this);
                        CURRENT_PAGE++;
                    }
                }
            }
        });
    }

    /**
     * Check if the phone is connected to internet
     */
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * Switch to another movies'category
     */
    private void changeToCategory(Category newCategory) {
        if (isConnected()) {
            category = newCategory;
            CURRENT_PAGE = 1;
            moviesAdapter.clear();
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CURRENT_PAGE = 1;
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MoviesLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        progressBar.setVisibility(View.GONE);
        Log.v("total", String.valueOf(CURRENT_PAGE));
        if (movies != null && !movies.isEmpty()) {
            moviesAdapter.addAll(movies);
        } else
            errorMsgTv.setVisibility(View.VISIBLE);
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
            Call<MovieResponse> call;
            if (category == Category.TOP_RATED)
                call = apiService.getTopRatedMovies(apiKey, CURRENT_PAGE);
            else
                call = apiService.getPopularMovies(apiKey, CURRENT_PAGE);
            try {
                Response<MovieResponse> response = call.execute();
                TOTAL_PAGES_COUNT = response.body() != null ? response.body().getTotal_pages() : 0;
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
