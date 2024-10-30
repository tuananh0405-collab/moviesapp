package com.example.mock.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.adapter.GridMovieAdapter;
import com.example.mock.adapter.ListMovieAdapter;
import com.example.mock.api.MovieAPI;
import com.example.mock.model.Movie;
import com.example.mock.model.response.MovieResponse;
import com.example.mock.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {

    private ProgressBar mProgressBar;
    private ProgressBar progressBarLoadMore;
    private RecyclerView recyclerView;
    private ListMovieAdapter listMovieAdapter;
    private GridMovieAdapter gridMovieAdapter;
    private List<Movie> movieList = new ArrayList<>();
    private boolean isGridView = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;

    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private static final String PREFS_NAME = "MovieSettings";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_RATE = "rate";
    private static final String KEY_YEAR = "year";
    private static final String KEY_SORT = "sort";

    private String selectedCategory;
    private int selectedRate;
    private int selectedYear;
    private String selectedSort;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int LOAD_MORE_DELAY = 2000;

    public MoviesFragment() {
    }

    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mProgressBar = view.findViewById(R.id.idPBLoading);
        progressBarLoadMore = view.findViewById(R.id.idPBLoadMore);
        recyclerView = view.findViewById(R.id.recyclerViewMovies);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        tvNoData = view.findViewById(R.id.tvNoData);


        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);

        listMovieAdapter = new ListMovieAdapter(movieList, this::openMovieDetail, false);

        gridMovieAdapter = new GridMovieAdapter(movieList, this::openMovieDetail);

        recyclerView.setAdapter(listMovieAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        loadMovies(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !isLoading && !isLastPage) {
                    currentPage++;
                    progressBarLoadMore.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMovies(currentPage);
                        }
                    }, LOAD_MORE_DELAY);
                }
            }
        });

        if (movieList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                recyclerView.setVisibility(View.VISIBLE);
                setSwipeRefreshEnabled(true);
            } else {
                recyclerView.setVisibility(View.GONE);
                setSwipeRefreshEnabled(false);
            }
        });

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_actions, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.viewType) {
                    toggleViewType(menuItem);
                    return true;
                } else if (menuItem.getItemId() == R.id.popularMovies) {
                    selectedCategory = "Popular Movies";
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(KEY_CATEGORY, selectedCategory);
                    editor.apply();
                    loadMovies(currentPage);
                } else if (menuItem.getItemId() == R.id.topRatedMovies) {
                    selectedCategory = "Top Rated Movies";
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(KEY_CATEGORY, selectedCategory);
                    editor.apply();
                    loadMovies(currentPage);
                } else if (menuItem.getItemId() == R.id.upcomingMovies) {
                    selectedCategory = "Upcoming Movies";
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(KEY_CATEGORY, selectedCategory);
                    editor.apply();
                    loadMovies(currentPage);
                } else if (menuItem.getItemId() == R.id.nowPlayingMovies) {
                    selectedCategory = "Now Playing Movies";
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(KEY_CATEGORY, selectedCategory);
                    editor.apply();
                    loadMovies(currentPage);
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void openMovieDetail(Movie movie) {
        recyclerView.setVisibility(View.GONE);
        MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie.getId());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container2, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setSwipeRefreshEnabled(boolean enabled) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enabled);
        }
    }

//    private void loadFilterPreferences() {
//        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        selectedCategory = preferences.getString(KEY_CATEGORY, "Popular Movies");
//        selectedRate = preferences.getInt(KEY_RATE, 0);
//        selectedYear = preferences.getInt(KEY_YEAR, 2000);
//        selectedSort = preferences.getString(KEY_SORT, "Release Date");
//    }

    private void loadFilterPreferences() {
        selectedCategory = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("category", "Popular Movies");
        selectedRate = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("rate", 0);
//    selectedYear = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("year", 2000);
        selectedSort = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("sort", "Release Date");

        String num =  PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("number","0");
        Log.d("TAG", num);
        selectedYear = Integer.parseInt(num);
    }

    private void loadMovies(int page) {
        loadFilterPreferences();

        if (page == 1) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            progressBarLoadMore.setVisibility(View.VISIBLE);
        }

        isLoading = true;

        MovieAPI movieApi = ApiClient.getInstance().create(MovieAPI.class);
        String categoryPath;
//        Call<MovieResponse> call;

        switch (selectedCategory) {
            case "Top Rated Movies":
                categoryPath = "top_rated";
//                call = movieApi.getTopRatedMovies("e7631ffcb8e766993e5ec0c1f4245f93", page);
                break;
            case "Upcoming Movies":
                categoryPath = "upcoming";
//                call = movieApi.getUpcomingMovies("e7631ffcb8e766993e5ec0c1f4245f93", page);
                break;
            case "Now Playing Movies":
                categoryPath = "now_playing";
//                call = movieApi.getNowPlayingMovies("e7631ffcb8e766993e5ec0c1f4245f93", page);
                break;
            default:
                categoryPath = "popular";
//                call = movieApi.getPopularMovies("e7631ffcb8e766993e5ec0c1f4245f93", page);
                break;
        }

        Call<MovieResponse> call = movieApi.getMoviesByCategory(categoryPath, page);


        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                progressBarLoadMore.setVisibility(View.GONE);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> newMovies = response.body().getMovies();
                    if (newMovies.isEmpty()) {
                        isLastPage = true;
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    } else {
                        tvNoData.setVisibility(View.GONE); // Ẩn "No Data" nếu có dữ liệu

                        if (page == 1) {
                            movieList.clear();
                        }
                        movieList.addAll(newMovies);
                        filterAndSortMovies();
                        if (listMovieAdapter == null) {
                            listMovieAdapter = new ListMovieAdapter(movieList, movie -> {
                                recyclerView.setVisibility(View.GONE);
                                MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie.getId());
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container2, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }, false);
                            recyclerView.setAdapter(listMovieAdapter);
                        } else {
                            listMovieAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                progressBarLoadMore.setVisibility(View.GONE);
                isLoading = false;
            }
        });
    }

    private void filterAndSortMovies() {
        movieList.removeIf(movie -> movie.getRating() < selectedRate);

        movieList.removeIf(movie -> {
            String releaseDate = movie.getReleaseDate();
            int releaseYear = !releaseDate.isEmpty() ? Integer.parseInt(releaseDate.split("-")[0]) : 0;
            return releaseYear < selectedYear;
        });

        if ("Rating".equals(selectedSort)) {
            movieList.sort((m1, m2) -> Float.compare(m2.getRating(), m1.getRating()));
        } else if ("Release Date".equals(selectedSort)) {
            movieList.sort((m1, m2) -> {
                String date1 = m1.getReleaseDate();
                String date2 = m2.getReleaseDate();
                return date2.compareTo(date1);
            });
        }
    }

    private void refreshData() {
        currentPage = 1;
        isLastPage = false;
        loadMovies(currentPage);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void toggleViewType(MenuItem menuItem) {
        if (!isGridView) {
            recyclerView.setLayoutManager(gridLayoutManager);
            isGridView = true;
            recyclerView.setAdapter(gridMovieAdapter);
            menuItem.setIcon(R.drawable.ic_list);
        } else {
            recyclerView.setLayoutManager(linearLayoutManager);
            isGridView = false;
            recyclerView.setAdapter(listMovieAdapter);
            menuItem.setIcon(R.drawable.ic_grid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMovies(currentPage);
    }
}
