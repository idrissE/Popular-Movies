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
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.Review;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.Utils.Constants.MOVIE_THUMBNAIL_BASE_URL;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reviewView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void addAll(List<Review> newReviews) {
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    public void clear() {
        reviews.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(int position) {
            Review currentReview = reviews.get(position);

            TextView reviewAuthorTv = itemView.findViewById(R.id.review_author);
            reviewAuthorTv.setText(currentReview.getAuthor());

            TextView reviewContentTv = itemView.findViewById(R.id.review_content);
            reviewContentTv.setText(currentReview.getContent());
        }
    }
}
