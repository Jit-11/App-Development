package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class Organizer extends AppCompatActivity {

    private FirebaseAuth auth;
    Button logoutButton;
    FloatingActionButton fabCreateEvent;
    ImageView ivProfile;
    Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        auth = FirebaseAuth.getInstance();

        // Initialize buttons and fab
        logoutButton = findViewById(R.id.logoutButton);
        fabCreateEvent = findViewById(R.id.fab_create_event);
        ivProfile = findViewById(R.id.iv_profile);

        // Logout button action
        logoutButton.setOnClickListener(v -> logout());

        // Floating Action Button action to open the bottom sheet dialog
        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        // Profile ImageView action to navigate to ProfileActivity
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Organizer.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Function to display bottom sheet dialog
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Organizer.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_event_options, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up bottom sheet options
        bottomSheetView.findViewById(R.id.tv_create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // Intent to navigate to CreateEventActivity
                intent = new Intent(Organizer.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        bottomSheetView.findViewById(R.id.tv_update_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // Handle event update logic here
            }
        });

        bottomSheetView.findViewById(R.id.tv_cancel_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // Handle event cancellation logic here
            }
        });

        bottomSheetDialog.show();
    }

    // Function to handle logout
    private void logout() {
        auth.signOut(); // Sign out the user
        Intent intent = new Intent(Organizer.this, SignupActivity.class); // Redirect to SignupActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish current activity
    }
}
