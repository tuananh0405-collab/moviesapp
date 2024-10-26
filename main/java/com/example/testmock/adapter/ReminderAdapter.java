package com.example.testmock.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmock.R;
import com.example.testmock.databinding.ItemReminderDetailBinding;
import com.example.testmock.model.Movie;
import com.example.testmock.model.Reminder;
import com.example.testmock.viewmodel.MoviesViewModel;
import com.example.testmock.viewmodel.ReminderViewModel;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private static final String TAG = "TAGTAGTAG";
    private List<Reminder> reminders;
    private ReminderViewModel reminderViewModel;
    private MoviesViewModel moviesViewModel;
    private LifecycleOwner lifecycleOwner;

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    public ReminderAdapter(List<Reminder> reminders, ReminderViewModel viewModel, MoviesViewModel moviesViewModel) {
        this.reminders = reminders;
        this.reminderViewModel = viewModel;
        this.moviesViewModel = moviesViewModel;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderDetailBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_reminder_detail, parent, false);
        return new ReminderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.binding.setReminder(reminder);
        Log.d(TAG, "onBindViewHolder: " + reminder.getMovieId());
        moviesViewModel.getMovieDetailLiveData().observe((LifecycleOwner) holder.itemView.getContext(), new Observer<Movie>() {
            @Override
            public void onChanged(Movie movie) {
                holder.binding.setMovie(movie);
            }
        });

        moviesViewModel.fetchMovieDetail(reminder.getMovieId());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        final ItemReminderDetailBinding binding;

        ReminderViewHolder(ItemReminderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
