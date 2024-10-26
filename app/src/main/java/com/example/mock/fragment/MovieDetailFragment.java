package com.example.mock.fragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mock.MainActivity;
import com.example.mock.R;
import com.example.mock.adapter.CastAdapter;
import com.example.mock.api.MovieAPI;
import com.example.mock.database.AlarmDatabaseHelper;
import com.example.mock.database.MovieDatabaseHelper;
import com.example.mock.model.Cast;
import com.example.mock.model.Movie;
import com.example.mock.model.response.CastResponse;
import com.example.mock.receiver.AlarmReceiver;
import com.example.mock.service.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {

    private int movieId;
    private static final String API_KEY = "e7631ffcb8e766993e5ec0c1f4245f93";
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private ImageView imageViewFavoriteDetail;
    private Button buttonReminder;
    private MovieDatabaseHelper dbHelper;
    private Movie movie_detail;

    private Calendar calendar;
    private AlarmManager alarmManager;
    private AlarmDatabaseHelper alarmDatabaseHelper;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt("movie_id", movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt("movie_id");
        }
        alarmDatabaseHelper = new AlarmDatabaseHelper(getContext());
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        castRecyclerView = view.findViewById(R.id.recyclerViewCast);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        imageViewFavoriteDetail = view.findViewById(R.id.imageViewFavoriteDetail);
        dbHelper = new MovieDatabaseHelper(getContext());

        loadMovieDetails(view);
        loadMovieCredits();

        checkIfFavorite();

        imageViewFavoriteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteClick();
            }
        });

        buttonReminder = view.findViewById(R.id.buttonReminder);
        buttonReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        return view;
    }


    private void showDatePicker() {
        calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    showTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    setMovieReminder();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void setMovieReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(getContext(), "Check permission for alarm", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        long alarmId = saveAlarmToDatabase();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateRemindersInDrawer();
        }
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("movieId", movieId);
        intent.putExtra("image", movie_detail.getPosterPath());
        intent.putExtra("title", movie_detail.getTitle());
        intent.putExtra("overview", movie_detail.getOverview());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) alarmId, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(getContext(), "Setting alarm at: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    private long saveAlarmToDatabase() {
        String movieTitle = movie_detail.getTitle();
        String movieReleaseDate = movie_detail.getReleaseDate();
        float movieRating = movie_detail.getRating();
        String moviePosterPath = movie_detail.getPosterPath();

        return alarmDatabaseHelper.upsertAlarm(calendar.getTimeInMillis(), movieId, movieTitle, movieReleaseDate, movieRating, moviePosterPath);
    }

    private void checkIfFavorite() {
        if (dbHelper.isFavorite(movieId)) {
            imageViewFavoriteDetail.setImageResource(R.drawable.ic_star_filled);
        } else {
            imageViewFavoriteDetail.setImageResource(R.drawable.ic_star);
        }
    }

    private void handleFavoriteClick() {
        Movie movie = new Movie(movieId, movie_detail.getTitle(), movie_detail.getReleaseDate(), movie_detail.getRating(), movie_detail.getPosterPath(), movie_detail.getOverview()); // Tạo movie từ thông tin
        if (dbHelper.isFavorite(movieId)) {
            dbHelper.removeFavoriteMovie(movieId);
            imageViewFavoriteDetail.setImageResource(R.drawable.ic_star);
            Toast.makeText(getContext(), movie.getTitle() + " removed from favorites!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addFavoriteMovie(movie);
            imageViewFavoriteDetail.setImageResource(R.drawable.ic_star_filled);
            Toast.makeText(getContext(), movie.getTitle() + " added to favorites!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMovieCredits() {
        MovieAPI movieApi = ApiClient.getInstance().create(MovieAPI.class);

        Call<CastResponse> call = movieApi.getMovieCredits(movieId, API_KEY);

        call.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> castList = response.body().getCastList();

                    castAdapter = new CastAdapter(castList);
                    castRecyclerView.setAdapter(castAdapter);
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Log.e("MovieDetail", "Failed to load cast and crew", t);
            }
        });
    }

    private void loadMovieDetails(View view) {
        MovieAPI movieApi = ApiClient.getInstance().create(MovieAPI.class);

        Call<Movie> call = movieApi.getMovieDetails(movieId, API_KEY);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();
                    movie_detail = movie;
                    Log.d("MovieDetail", "onResponse: " + movie.getTitle());

                    updateUI(view, movie);
                } else {
                    Log.e("MovieDetail", "onResponse: Failed to load movie details");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e("MovieDetail", "onFailure: ", t);
            }
        });
    }

    private void updateUI(View view, Movie movie) {
        TextView titleTextView = view.findViewById(R.id.textViewTitle);
        TextView releaseDateTextView = view.findViewById(R.id.textViewReleaseDate);
        TextView ratingTextView = view.findViewById(R.id.textViewRating);
        TextView overviewTextView = view.findViewById(R.id.textViewOverview);
        ImageView posterImageView = view.findViewById(R.id.imageViewPoster);

        titleTextView.setText(movie.getTitle());
        releaseDateTextView.setText("Release date: " + movie.getReleaseDate());
        ratingTextView.setText("Rating: " + movie.getRating() + "/10.0");
        overviewTextView.setText(movie.getOverview());

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(posterImageView);
    }
}
