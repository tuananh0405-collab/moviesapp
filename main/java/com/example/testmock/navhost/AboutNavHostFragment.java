package com.example.testmock.navhost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.testmock.R;
import com.example.testmock.databinding.FragmentNavhostAboutBinding;

public class AboutNavHostFragment extends Fragment {
    private FragmentNavhostAboutBinding binding;
    public MutableLiveData<NavController> aboutNavController = new MutableLiveData<>();
    private NavController navController = null;
    private final int nestedNavHostFragmentId = R.id.nested_nav_host_fragment_about;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNavhostAboutBinding.inflate(inflater, container, false);
        NavHostFragment nestedNavHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(nestedNavHostFragmentId);
        if (nestedNavHostFragment != null) {
            navController = nestedNavHostFragment.getNavController();
        }
        aboutNavController.setValue(navController);
        return binding.getRoot();
    }

    public MutableLiveData<NavController> getAboutNavController() {
        return aboutNavController;
    }
}
