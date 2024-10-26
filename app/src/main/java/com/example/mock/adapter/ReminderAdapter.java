package com.example.mock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.database.AlarmDatabaseHelper;
import com.example.mock.fragment.MovieDetailFragment;
import com.example.mock.model.Reminder;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends BaseAdapter {

    private Context context;
    private List<Reminder> reminders;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private Runnable refreshDrawerCallback;


    public ReminderAdapter(Context context, List<Reminder> reminders, Runnable refreshDrawerCallback) {
        this.context = context;
        this.reminders = reminders;
        this.alarmDatabaseHelper = new AlarmDatabaseHelper(context);
        this.refreshDrawerCallback = refreshDrawerCallback;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem(int position) {
        return reminders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        }

        ImageView imageViewPoster = convertView.findViewById(R.id.imageViewPoster);
        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewReleaseDate = convertView.findViewById(R.id.textViewReleaseDate);
        TextView textViewRating = convertView.findViewById(R.id.textViewRating);
        TextView textViewReminderTime = convertView.findViewById(R.id.textViewReminderTime);
        ImageButton imageViewDelete = convertView.findViewById(R.id.imageViewDelete);

        Reminder reminder = reminders.get(position);

        textViewTitle.setText(reminder.getTitle());
        textViewReleaseDate.setText(reminder.getReleaseDate());
        textViewRating.setText(String.format(Locale.getDefault(), "%.1f/10", reminder.getRating()));
        textViewReminderTime.setText(reminder.getReminderTime());

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + reminder.getPosterPath())
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(imageViewPoster);


        imageViewDelete.setOnClickListener(v -> {
            long movieId = reminder.getId();
            alarmDatabaseHelper.deleteAlarmByMovieId(movieId);
            reminders.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Reminder deleted !", Toast.LENGTH_SHORT).show();

            if (refreshDrawerCallback != null) {
                refreshDrawerCallback.run();
            }
        });


        convertView.setOnClickListener(v -> {
            MovieDetailFragment detailFragment = MovieDetailFragment.newInstance((int) reminder.getId());
            if (context instanceof MainActivity) {

                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return convertView;
    }
}
