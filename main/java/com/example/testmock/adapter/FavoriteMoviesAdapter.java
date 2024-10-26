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
import com.example.testmock.databinding.ItemMovieFavoriteBinding;
import com.example.testmock.model.Movie;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMovieViewHolder> {

    private List<Movie> favoriteMovieList;
    private final OnFavoriteClickListener onFavoriteClickListener;
    private final FavoriteMoviesAdapter.OnItemClickListener onItemClickListener;

    public FavoriteMoviesAdapter(List<Movie> favoriteMovieList, OnFavoriteClickListener onFavoriteClickListener, OnItemClickListener onItemClickListener) {
        this.favoriteMovieList = favoriteMovieList;
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFavoriteMovieList(List<Movie> favoriteMovieList) {
        this.favoriteMovieList = favoriteMovieList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMovieFavoriteBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_movie_favorite, parent, false);
        return new FavoriteMovieViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMovieViewHolder holder, int position) {
        Movie movie = favoriteMovieList.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return favoriteMovieList != null ? favoriteMovieList.size() : 0;
    }

    class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieFavoriteBinding binding;

        public FavoriteMovieViewHolder(ItemMovieFavoriteBinding binding) {
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
