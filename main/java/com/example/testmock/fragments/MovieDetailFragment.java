package com.example.testmock.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.testmock.R;
import com.example.testmock.adapter.CastAndCrewAdapter;
import com.example.testmock.database.AppDatabase;
import com.example.testmock.databinding.FragmentMovieDetailsBinding;
import com.example.testmock.model.Movie;
import com.example.testmock.model.Reminder;
import com.example.testmock.viewmodel.MoviesViewModel;
import com.example.testmock.viewmodel.ReminderViewModel;
import com.example.testmock.workmanager.ReminderWorker;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MovieDetailFragment extends Fragment {
    private static final String TAG = "TAGTAGTAG";
    private FragmentMovieDetailsBinding binding;
    private MoviesViewModel viewModel;
    private ReminderViewModel reminderViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MoviesViewModel.class);
        reminderViewModel = new ViewModelProvider(requireActivity()).get(ReminderViewModel.class);
        viewModel.getSelectedMovieLiveData().observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                Log.d(TAG, "onViewCreated: isFav is " + movie.isFavorite());
                binding.icFavorite.setImageResource(movie.isFavorite() ? R.drawable.ic_like : R.drawable.ic_dislike);
                binding.setMovie(movie);
                binding.executePendingBindings();
                // Set up RecyclerView for cast and crew
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                binding.recyclerViewCastAndCrew.setLayoutManager(layoutManager);

                CastAndCrewAdapter adapter = new CastAndCrewAdapter(new ArrayList<>());
                binding.recyclerViewCastAndCrew.setAdapter(adapter);
                viewModel.getCastMembersLiveData().observe(getViewLifecycleOwner(), adapter::setCastMembers);
            }
        });

        binding.icFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getSelectedMovieLiveData().getValue().isFavorite()) {
                    Log.d(TAG, "onClick: " + viewModel.getSelectedMovieLiveData().getValue().isFavorite());
                    binding.icFavorite.setImageResource(!viewModel.getSelectedMovieLiveData().getValue().isFavorite() ? R.drawable.ic_like : R.drawable.ic_dislike);
                    viewModel.getSelectedMovieLiveData().getValue().setFavorite(false);
                    viewModel.insertMovie(viewModel.getSelectedMovieLiveData().getValue());
                } else {
                    Log.d(TAG, "onClick: " + viewModel.getSelectedMovieLiveData().getValue().isFavorite());
                    binding.icFavorite.setImageResource(viewModel.getSelectedMovieLiveData().getValue().isFavorite() ? R.drawable.ic_dislike : R.drawable.ic_like);
                    viewModel.getSelectedMovieLiveData().getValue().setFavorite(true);
                    viewModel.insertMovie(viewModel.getSelectedMovieLiveData().getValue());
                }
            }
        });

        binding.btnReminder.setOnClickListener(v -> showDateTimePicker());
    }


    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();

        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            date.set(year, month, dayOfMonth);
            new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                scheduleReminder(date.getTimeInMillis());
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void scheduleReminder(long reminderTime) {
        // Tạo một WorkRequest để thực hiện nhắc nhở
        OneTimeWorkRequest reminderRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(reminderTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(reminderRequest);

        // Lưu reminder vào Room
        Reminder reminder = new Reminder();
        reminder.setReminderTime(reminderTime);
        reminder.setMovieId(viewModel.getSelectedMovieLiveData().getValue().getId());

        // Lưu reminder vào Room
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        new Thread(() ->
        {
            if (db.reminderDao().getReminderByMovieId(reminder.getMovieId()) == null) {
                reminderViewModel.insertReminder(reminder);
            } else {
                reminderViewModel.updateReminder(reminder);
            }
        }).start();
    }
}
