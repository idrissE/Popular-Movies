package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.database.AppDatabase;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Movie currentMovie = intent.getParcelableExtra("CURRENT_MOVIE");
        populateMovieDetails(currentMovie);

        ImageButton faveBtn = findViewById(R.id.favorite_btn);
        faveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = currentMovie.getId();
                String title = currentMovie.getTitle();
                Movie movie = new Movie(id, title);
                saveFavoriteMovie(movie);
            }
        });
    }

    private void populateMovieDetails(Movie movie) {
        ImageView headerPosterImg = findViewById(R.id.header_poster);
        ImageView mainPosterImg = findViewById(R.id.main_poster);

        Picasso.get()
                .load(MOVIE_POSTER_BASE_URL + "w500" + movie.getPosterPath())
                .error(R.drawable.not_available)
                .into(headerPosterImg);

        Picasso.get()
                .load(MOVIE_POSTER_BASE_URL + "w185" + movie.getPosterPath())
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

    private void saveFavoriteMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().saveMovie(movie);
            }
        });
    }
}
