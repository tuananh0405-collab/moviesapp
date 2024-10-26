package com.example.testmock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.testmock.adapter.ActivityFragmentStateAdapter;
import com.example.testmock.adapter.ReminderAdapter;
import com.example.testmock.api.MovieApiService;
import com.example.testmock.application.MyApplication;
import com.example.testmock.database.AppDatabase;
import com.example.testmock.database.MovieDao;
import com.example.testmock.databinding.ActivityMainBinding;
import com.example.testmock.model.Reminder;
import com.example.testmock.repository.MovieRepository;
import com.example.testmock.viewmodel.MoviesViewModel;
import com.example.testmock.viewmodel.MoviesViewModelFactory;
import com.example.testmock.viewmodel.ProfileViewModel;
import com.example.testmock.viewmodel.ReminderViewModel;
import com.example.testmock.workmanager.ReminderWorker;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Observer<NavController> {
    private static final String TAG = "TAGTAGTAG";
    private NavController currentNavController;
    private ActivityMainBinding binding;
    private MoviesViewModel viewModel;
    private MovieApiService movieApiService;
    private MovieDao movieDao;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Initialize the API service

        MyApplication app = (MyApplication) getApplication();
        movieApiService = app.getMovieApiService();
        // Initialize the database and DAO
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        movieDao = database.movieDao();
        // Create the repository
        MovieRepository repository = new MovieRepository(movieApiService, movieDao);

        MoviesViewModelFactory factory = new MoviesViewModelFactory(repository, movieApiService);
        // Obtain the ViewModel
        viewModel = new ViewModelProvider(this, factory).get(MoviesViewModel.class);

        ViewPager2 viewPager2 = binding.viewPager;
        ActivityFragmentStateAdapter adapter = new ActivityFragmentStateAdapter(this);
        viewPager2.setAdapter(adapter);
        // TabLayout
        TabLayout tabLayout = binding.tabLayout;
        // Bind tabs and viewpager
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_favorite);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_settings);
                    break;
                case 3:
                    tab.setIcon(R.drawable.ic_about);
                    break;
            }
        }).attach();


        // Initialize badges
        TabLayout.Tab favoriteTab = tabLayout.getTabAt(1);
        if (favoriteTab != null) {
            BadgeDrawable badgeDrawable = tabLayout.getTabAt(1).getOrCreateBadge();
            badgeDrawable.setVisible(false); // Initially hidden
        }

        // Observe the favorite count and update the badge
        viewModel.getFavoriteCountLiveData().observe(this, count -> {
            if (favoriteTab != null) {
                BadgeDrawable badge = favoriteTab.getOrCreateBadge();
                if (count > 0) {
                    badge.setVisible(true);
                    badge.setNumber(count);
                } else {
                    badge.setVisible(false);
                }
            }
        });

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.view != null) {
                tab.view.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f // This makes sure each tab takes equal width
                ));
            }
        }

        // Set support action bar
        setSupportActionBar(binding.toolbar);
        // Set page change callback to update AppBar
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setAppBarNavigation(adapter, position);
            }
        });

        // Initialize drawer toggle
        DrawerLayout drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.editProfileButton.setOnClickListener(v -> {
            currentNavController.navigate(R.id.profileFragment);
        });

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getUserProfileLiveData().observe(this, userProfile -> {
            if (userProfile != null) {
                binding.setProfile(userProfile);
            }
        });

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        reminderViewModel.setApplication(getApplication());
        binding.btnShowAll.setOnClickListener(v -> {
            currentNavController.navigate(R.id.remindersFragment);
        });

        reminderAdapter = new ReminderAdapter(new ArrayList<>(), reminderViewModel, viewModel);
        binding.reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.reminderRecyclerView.setAdapter(reminderAdapter);
        reminderViewModel.getReminders().observe(this, this::updateReminders);

        // Register BroadcastReceiver
        IntentFilter filter = new IntentFilter(ReminderWorker.ACTION_REMINDER_DELETED);
        registerReceiver(reminderDeletedReceiver, filter);

    }

    ReminderAdapter reminderAdapter;

    private void updateReminders(List<Reminder> reminders) {
        if (reminders != null) {
            reminderAdapter.setReminders(reminders);
        }
    }

    ReminderViewModel reminderViewModel;

    // Dua vao position + ten fragment => display len thang appBar
    private void setAppBarNavigation(ActivityFragmentStateAdapter adapter, int position) {
        try {
            // Remove previous observers
            Log.d(TAG, "setAppBarNavigation: remove all observer");
            for (MutableLiveData<NavController> navControllerLiveData : adapter.getNavControllerMap().values()) {
                navControllerLiveData.removeObserver(MainActivity.this);
            }
            // Observe the new NavController
            // register new observer for mainactivity, khi ma navController thay doi thi method onChanged se duoc goi => cap nhat lai thang appbar
            adapter.getNavControllerMap().get(position).observe(this, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChanged(NavController navController) {
        currentNavController = navController;
        setAppBarNavigation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (currentNavController != null && currentNavController.navigateUp())
                || super.onSupportNavigateUp();
    }

    private void setAppBarNavigation() {
        if (currentNavController != null) {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(currentNavController.getGraph()).build();
            // Đổi tên appbar
            NavigationUI.setupActionBarWithNavController(MainActivity.this, currentNavController, appBarConfiguration);
        }
    }

    private final BroadcastReceiver reminderDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int movieId = intent.getIntExtra("movieId", -1);
            if (movieId != -1) {
                reminderViewModel.deleteReminder(movieId);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reminderDeletedReceiver);
    }
}