package com.example.testmock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.testmock.R;
import com.example.testmock.adapter.FavoriteMoviesAdapter;
import com.example.testmock.adapter.MoviesAdapter;
import com.example.testmock.databinding.FragmentListFavoriteMoviesBinding;
import com.example.testmock.model.Movie;
import com.example.testmock.viewmodel.MoviesViewModel;

import java.util.ArrayList;

public class ListFavoriteMoviesFragment extends Fragment {
    private FragmentListFavoriteMoviesBinding binding;
    private MoviesViewModel viewModel;
    private FavoriteMoviesAdapter favoriteMoviesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListFavoriteMoviesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MoviesViewModel.class);
        favoriteMoviesAdapter = new FavoriteMoviesAdapter(new ArrayList<>(), this::onFavoriteClick, this::onItemClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(favoriteMoviesAdapter);
        // Observe favorite movies LiveData
        viewModel.getFavoriteMoviesLiveData().observe(getViewLifecycleOwner(), favoriteMovies -> {
            if (favoriteMovies != null) {
                favoriteMoviesAdapter.setFavoriteMovieList(favoriteMovies);
            }
        });
    }

    private void onFavoriteClick(Movie movie) {
        if (movie.isFavorite()) {
            viewModel.insertMovie(movie);
        } else {
            viewModel.deleteMovie(movie);
        }
    }

    private void onItemClick(Movie movie) {
        viewModel.setSelectedMovie(movie);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.movieDetailFragment);
    }

}
