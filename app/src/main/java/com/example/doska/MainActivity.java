package com.example.doska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void init()
    {
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.id_my_ads:
                Toast.makeText(this, "Najata id my ads", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_cars_ads:
                Toast.makeText(this, "Najata id cars ads", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_pc_ads:
                Toast.makeText(this, "Najata id pc ads", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_smartphone_ads:
                Toast.makeText(this, "Najata id smartphone ads", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_dm_ads:
                Toast.makeText(this, "Najata id dm ads", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_sign_up:
                Toast.makeText(this, "Najata id sign up", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_sign_in:
                Toast.makeText(this, "Najata id sign in", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_sign_out:
                Toast.makeText(this, "Najata id sign out", Toast.LENGTH_SHORT).show();

                break;
        }

        return true;
    }
}