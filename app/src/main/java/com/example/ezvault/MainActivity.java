package com.example.ezvault;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ezvault.database.FirebaseBundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.example.ezvault.utils.UserManager;
import com.example.ezvault.view.ItemsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity{
    FirebaseBundle firebase = new FirebaseBundle();
    BottomNavigationView bottomNavView;
    Toolbar toolbar;
    NavController navController;
    NavHostFragment navHostFragment;
    @Inject
    UserManager userManager;
    private boolean mainMenu = true;
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
        topLevelDestinations.add(R.id.searchFragment);
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
                    userManager.clearUriCache();
                    bottomNavView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (navDestination.getId() == R.id.addItemFragment || navDestination.getId() == R.id.filterFragment) {
                    bottomNavView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mainMenu) {
            inflater.inflate(R.menu.toolbar_menu, menu);
        }
        else {
            inflater.inflate(R.menu.toolbar_edit_item_menu, menu);
            mainMenu = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment itemsFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        Integer currentDestination = navController.getCurrentDestination().getId();

        // enter delete mode if on items fragment
        if (item.getItemId() == R.id.toolbar_trash && currentDestination == R.id.itemsFragment) {
            itemsDeleteMode(itemsFragment, currentDestination, 0);
            return false;
        }

        // exit delete mode
        else if (item.getItemId() == R.id.edit_item_cancel && currentDestination == R.id.itemsFragment) {
            itemsDeleteMode(itemsFragment, currentDestination, 1);
            return false;
        }

        // delete selected items in the items fragment
        else if (item.getItemId() == R.id.edit_item_confirm && currentDestination == R.id.itemsFragment) {
            itemsDeleteMode(itemsFragment, currentDestination, 2);
            return false;
        }

        // navigate to selected fragment
        else {
            return NavigationUI.onNavDestinationSelected(item, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            navController.navigateUp();
        }
        return super.onSupportNavigateUp();
    }

    private void itemsDeleteMode(Fragment itemsFragment ,Integer currentDestination ,Integer mode) {
        // check if given fragment is correct
        if (itemsFragment instanceof ItemsFragment) {
            // enter delete mode
            if (mode == 0) {
                bottomNavView.setVisibility(View.GONE);
                ((ItemsFragment) itemsFragment).hideButton();
                if (currentDestination == R.id.itemsFragment) {
                    mainMenu = false;
                    invalidateOptionsMenu();
                    ((ItemsFragment) itemsFragment).deleteMode(true);
                }
            }

            // exit delete mode
            else if (mode == 1) {
                mainMenu = true;
                ((ItemsFragment) itemsFragment).deleteMode(false);
                invalidateOptionsMenu();
                bottomNavView.setVisibility(View.VISIBLE);
                ((ItemsFragment) itemsFragment).showButton();
            }

            // delete selected items
            else if (mode == 2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // only delete if there are items selected
                if (((ItemsFragment) itemsFragment).getSelectedCount() > 0) {
                    builder.setMessage("Delete Selected Items?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ItemsFragment) itemsFragment).deleteSelected();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
                ((ItemsFragment) itemsFragment).deleteMode(false);
                mainMenu = true;
                invalidateOptionsMenu();
                bottomNavView.setVisibility(View.VISIBLE);
                ((ItemsFragment) itemsFragment).showButton();
            }
            else {
                Log.d("deleteMode", "Invalid Mode Selected");
            }
        }
    }
}