package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, check their role
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            databaseReference.child("role").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String role = task.getResult().getValue(String.class);
                    Intent intent;
                    if ("Organizer".equals(role)) {
                        intent = new Intent(MainActivity.this, Organizer.class);
                    } else {
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                    }
                    startActivity(intent);
                } else {
                    // Handle case where fetching the role fails
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
                finish();
            });
        } else {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
