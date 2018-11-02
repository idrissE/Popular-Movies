package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.ReviewAdapter;
import com.example.android.popularmovies.Adapters.TrailerAdapter;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.Review;
import com.example.android.popularmovies.Models.ReviewsResponse;
import com.example.android.popularmovies.Models.SingleMovieViewModel;
import com.example.android.popularmovies.Models.SingleMovieViewModelFactory;
import com.example.android.popularmovies.Models.Trailer;
import com.example.android.popularmovies.Models.TrailersResponse;
import com.example.android.popularmovies.Utils.ApiClient;
import com.example.android.popularmovies.Utils.ApiInterface;
import com.example.android.popularmovies.Utils.Constants;
import com.example.android.popularmovies.database.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.Utils.Constants.MOVIE_HEADER_BASE_URL;
import static com.example.android.popularmovies.Utils.Constants.MOVIE_THUMBNAIL_BASE_URL;
import static com.example.android.popularmovies.Utils.Constants.apiKey;

public class DetailActivity extends AppCompatActivity {
    private AppDatabase mDb;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private ImageButton faveBtn;
    private Movie selectedMovie;
    private boolean isFavorite;
    private RecyclerView reviewsRecycler;
    private RecyclerView trailersRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Reviews recycler
        reviewsRecycler = findViewById(R.id.reviews_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        reviewsRecycler.setLayoutManager(layoutManager);
        reviewsRecycler.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        reviewsRecycler.setAdapter(reviewAdapter);

        // Trailers recycler
        trailersRecycler = findViewById(R.id.trailers_recycler);
        RecyclerView.LayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        trailersRecycler.setLayoutManager(trailersLayoutManager);
        trailersRecycler.setHasFixedSize(true);
        trailerAdapter = new TrailerAdapter(this, new ArrayList<Trailer>());
        trailersRecycler.setAdapter(trailerAdapter);

        faveBtn = findViewById(R.id.favorite_btn);
        faveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite)
                    removeMovieFromFavorite(selectedMovie);
                else
                    saveFavoriteMovie(selectedMovie);
            }
        });

        Intent intent = getIntent();
        final int currentMovieId = intent.getIntExtra(Constants.MOVIE_ID_INTENT_KEY, -1);
        checkIfMovieIsFavorite(currentMovieId);
        if (!isConnected()) {
            hideOfflineElements();
            loadMovieFromDb(currentMovieId);
        } else {
            fetchMovieDetails(currentMovieId);
            fetchMovieTrailers(currentMovieId);
            fetchMovieReviews(currentMovieId);
        }
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
     * Hide trailers and reviews in offline mode
     * since they are not saved in the db with other
     * movie details
     */
    private void hideOfflineElements() {
        TextView trailersLabel = findViewById(R.id.trailers_label);
        trailersLabel.setVisibility(View.GONE);
        TextView reviewsLabel = findViewById(R.id.reviews_label);
        reviewsLabel.setVisibility(View.GONE);
        trailersRecycler.setVisibility(View.GONE);
        reviewsRecycler.setVisibility(View.GONE);
    }

    private void populateMovieDetails(Movie movie) {
        ImageView headerPosterImg = findViewById(R.id.header_poster);
        ImageView mainPosterImg = findViewById(R.id.main_poster);

        Picasso.get()
                .load(MOVIE_HEADER_BASE_URL + movie.getPosterPath())
                .error(R.drawable.not_available)
                .into(headerPosterImg);

        Picasso.get()
                .load(MOVIE_THUMBNAIL_BASE_URL + movie.getPosterPath())
                .error(R.drawable.not_available)
                .into(mainPosterImg);

        TextView movieTitleTv = findViewById(R.id.movie_title);
        movieTitleTv.setText(movie.getTitle());

        setTitle(movie.getTitle());

        RatingBar averageVoteRB = findViewById(R.id.vote_average);
        averageVoteRB.setRating(movie.getVoteAverage().floatValue());

        TextView releaseDateTv = findViewById(R.id.release_date);
        releaseDateTv.setText(movie.getReleaseDate());

        TextView overviewTv = findViewById(R.id.overview);
        overviewTv.setText(movie.getOverview());
    }

    private void fetchMovieDetails(final int movieId) {
        ApiInterface apiService = ApiClient.getClient()
                .create(ApiInterface.class);

        Call<Movie> call = apiService.getMovieDetails(movieId, apiKey);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful()) {
                    selectedMovie = response.body();
                    if (selectedMovie != null) {
                        populateMovieDetails(selectedMovie);
                        if (isFavorite)
                            faveBtn.setImageResource(R.drawable.ic_favorite);
                        else
                            faveBtn.setImageResource(R.drawable.ic_favorite_border);
                    }
                } else {
                    displayError(R.string.server_error);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                displayError(R.string.no_internet_error);
            }
        });
    }

    private void fetchMovieTrailers(int movieId) {
        ApiInterface apiService = ApiClient.getClient()
                .create(ApiInterface.class);

        Call<TrailersResponse> call = apiService.getMovieTrailers(movieId, apiKey);
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                if (response.isSuccessful()) {
                    List<Trailer> trailers = response.body().getResults();
                    trailerAdapter.addAll(trailers);
                } else
                    displayError(R.string.server_error);
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                displayError(R.string.no_internet_error);
            }
        });
    }

    private void fetchMovieReviews(int movieId) {
        ApiInterface apiService = ApiClient.getClient()
                .create(ApiInterface.class);

        Call<ReviewsResponse> call = apiService.getMovieReviews(movieId, apiKey);
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    List<Review> reviews = response.body() != null ? response.body().getReviews() : null;
                    reviewAdapter.addAll(reviews);
                } else
                    displayError(R.string.server_error);
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call, Throwable t) {
                displayError(R.string.no_internet_error);
            }
        });
    }

    /**
     * Load movie from database and use the movie observer
     * depending on what's needed
     */
    private void loadAndObserveMovie(int movieId, Observer<Movie> movieObserver) {
        SingleMovieViewModelFactory movieViewModelFactory = new SingleMovieViewModelFactory(mDb, movieId);
        SingleMovieViewModel singleMovieViewModel = ViewModelProviders.of(this, movieViewModelFactory)
                .get(SingleMovieViewModel.class);
        singleMovieViewModel.getMovie().observe(this, movieObserver);
    }


    private void loadMovieFromDb(int movieId) {
        loadAndObserveMovie(movieId, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                populateMovieDetails(movie);
                if (isFavorite)
                    faveBtn.setImageResource(R.drawable.ic_favorite);
                else
                    faveBtn.setImageResource(R.drawable.ic_favorite_border);
            }
        });
    }

    /**
     * Check if the movie is already a favorite movie
     */
    private void checkIfMovieIsFavorite(final int movieId) {
        loadAndObserveMovie(movieId, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                isFavorite = movie != null;
            }
        });
    }

    /**
     * Save Favorite movie to db
     */
    private void saveFavoriteMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().saveMovie(movie);
            }
        });
        faveBtn.setImageResource(R.drawable.ic_favorite);
        isFavorite = true;
        Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete selected movie from favorites db
     */
    private void removeMovieFromFavorite(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().deleteMovie(movie);
            }
        });
        faveBtn.setImageResource(R.drawable.ic_favorite_border);
        isFavorite = false;
        Toast.makeText(this, getString(R.string.remove_success), Toast.LENGTH_SHORT).show();
    }

    /**
     * Display error message based on the type of
     * error that occurred
     */
    private void displayError(int errorId) {
        Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
    }
}
