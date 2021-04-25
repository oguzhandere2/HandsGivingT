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

public class NeedyMainBottomNav extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_main_bottom_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.navigation_homepage);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        Log.i("START HERE", "START HERE");
        if(appBarConfiguration == null)
        {
            Log.i("APPBAR NULL", "APPBAR NULL");
        }


        NavController navController = Navigation.findNavController(this, R.id.nav_host_frame);
        if( navController == null)
        {
            Log.i("navController NULL", "navController NULL");
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

         */


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
            fragment = new NeedyHomepageFragment();
        }
        else if(selectedItem == R.id.navigation_profile)
        {
            fragment = new NeedyProfileFragment();
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