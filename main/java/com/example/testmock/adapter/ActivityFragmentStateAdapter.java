package com.example.testmock.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.testmock.navhost.AboutNavHostFragment;
import com.example.testmock.navhost.ListFavoriteMoviesNavHostFragment;
import com.example.testmock.navhost.ListMoviesNavHostFragment;
import com.example.testmock.navhost.SettingsNavHostFragment;

import java.util.HashMap;

public class ActivityFragmentStateAdapter extends FragmentStateAdapter {
    public HashMap<Integer, MutableLiveData<NavController>> navControllerMap = new HashMap<>();

    public ActivityFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HashMap<Integer, MutableLiveData<NavController>> getNavControllerMap() {
        return navControllerMap;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                ListMoviesNavHostFragment listMoviesNavHostFragment = new ListMoviesNavHostFragment();
                listMoviesNavHostFragment.getViewLifecycleOwnerLiveData().observeForever(lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        navControllerMap.put(position, listMoviesNavHostFragment.getListMovieNavController());
                    }
                });
                return listMoviesNavHostFragment;
            case 1:
                ListFavoriteMoviesNavHostFragment listFavoriteMoviesNavHostFragment = new ListFavoriteMoviesNavHostFragment();
                listFavoriteMoviesNavHostFragment.getViewLifecycleOwnerLiveData().observeForever(lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        navControllerMap.put(position, listFavoriteMoviesNavHostFragment.getListFavoriteMovieNavController());
                    }
                });
                return listFavoriteMoviesNavHostFragment;
            case 2:
                SettingsNavHostFragment settingsNavHostFragment = new SettingsNavHostFragment();
                settingsNavHostFragment.getViewLifecycleOwnerLiveData().observeForever(lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        navControllerMap.put(position, settingsNavHostFragment.getSettingsNavController());
                    }
                });
                return settingsNavHostFragment;
            case 3:
                AboutNavHostFragment aboutNavHostFragment = new AboutNavHostFragment();
                aboutNavHostFragment.getViewLifecycleOwnerLiveData().observeForever(lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        navControllerMap.put(position, aboutNavHostFragment.getAboutNavController());
                    }
                });
                return aboutNavHostFragment;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
