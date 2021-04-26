package com.example.handsgivingt;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class VolunteerMainBottomNav extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_main_bottom_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.navigation_homepage);
    }

    private boolean loadFragment(Fragment fragment)
    {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        int selectedItem = item.getItemId();
        if(selectedItem == R.id.navigation_homepage)
        {
            fragment = new VolunteerHomepageFragment();
        }
        else if(selectedItem == R.id.navigation_profile)
        {
            fragment = new VolunteerProfileFragment();
        }
        else if(selectedItem == R.id.navigation_social)
        {
            fragment = new NeedySocialPageFragment();
        }
        else
        {
            Log.i("ERROR: ", "Something is wrong, better call oguzhan.");
        }

        return loadFragment(fragment);
    }


}