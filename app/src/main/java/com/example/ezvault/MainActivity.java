package com.example.ezvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavView = findViewById(R.id.bottom_navigation_bar);

        bottomNavView.setOnItemSelectedListener(this);
    }

    ItemsFragment items = new ItemsFragment();
    SearchFragment search = new SearchFragment();
    TagsFragment tags = new TagsFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.search_nav) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, search)
                    .commit();
            return true;
        } else if (itemId == R.id.items_nav) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, items)
                    .commit();
            return true;
        } else if (itemId == R.id.tags_nav) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, tags)
                    .commit();
            return true;
        }
        return false;
    }

}