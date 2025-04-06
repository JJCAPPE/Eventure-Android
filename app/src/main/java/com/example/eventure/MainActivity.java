package com.example.eventure;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Set up the bottom navigation view with the nav controller
            BottomNavigationView navView = binding.navView;
            NavigationUI.setupWithNavController(navView, navController);
            
            // Configure the app bar with the nav controller
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_events, R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}