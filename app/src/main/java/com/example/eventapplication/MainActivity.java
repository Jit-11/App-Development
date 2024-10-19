package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

        ImageView logoImageView = findViewById(R.id.logoImageView);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        logoImageView.startAnimation(fadeInAnimation);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                navigateToNextActivity(currentUser);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        /*if (currentUser != null) {
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
        }*/
    }
    private void navigateToNextActivity(FirebaseUser currentUser) {
        if (currentUser != null) {
            // User is signed in, check their role
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            databaseReference.child("role").get().addOnCompleteListener(task -> {
                Intent intent;
                if (task.isSuccessful() && task.getResult() != null) {
                    String role = task.getResult().getValue(String.class);
                    if ("Organizer".equals(role)) {
                        intent = new Intent(MainActivity.this, Organizer.class);
                    } else {
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                    }
                } else {
                    // Handle case where fetching the role fails
                    intent = new Intent(MainActivity.this, SignupActivity.class);
                }
                startActivity(intent);
                finish();
            });
        } else {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
