package com.example.mock.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mock.MainActivity;
import com.example.mock.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mock.R;
import com.example.mock.adapter.ListMovieAdapter;
import com.example.mock.database.MovieDatabaseHelper;
import com.example.mock.model.Movie;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.appcompat.widget.SearchView;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListMovieAdapter adapter;
    private MovieDatabaseHelper dbHelper;

    private FrameLayout fragment_containerF;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoFavorites;

    private String selectedCategory;
    private int selectedRate;
    private int selectedYear;
    private String selectedSort;


    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }

    private OnFavoriteChangeListener favoriteChangeListener;


    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private void loadFilterPreferences() {
        selectedCategory = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("category", "Popular Movies");
        selectedRate = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("rate", 0);
        selectedSort = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("sort", "Release Date");

        String num = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("number", "0");
        selectedYear = Integer.parseInt(num);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);
        fragment_containerF = view.findViewById(R.id.fragment_containerF);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        dbHelper = new MovieDatabaseHelper(getContext());

        List<Movie> favoriteMovies = dbHelper.getAllFavoriteMovies();

        adapter = new ListMovieAdapter(favoriteMovies, new ListMovieAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie) {
                recyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                fragment_containerF.setVisibility(View.VISIBLE);

                MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie.getId());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containerF, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }

//            @Override
//            public void onFavoriteClick(Movie movie) {
//                if (favoriteChangeListener != null) {
//                    favoriteChangeListener.onFavoriteChanged();
//                }
//            }
        }, true);

        recyclerView.setAdapter(adapter);

checkNoFavorites(favoriteMovies);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                recyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                fragment_containerF.setVisibility(View.GONE);
                setSwipeRefreshEnabled(true);
            } else {
                recyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                fragment_containerF.setVisibility(View.VISIBLE);
                setSwipeRefreshEnabled(false);
            }
        });

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_favorite, menu);

                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setQueryHint("Search Favorites");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        filterFavoriteMovies(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filterFavoriteMovies(newText);
                        return false;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    private void setSwipeRefreshEnabled(boolean enabled) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enabled);
        }
    }
    private void filterFavoriteMovies(String query) {
        List<Movie> allFavoriteMovies = dbHelper.getAllFavoriteMovies();
        List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : allFavoriteMovies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredMovies.add(movie);
            }
        }



        adapter.updateMovies(filteredMovies);
    }

    public void refreshData() {
        loadFilterPreferences();
        List<Movie> favoriteMovies = dbHelper.getAllFavoriteMovies();
        favoriteMovies.removeIf(movie -> movie.getRating() < selectedRate);

        favoriteMovies.removeIf(movie -> {
            String releaseDate = movie.getReleaseDate();
            int releaseYear = !releaseDate.isEmpty() ? Integer.parseInt(releaseDate.split("-")[0]) : 0;
            return releaseYear < selectedYear;
        });

        if ("Rating".equals(selectedSort)) {
            favoriteMovies.sort((m1, m2) -> Float.compare(m2.getRating(), m1.getRating()));
        } else if ("Release Date".equals(selectedSort)) {
            favoriteMovies.sort((m1, m2) -> {
                String date1 = m1.getReleaseDate();
                String date2 = m2.getReleaseDate();
                return date2.compareTo(date1);
            });
        }
        adapter.updateMovies(favoriteMovies);

        checkNoFavorites(favoriteMovies);

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        fragment_containerF.setVisibility(View.GONE);
        setSwipeRefreshEnabled(true);
        refreshData();
    }

    private void checkNoFavorites(List<Movie> favoriteMovies) {
        if (favoriteMovies.isEmpty()) {
            tvNoFavorites.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}