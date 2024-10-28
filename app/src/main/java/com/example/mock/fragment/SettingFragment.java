package com.example.mock.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mock.MainActivity;
import com.example.mock.R;

public class SettingFragment extends Fragment {
    private LinearLayout categoryLayout, rateLayout, yearLayout, sortLayout;
    private TextView categoryTextView, rateTextView, yearTextView, sortTextView;

    private static final String PREFS_NAME = "MovieSettings";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_RATE = "rate";
    private static final String KEY_YEAR = "year";
    private static final String KEY_SORT = "sort";


    public SettingFragment() {
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        categoryLayout = view.findViewById(R.id.category_layout);
        rateLayout = view.findViewById(R.id.rate_layout);
        yearLayout = view.findViewById(R.id.year_layout);
        sortLayout = view.findViewById(R.id.sort_layout);

        categoryTextView = view.findViewById(R.id.category_text);
        rateTextView = view.findViewById(R.id.rate_text);
        yearTextView = view.findViewById(R.id.year_text);
        sortTextView = view.findViewById(R.id.sort_text);

        loadPreferences();

        categoryLayout.setOnClickListener(v -> showCategoryDialog());
        rateLayout.setOnClickListener(v -> showRateDialog());
        yearLayout.setOnClickListener(v -> showYearDialog());
        sortLayout.setOnClickListener(v -> showSortDialog());

        return view;
    }

    private void loadPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        categoryTextView.setText(preferences.getString(KEY_CATEGORY, "Popular Movies"));
        rateTextView.setText(String.valueOf(preferences.getInt(KEY_RATE, 0)));
        yearTextView.setText(String.valueOf(preferences.getInt(KEY_YEAR, 2000)));
        sortTextView.setText(preferences.getString(KEY_SORT, "Release Date"));
    }

    private void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void savePreferences(String key, int value) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void showCategoryDialog() {
        String[] categories = {"Popular Movies", "Top Rated Movies", "Upcoming Movies", "Now Playing Movies"};
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentCategory = preferences.getString(KEY_CATEGORY, "Popular Movies");
        int checkedItem = -1;
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentCategory)) {
                checkedItem = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Category")
                .setSingleChoiceItems(categories, checkedItem, (dialog, which) -> {
                    categoryTextView.setText(categories[which]);
                    savePreferences(KEY_CATEGORY, categories[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View rateView = getLayoutInflater().inflate(R.layout.dialog_seekbar, null);
        SeekBar seekBar = rateView.findViewById(R.id.seekbar);
        TextView rateValue = rateView.findViewById(R.id.rate_value);

        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentRate = preferences.getInt(KEY_RATE, 0);
        seekBar.setProgress(currentRate);
        rateValue.setText(String.valueOf(currentRate));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rateValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rateTextView.setText(String.valueOf(seekBar.getProgress()));
                savePreferences(KEY_RATE, seekBar.getProgress());
            }
        });

        builder.setView(rateView)
                .setTitle("Select Minimum Rating")
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View yearView = getLayoutInflater().inflate(R.layout.dialog_seekbar, null);
        SeekBar seekBar = yearView.findViewById(R.id.seekbar);
        TextView yearValue = yearView.findViewById(R.id.rate_value);

        seekBar.setMax(2024 - 2000);
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentYear = preferences.getInt(KEY_YEAR, 2000);
        seekBar.setProgress(currentYear - 2000);
        yearValue.setText(String.valueOf(currentYear));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int selectedYear = 2000 + progress;
                yearValue.setText(String.valueOf(selectedYear));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int selectedYear = 2000 + seekBar.getProgress();
                yearTextView.setText(String.valueOf(selectedYear));
                savePreferences(KEY_YEAR, selectedYear);
            }
        });

        builder.setView(yearView)
                .setTitle("Select Release Year From")
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showSortDialog() {
        String[] sorts = {"Release Date", "Rating"};
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentSort = preferences.getString(KEY_SORT, "Release Date");
        int checkedItem = -1;

        for (int i = 0; i < sorts.length; i++) {
            if (sorts[i].equals(currentSort)) {
                checkedItem = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sort By")
                .setSingleChoiceItems(sorts, checkedItem, (dialog, which) -> {
                    sortTextView.setText(sorts[which]);
                    savePreferences(KEY_SORT, sorts[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }
}