package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Models.Trailer;
import com.example.android.popularmovies.R;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private Context context;
    private List<Trailer> trailers;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reviewView = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void addAll(List<Trailer> newReviews) {
        trailers.addAll(newReviews);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Trailer currentTrailer = trailers.get(position);

            TextView reviewAuthorTv = itemView.findViewById(R.id.trailer_name);
            reviewAuthorTv.setText(currentTrailer.getName());
        }

        @Override
        public void onClick(View view) {
            int currentTrailerPosition = getAdapterPosition();
            Trailer currentTrailer = trailers.get(currentTrailerPosition);
            String youtubeUrl = "https://www.youtube.com/watch?v=" + currentTrailer.getKey();
            Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            if (playTrailerIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(playTrailerIntent);
            }
        }
    }
}
