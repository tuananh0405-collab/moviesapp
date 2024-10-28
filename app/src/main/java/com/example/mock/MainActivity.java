package com.example.mock;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mock.adapter.ViewPagerAdapter;
import com.example.mock.database.AlarmDatabaseHelper;
import com.example.mock.database.MovieDatabaseHelper;
import com.example.mock.fragment.EditProfileFragment;
import com.example.mock.fragment.FavoriteFragment;
import com.example.mock.fragment.MovieDetailFragment;
import com.example.mock.fragment.MoviesFragment;
import com.example.mock.fragment.ShowAllRemindersFragment;
import com.example.mock.model.Movie;
import com.example.mock.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements EditProfileFragment.OnProdileEditedListener, FavoriteFragment.OnFavoriteChangeListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;

    static User user = new User();

    private MovieDatabaseHelper movieDatabaseHelper;

    private String previousTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        movieDatabaseHelper = new MovieDatabaseHelper(this);

        // SET UP NAV DRAWER
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // SET UP TAB LAYOUT AND VIEW PAGER
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Movies");
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setText("Favorite");
                    tab.setIcon(R.drawable.ic_heart);
                    tab.getOrCreateBadge().setNumber(movieDatabaseHelper.getAllFavoriteMovies().size());
                    break;
                case 2:
                    tab.setText("Settings");
                    tab.setIcon(R.drawable.ic_settings);
                    break;
                case 3:
                    tab.setText("About");
                    tab.setIcon(R.drawable.ic_about);
                    break;
            }
        }).attach();

        // HANDLE INTENT FROM NOTIFICATION
        handleIntent(getIntent());

        // SET UP TOOLBAR TITLE FOR EACH TABLAYOUT
        setToolbarTitle(viewPager.getCurrentItem());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setToolbarTitle(position);
            }
        });

        // LOAD USER DATA FOR HEADER
        loadUserData();

        // LOAD REMINDERS FOR HEADER
        loadReminders();

        // ONCLICK EVENT IN HEADER
        View headerView = navigationView.getHeaderView(0);

        // EDIT BUTTON
        Button drawer_button = headerView.findViewById(R.id.drawer_button);
        drawer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu tiêu đề hiện tại
                previousTitle = toolbar.getTitle().toString();

                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.VISIBLE);
                Fragment fragment = EditProfileFragment.newInstance(user);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

                // Đặt tiêu đề mới cho fragment ShowAllRemindersFragment
                toolbar.setTitle("Edit Profile");

                drawerLayout.closeDrawer(navigationView);
            }
        });

        // SHOW ALL REMINDERS BUTTON
        Button showAllButton = headerView.findViewById(R.id.show_all_button);
        showAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu tiêu đề hiện tại
                previousTitle = toolbar.getTitle().toString();

                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.VISIBLE);
                Fragment fragment = new ShowAllRemindersFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                // Đặt tiêu đề mới cho fragment ShowAllRemindersFragment
                toolbar.setTitle("Reminders");

                drawerLayout.closeDrawer(navigationView);
            }
        });

        // REQUEST NOTIFICATION PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // CREATE NOTIFICATION CHANNEL
        createNotificationChannel();

    }

    @Override
    public void onFavoriteChanged() {
        updateFavoriteBadge();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Movie Reminder Channel";
            String description = "Channel for Movie Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
//            }
        }
    }

    public void updateFavoriteBadge() {
        TabLayout.Tab favoriteTab = tabLayout.getTabAt(1);
        if (favoriteTab != null) {
            BadgeDrawable badgeDrawable = favoriteTab.getOrCreateBadge();
            badgeDrawable.setMaxNumber(9);
            int favoriteCount = movieDatabaseHelper.getAllFavoriteMovies().size();

            if (favoriteCount > 0) {
                badgeDrawable.setVisible(true);
                badgeDrawable.setNumber(favoriteCount);
            } else {
                badgeDrawable.setVisible(false);
            }
        }
    }

    private void setToolbarTitle(int position) {
        switch (position) {
            case 0:
                toolbar.setTitle("Movies");
                break;
            case 1:
                toolbar.setTitle("Favorite");

                break;
            case 2:
                toolbar.setTitle("Settings");
                break;
            case 3:
                toolbar.setTitle("About");
                break;
        }
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "thang nguyen");
        String email = sharedPreferences.getString("email", "abc@abc.com");
        String dob = sharedPreferences.getString("dob", "2015/11/27");
        boolean isMale = sharedPreferences.getBoolean("isMale", false);
        String avatarBase64 = sharedPreferences.getString("avatar", null);

        if (avatarBase64 != null) {
            byte[] decodedString = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            user.setAvt(decodedByte);
        }

        user.setName(name);
        user.setEmail(email);
        user.setDob(dob);
        user.setMale(isMale);

        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvName);
        tvName.setText(user.getName());
        ImageView headerImage = headerView.findViewById(R.id.headerImage);
        if (user.getAvt() != null) {
            headerImage.setImageBitmap(user.getAvt());
        }
        TextView tvEmail = headerView.findViewById(R.id.tvEmail);
        tvEmail.setText(user.getEmail());
        TextView tvDOB = headerView.findViewById(R.id.tvDOB);
        tvDOB.setText(user.getDob());


        if (user.isMale()) {
            TextView tVGender = headerView.findViewById(R.id.tvGender);
            tVGender.setText("Male");
        } else {
            TextView tVGender = headerView.findViewById(R.id.tvGender);
            tVGender.setText("Female");
        }

    }

    @Override
    public void onProfileEdited(User user) {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvName);
        tvName.setText(user.getName());
        ImageView headerImage = headerView.findViewById(R.id.headerImage);
        if (user.getAvt() != null) {
            headerImage.setImageBitmap(user.getAvt());
        }
        TextView tvEmail = headerView.findViewById(R.id.tvEmail);
        tvEmail.setText(user.getEmail());
        TextView tvDOB = headerView.findViewById(R.id.tvDOB);
        tvDOB.setText(user.getDob());


        if (user.isMale()) {
            TextView tVGender = headerView.findViewById(R.id.tvGender);
            tVGender.setText("Male");
        } else {
            TextView tVGender = headerView.findViewById(R.id.tvGender);
            tVGender.setText("Female");
        }
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    private void loadReminders() {
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        Cursor cursor = alarmDatabaseHelper.getAllAlarms();

        LinearLayout reminderContainer1 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container1);
        LinearLayout reminderContainer2 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container2);
        LinearLayout reminderContainer3 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container3);

        TextView tvReminderContent1 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent1);
        TextView tvReminderTime1 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime1);

        TextView tvReminderContent2 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent2);
        TextView tvReminderTime2 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime2);

        TextView tvReminderContent3 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent3);
        TextView tvReminderTime3 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime3);

        int count = 0;
        while (cursor.moveToNext() && count < 3) {
            @SuppressLint("Range") String movieTitle = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
            @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex("rating"));
            @SuppressLint("Range") long dateTimeMillis = cursor.getLong(cursor.getColumnIndex("date_time"));
            String formattedDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(dateTimeMillis));

            String reminderContent = String.format("%s - %s - %.1f/10", movieTitle, releaseDate, rating);

            if (count == 0) {
                reminderContainer1.setVisibility(View.VISIBLE);
                tvReminderContent1.setText(reminderContent);
                tvReminderTime1.setText(formattedDateTime);
            } else if (count == 1) {
                reminderContainer2.setVisibility(View.VISIBLE);
                tvReminderContent2.setText(reminderContent);
                tvReminderTime2.setText(formattedDateTime);
            } else if (count == 2) {
                reminderContainer3.setVisibility(View.VISIBLE);
                tvReminderContent3.setText(reminderContent);
                tvReminderTime3.setText(formattedDateTime);
            }
            count++;
        }

        cursor.close();
    }

    public void updateRemindersInDrawer() {
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        Cursor cursor = alarmDatabaseHelper.getAllAlarms();

        LinearLayout reminderContainer1 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container1);
        LinearLayout reminderContainer2 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container2);
        LinearLayout reminderContainer3 = navigationView.getHeaderView(0).findViewById(R.id.reminder_container3);

        TextView tvReminderContent1 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent1);
        TextView tvReminderTime1 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime1);

        TextView tvReminderContent2 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent2);
        TextView tvReminderTime2 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime2);

        TextView tvReminderContent3 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderContent3);
        TextView tvReminderTime3 = navigationView.getHeaderView(0).findViewById(R.id.tvReminderTime3);

        reminderContainer1.setVisibility(View.GONE);
        reminderContainer2.setVisibility(View.GONE);
        reminderContainer3.setVisibility(View.GONE);

        int count = 0;
        while (cursor.moveToNext() && count < 3) {
            @SuppressLint("Range") String movieTitle = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
            @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex("rating"));
            @SuppressLint("Range") long dateTimeMillis = cursor.getLong(cursor.getColumnIndex("date_time"));
            String formattedDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(dateTimeMillis));

            String reminderContent = String.format("%s - %s - %.1f/10", movieTitle, releaseDate, rating);

            if (count == 0) {
                reminderContainer1.setVisibility(View.VISIBLE);
                tvReminderContent1.setText(reminderContent);
                tvReminderTime1.setText(formattedDateTime);
            } else if (count == 1) {
                reminderContainer2.setVisibility(View.VISIBLE);
                tvReminderContent2.setText(reminderContent);
                tvReminderTime2.setText(formattedDateTime);
            } else if (count == 2) {
                reminderContainer3.setVisibility(View.VISIBLE);
                tvReminderContent3.setText(reminderContent);
                tvReminderTime3.setText(formattedDateTime);
            }
            count++;
        }

        cursor.close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("open_movie_detail", false)) {
            int movieId = intent.getIntExtra("movieId", -1);
            if (movieId != -1) {
                adapter.setMovieId(String.valueOf(movieId));
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(0, false);
            } else {
                Log.d("START", "movieId is invalid or not received.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setVisibility(View.GONE);

                // Khôi phục tiêu đề ban đầu khi quay lại fragment trước đó
                toolbar.setTitle(previousTitle);
            } else {

            }
        });
    }
}