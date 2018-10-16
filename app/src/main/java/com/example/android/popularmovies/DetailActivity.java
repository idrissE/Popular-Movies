package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.ReviewAdapter;
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
    private ImageButton faveBtn;
    private Movie selectedMovie;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView reviewsRecycler = findViewById(R.id.reviews_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        reviewsRecycler.setLayoutManager(layoutManager);
        reviewsRecycler.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        reviewsRecycler.setAdapter(reviewAdapter);

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
        fetchMovieDetails(currentMovieId);
        fetchMovieTrailers(currentMovieId);
        fetchMovieReviews(currentMovieId);
    }

    private void loadMovieFromDb(int movieId) {
        SingleMovieViewModelFactory movieViewModelFactory = new SingleMovieViewModelFactory(mDb, movieId);
        SingleMovieViewModel singleMovieViewModel = ViewModelProviders.of(this, movieViewModelFactory)
                .get(SingleMovieViewModel.class);
        singleMovieViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                populateMovieDetails(movie);
            }
        });
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
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                selectedMovie = response.body();
                populateMovieDetails(selectedMovie);
                if (isFavorite)
                    faveBtn.setImageResource(R.drawable.ic_favorite);
                else
                    faveBtn.setImageResource(R.drawable.ic_favorite_border);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

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
                List<Trailer> trailers = response.body().getResults();
                for (Trailer trailer : trailers)
                    Log.v("Trailer", trailer.getName());
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {

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
                List<Review> reviews = response.body() != null ? response.body().getReviews() : null;
                reviewAdapter.addAll(reviews);
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Check if the movie is already a favorite movie
     */
    private void checkIfMovieIsFavorite(final int movieId) {
        SingleMovieViewModelFactory singleMovieViewModelFactory = new SingleMovieViewModelFactory(mDb, movieId);
        SingleMovieViewModel movieViewModel = ViewModelProviders.of(this, singleMovieViewModelFactory)
                .get(SingleMovieViewModel.class);
        movieViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                isFavorite = movie != null;
            }
        });
    }

    private void saveFavoriteMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().saveMovie(movie);
            }
        });
        faveBtn.setImageResource(R.drawable.ic_favorite);
        isFavorite = true;
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
    }
}
