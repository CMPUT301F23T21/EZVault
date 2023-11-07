package com.example.ezvault;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ezvault.database.FirebaseBundle;

import android.view.Menu;
import android.view.MenuItem;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    @Inject
    FirebaseBundle firebase;
    BottomNavigationView bottomNavView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup bottom navigation bar
        bottomNavView = findViewById(R.id.bottom_navigation_bar);
        bottomNavView.setOnItemSelectedListener(this);
        bottomNavView.setSelectedItemId(R.id.nav_bar_items);
        toolbar.setTitle("Items");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    ItemsFragment items = new ItemsFragment();
    SearchFragment search = new SearchFragment();
    TagsFragment tags = new TagsFragment();

    // Allows for switching of fragments from bottom navigation bar
    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_bar_search) {
            toolbar.setTitle("Search");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, search)
                    .commit();
            return true;
        } else if (itemId == R.id.nav_bar_items) {
            toolbar.setTitle("Items");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, items)
                    .commit();
            return true;
        } else if (itemId == R.id.nav_bar_tags) {
            toolbar.setTitle("Tags");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, tags)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}