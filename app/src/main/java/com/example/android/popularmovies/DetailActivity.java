package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Movie currentMovie = intent.getParcelableExtra("CURRENT_MOVIE");
        populateMovieDetails(currentMovie);
    }

    private void populateMovieDetails(Movie movie) {
        ImageView headerPosterImg = findViewById(R.id.header_poster);
        ImageView mainPosterImg = findViewById(R.id.main_poster);

        Picasso.get()
                .load(MOVIE_POSTER_BASE_URL + "w500" + movie.getPosterPath())
                .into(headerPosterImg);

        Picasso.get()
                .load(MOVIE_POSTER_BASE_URL + "w185" + movie.getPosterPath())
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
}
