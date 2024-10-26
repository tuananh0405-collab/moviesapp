package com.example.mock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.database.MovieDatabaseHelper;
import com.example.mock.fragment.FavoriteFragment;
import com.example.mock.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListMovieAdapter extends RecyclerView.Adapter<ListMovieAdapter.ListViewHolder> {

    private List<Movie> movies;
    private OnMovieClickListener listener;
    private Context context;
    private MovieDatabaseHelper dbHelper;

    private boolean isFavoriteMode;


    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
//        void onFavoriteClick(Movie movie);
    }


    public ListMovieAdapter(List<Movie> movies, OnMovieClickListener listener, boolean isFavoriteMode) {
        this.movies = movies;
        this.listener = listener;
        this.isFavoriteMode = isFavoriteMode;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        context = parent.getContext();
        dbHelper = new MovieDatabaseHelper(context);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView releaseDateTextView;
        private TextView ratingTextView;
        private TextView overviewTextView;
        private ImageView posterImageView;
        private ImageView imageViewFavorite;

        public ListViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            releaseDateTextView = itemView.findViewById(R.id.textViewReleaseDate);
            ratingTextView = itemView.findViewById(R.id.textViewRating);
            overviewTextView = itemView.findViewById(R.id.textViewOverview);
            posterImageView = itemView.findViewById(R.id.imageViewPoster);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);

            imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Movie movie = movies.get(getAdapterPosition());

                    if (dbHelper.isFavorite(movie.getId())) {
                        dbHelper.removeFavoriteMovie(movie.getId());
                        if (isFavoriteMode) {
                            removeMovie(movie);
                        }
                        if (context instanceof FavoriteFragment.OnFavoriteChangeListener) {
                            ((FavoriteFragment.OnFavoriteChangeListener) context).onFavoriteChanged();
                        }
                        imageViewFavorite.setImageResource(R.drawable.ic_star);
                        Toast.makeText(context, movie.getTitle() + " removed from favorites!", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addFavoriteMovie(movie);
                        if (context instanceof FavoriteFragment.OnFavoriteChangeListener) {
                            ((FavoriteFragment.OnFavoriteChangeListener) context).onFavoriteChanged();
                        }
                        imageViewFavorite.setImageResource(R.drawable.ic_star_filled);
                        Toast.makeText(context, movie.getTitle() + " added to favorites!", Toast.LENGTH_SHORT).show();
                    }



                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onMovieClick(movies.get(position));
                    }
                }
            });
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            releaseDateTextView.setText("Release date: " + movie.getReleaseDate());
            ratingTextView.setText("Rating: " + movie.getRating() + "/10.0");
            overviewTextView.setText(movie.getOverview());

            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .into(posterImageView);
            if (dbHelper.isFavorite(movie.getId())) {
                imageViewFavorite.setImageResource(R.drawable.ic_star_filled);
            } else {
                imageViewFavorite.setImageResource(R.drawable.ic_star);
            }
        }
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies.clear();
        this.movies.addAll(newMovies);
        notifyDataSetChanged();
    }
    public void removeMovie(Movie movie) {
        int position = movies.indexOf(movie);
        if (position != -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

}
