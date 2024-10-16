package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Organizer extends AppCompatActivity {

    private FirebaseAuth auth;
    Button logoutButton,createEvent;
    Intent intent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer);

        auth = FirebaseAuth.getInstance();

        logoutButton=findViewById(R.id.logoutButton);
        createEvent=findViewById(R.id.createEventButton);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(Organizer.this,CreateEventActivity.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(v -> logout());

    }
    private void logout() {
        auth.signOut(); // Sign out the user
        Intent intent = new Intent(Organizer.this, SignupActivity.class); // Redirect to MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish current activity
    }
}
