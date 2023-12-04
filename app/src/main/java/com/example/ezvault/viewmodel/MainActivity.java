package com.example.ezvault.viewmodel;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ezvault.R;
import com.example.ezvault.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity{
    BottomNavigationView bottomNavView;
    Toolbar toolbar;
    NavController navController;
    NavHostFragment navHostFragment;
    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar, bottom nav bar and set visibility to gone to hide
        toolbar = findViewById(R.id.toolbar);
        bottomNavView = findViewById(R.id.bottom_navigation_bar);
        toolbar.setVisibility(View.GONE);
        bottomNavView.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        // set top level destinations to hide back button
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.itemsFragment);
        topLevelDestinations.add(R.id.tagsFragment);
        topLevelDestinations.add(R.id.profileFragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // setup bottom navigation bar with navController
        navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        // show toolbar and bottom nav bar once on items page
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.itemsFragment) {
                    userManager.clearLocalImages();
                }

                // Hide bottom nav bar in following fragments
                if (navDestination.getId() == R.id.addItemFragment
                        || navDestination.getId() == R.id.filterFragment
                        || navDestination.getId() == R.id.cameraFragment
                        || navDestination.getId() == R.id.editItemDetails) {

                    toolbar.setVisibility(View.VISIBLE);
                    bottomNavView.setVisibility(View.GONE);

                // Hide both nav bars in following fragments
                } else if (navDestination.getId() == R.id.welcomeFragment
                        || navDestination.getId() == R.id.loginFragment
                        || navDestination.getId() == R.id.newUserFragment){

                    bottomNavView.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);

                // Show both nav bars as default behaviour
                } else {
                    bottomNavView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            navController.navigateUp();
        }
        return super.onSupportNavigateUp();
    }

}