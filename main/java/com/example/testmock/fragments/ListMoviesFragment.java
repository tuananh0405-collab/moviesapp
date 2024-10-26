package com.example.testmock.fragments;

import android.annotation.SuppressLint;
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
import com.example.testmock.adapter.MoviesPagingAdapter;
import com.example.testmock.databinding.FragmentListMoviesBinding;
import com.example.testmock.model.Movie;
import com.example.testmock.viewmodel.MoviesViewModel;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListMoviesFragment extends Fragment {
    public FragmentListMoviesBinding binding;
    private MoviesViewModel viewModel;
    private MoviesPagingAdapter moviesPagingAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListMoviesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MoviesViewModel.class);
        moviesPagingAdapter = new MoviesPagingAdapter(this::onFavoriteClick, this::onItemClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Set Adapter to RecyclerView
        binding.recyclerView.setAdapter(moviesPagingAdapter);
        // Observe filtered and sorted movies LiveData
        // Observe the Flowable and submit data to adapter
        viewModel.getMoviesFlowable().observe(getViewLifecycleOwner(), flowable -> {
            flowable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pagingData -> {
                        moviesPagingAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
                    }, throwable -> {
                        // Handle errors here
                        throwable.printStackTrace();
                    });
        });
    }

    private void onFavoriteClick(Movie movie) {
        if (movie.isFavorite()) {
            viewModel.insertMovie(movie);
        } else {
            viewModel.deleteMovie(movie);
            viewModel.updateMovie(movie);
        }
    }

    private void onItemClick(Movie movie) {
        viewModel.setSelectedMovie(movie);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.movieDetailFragment);
    }
}
