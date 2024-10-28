package com.example.mock.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.adapter.ReminderAdapter;
import com.example.mock.database.AlarmDatabaseHelper;
import com.example.mock.model.Reminder;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowAllRemindersFragment extends Fragment {
    private AlarmDatabaseHelper alarmDatabaseHelper;

    public ShowAllRemindersFragment() {
    }


    public static ShowAllRemindersFragment newInstance(String param1, String param2) {
        ShowAllRemindersFragment fragment = new ShowAllRemindersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_all_reminders, container, false);

        ListView listViewReminders = view.findViewById(R.id.listViewReminders);
        alarmDatabaseHelper = new AlarmDatabaseHelper(getContext());

        Cursor cursor = alarmDatabaseHelper.getAllAlarms();
        List<Reminder> reminders = new ArrayList<>();
        int itemCount = 0;

        while (cursor.moveToNext() && itemCount < 10) {
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex("movie_id"));

            @SuppressLint("Range") String movieTitle = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
            @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex("rating"));
            @SuppressLint("Range") String posterPath = cursor.getString(cursor.getColumnIndex("poster_path"));
            @SuppressLint("Range") long dateTimeMillis = cursor.getLong(cursor.getColumnIndex("date_time"));
            String formattedDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(dateTimeMillis));

            reminders.add(new Reminder(id, movieTitle, releaseDate, rating, formattedDateTime, posterPath));
            itemCount++;
        }

        cursor.close();

        ReminderAdapter adapter = new ReminderAdapter(getContext(), reminders, this::refreshNavigationDrawer);
        listViewReminders.setAdapter(adapter);

        Button btnDoneReminder = view.findViewById(R.id.btnDoneReminder);
        btnDoneReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ShowAllRemindersFragment.this).commit();
                    getActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateRemindersInDrawer();
                    }
                }
            }
        });
        return view;
    }

    private void refreshNavigationDrawer() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateRemindersInDrawer();
        }
    }

}