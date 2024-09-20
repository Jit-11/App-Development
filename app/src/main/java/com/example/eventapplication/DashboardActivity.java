package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    private EditText searchBar;
    private LinearLayout btnHome,btnLiveEvent, btnSettings;;
    private ImageView profileIcon, menuIcon;
    Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnHome=(LinearLayout)findViewById(R.id.btnHome);
        searchBar = findViewById(R.id.searchBar);
        //btnHome = findViewById(R.id.textHome);
        btnLiveEvent = findViewById(R.id.btnLiveEventLayout);
        btnSettings = findViewById(R.id.btnSettings);
        profileIcon = findViewById(R.id.profileIcon);

        // Handle search functionality
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString();
                // Perform search logic
            }
        });

        // Set filters for events
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Upcoming"
            }
        });

        btnLiveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Popular"
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Near You"
                intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Profile icon functionality
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile activity
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}