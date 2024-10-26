package com.example.testmock.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.testmock.R;
import com.example.testmock.builder.SettingsBuilder;
import com.example.testmock.viewmodel.MoviesViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    private MoviesViewModel viewModel;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        viewModel = new ViewModelProvider(requireActivity()).get(MoviesViewModel.class);
        // Filter by movie category
        ListPreference filterCategoryPreference = findPreference("filter_movie_category");
        if (filterCategoryPreference != null) {
            filterCategoryPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String filterValue = (String) newValue;
                updateSettings(filterValue, null, null);
                return true;
            });
        }

        // Sort by option
        ListPreference sortOptionPreference = findPreference("sort_option");
        if (sortOptionPreference != null) {
            sortOptionPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String sortValue = (String) newValue;
                updateSettings(null, sortValue, null);
                return true;
            });
        }

        // Number of pages per loading
        EditTextPreference pagesPerLoadingPreference = findPreference("pages_per_loading");
        if (pagesPerLoadingPreference != null) {
            pagesPerLoadingPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String pagesValue = (String) newValue;
                int pagesPerLoading = Integer.parseInt(pagesValue);
                updateSettings(null, null, pagesPerLoading);
                return true;
            });
        }
    }

    private void updateSettings(@Nullable String filterCategory, @Nullable String sortOption, @Nullable Integer pagesPerLoading) {
        // Retrieve current settings
        SettingsBuilder currentSettings = viewModel.getSettingsBuilderLiveData().getValue();

        // Update settings only if the value is provided (non-null)
        if (currentSettings != null) {
            if (filterCategory != null) {
                currentSettings.setMovieCategoryFilter(filterCategory);
            }
            if (sortOption != null) {
                currentSettings.setSortOption(sortOption);
            }
            if (pagesPerLoading != null) {
                currentSettings.setPagesPerLoading(pagesPerLoading);
            }

            // Apply updated settings
            viewModel.updateSettings(
                    currentSettings.getMovieCategoryFilter(),
                    currentSettings.getSortOption(),
                    currentSettings.getPagesPerLoading()
            );
        }
    }
}
