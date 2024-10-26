package com.example.testmock.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmock.R;
import com.example.testmock.databinding.ItemMovieBinding;
import com.example.testmock.model.Movie;

import java.util.List;

public class MoviesPagingAdapter extends PagingDataAdapter<Movie, MoviesPagingAdapter.MovieViewHolder> {
    private static final String TAG = "TAGTAGTAG";
    private final MoviesPagingAdapter.OnFavoriteClickListener onFavoriteClickListener;
    private final MoviesPagingAdapter.OnItemClickListener onItemClickListener;

    public MoviesPagingAdapter(MoviesPagingAdapter.OnFavoriteClickListener onFavoriteClickListener, MoviesPagingAdapter.OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMovieBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_movie, parent, false);
        return new MoviesPagingAdapter.MovieViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = getItem(position);
        if (movie != null) {
            holder.bind(movie);
        }
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

    private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
