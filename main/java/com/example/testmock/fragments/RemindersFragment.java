package com.example.testmock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.testmock.adapter.ReminderAdapter;
import com.example.testmock.databinding.FragmentRemindersBinding;
import com.example.testmock.model.Reminder;
import com.example.testmock.viewmodel.MoviesViewModel;
import com.example.testmock.viewmodel.ReminderViewModel;

import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {
    private FragmentRemindersBinding binding;
    private ReminderViewModel reminderViewModel;
    private ReminderAdapter adapter;
    private MoviesViewModel moviesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRemindersBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reminderViewModel = new ViewModelProvider(requireActivity()).get(ReminderViewModel.class);
        moviesViewModel = new ViewModelProvider(requireActivity()).get(MoviesViewModel.class);
        // Set up RecyclerView
        adapter = new ReminderAdapter(new ArrayList<>(), reminderViewModel, moviesViewModel);
        adapter.setLifecycleOwner(getViewLifecycleOwner());
        binding.recyclerViewReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewReminders.setAdapter(adapter);

        reminderViewModel.getReminders().observe(getViewLifecycleOwner(), this::updateReminders);
    }

    private void updateReminders(List<Reminder> reminders) {
        if (reminders != null) {
            adapter.setReminders(reminders);
        }
    }
}
