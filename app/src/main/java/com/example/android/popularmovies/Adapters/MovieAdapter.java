package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.Utils.Mode;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.Utils.Constants.MOVIE_THUMBNAIL_BASE_URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movies;
    private Mode mode;

    public MovieAdapter(Context context, List<Movie> movies, Mode mode) {
        this.context = context;
        this.movies = movies;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View movieView;
        if (mode == Mode.ONLINE)
            movieView = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        else
            movieView = LayoutInflater.from(context).inflate(R.layout.movie_item_offline, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void addAll(List<Movie> newMovies) {
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Movie currentMovie = movies.get(position);
            if (mode == Mode.ONLINE) {
                ImageView moviePoster = itemView.findViewById(R.id.thumbnail);
                Picasso.get()
                        .load(MOVIE_THUMBNAIL_BASE_URL + currentMovie.getPosterPath())
                        .into(moviePoster);
            } else {
                TextView movieTitleTv = itemView.findViewById(R.id.movie_title);
                movieTitleTv.setText(currentMovie.getTitle());

                TextView movieRatingTv = itemView.findViewById(R.id.vote_average);
                movieRatingTv.setText(String.valueOf(currentMovie.getVoteAverage()));
            }
        }

        @Override
        public void onClick(View v) {
            Intent detailIntent = new Intent(context, DetailActivity.class);
            detailIntent.putExtra(Constants.MOVIE_ID_INTENT_KEY, movies.get(getAdapterPosition()).getId());
            context.startActivity(detailIntent);
        }
    }
}
