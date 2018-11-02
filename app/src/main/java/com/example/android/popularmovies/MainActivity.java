package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.Models.Category;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MoviesResponse;
import com.example.android.popularmovies.Utils.ApiClient;
import com.example.android.popularmovies.Utils.ApiInterface;
import com.example.android.popularmovies.Utils.Mode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.Utils.Constants.apiKey;

public class MainActivity extends AppCompatActivity {
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
            fetchMovies();
        } else {
            progressBar.setVisibility(View.GONE);
            displayError(R.string.no_internet_error);
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

        moviesAdapter = new MovieAdapter(this, new ArrayList<Movie>(), Mode.ONLINE);
        moviesRecycler.setAdapter(moviesAdapter);

        moviesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // check scroll down
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && TOTAL_PAGES_COUNT > CURRENT_PAGE + 1) {
                        CURRENT_PAGE++;
                        fetchMovies();
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

        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
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
            fetchMovies();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CURRENT_PAGE = 1;
    }

    private void fetchMovies() {
        ApiInterface apiService = ApiClient.getClient()
                .create(ApiInterface.class);
        Call<MoviesResponse> call;
        if (category == Category.TOP_RATED)
            call = apiService.getTopRatedMovies(apiKey, CURRENT_PAGE);
        else
            call = apiService.getPopularMovies(apiKey, CURRENT_PAGE);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    TOTAL_PAGES_COUNT = response.body() != null ? response.body().getTotal_pages() : 0;
                    List<Movie> movies = response.body().getResults();
                    progressBar.setVisibility(View.GONE);
                    if (movies != null && !movies.isEmpty())
                        moviesAdapter.addAll(movies);
                } else {
                    displayError(R.string.server_error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                displayError(R.string.no_internet_error);
            }
        });
    }

    /**
     * Display error message based on the type of
     * error that occurred
     */
    private void displayError(int errorId) {
        errorMsgTv.setText(errorId);
        errorMsgTv.setVisibility(View.VISIBLE);
    }
}
