package com.example.testmock.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmock.R;
import com.example.testmock.databinding.ItemMovieBinding;
import com.example.testmock.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final OnFavoriteClickListener onFavoriteClickListener;
    private final OnItemClickListener onItemClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public MoviesAdapter(List<Movie> movieList, OnFavoriteClickListener onFavoriteClickListener, OnItemClickListener onItemClickListener) {
        this.movieList = movieList;
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMovieBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie) {
            binding.setMovie(movie);
            binding.executePendingBindings();

            ImageView favoriteImageView = binding.favorite;
            favoriteImageView.setImageResource(movie.isFavorite() ? R.drawable.ic_like : R.drawable.ic_dislike);

            favoriteImageView.setOnClickListener(v -> {
                movie.setFavorite(!movie.isFavorite());
                favoriteImageView.setImageResource(movie.isFavorite() ? R.drawable.ic_like : R.drawable.ic_dislike);
                onFavoriteClickListener.onFavoriteClick(movie);
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(movie);
                }
            });
        }
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Movie movie);
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }
}
