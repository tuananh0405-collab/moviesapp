package com.example.mock.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mock.fragment.AboutFragment;
import com.example.mock.fragment.FavoriteFragment;
import com.example.mock.fragment.MovieDetailFragment;
import com.example.mock.fragment.MoviesFragment;
import com.example.mock.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String movieId;


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (movieId != null && position == 0) {
            return MovieDetailFragment.newInstance(Integer.parseInt(movieId));
        }

        switch (position) {
            case 0:
                return new MoviesFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new SettingFragment();
            case 3:
                return new AboutFragment();
        }
        return new MoviesFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
