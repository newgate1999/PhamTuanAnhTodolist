package com.example.phamtuananhtodolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Home");

        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView navigationView = findViewById(R.id.navigationBottom);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        actionBar.setTitle("Home");
                        HomeFragment fragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content, fragment, "");
                        fragmentTransaction.commit();
                        return true;

                    case R.id.nav_profile:
                        ProfileFragment fragment1 = new ProfileFragment();
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.content, fragment1, "");
                        fragmentTransaction1.commit();

                        actionBar.setTitle("Profile");
                        return true;
                }

                return false;
            }
        });

        setActionBar();

    }

    private void setActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}