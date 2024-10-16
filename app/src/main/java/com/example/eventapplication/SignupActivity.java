package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private TextView loginText;
    private EditText emailButton;
    private LinearLayout next;
    Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailButton = findViewById(R.id.continue_with_email);
        loginText = findViewById(R.id.login_text);
        next = findViewById(R.id.next);

        // Handle next button click
        next.setOnClickListener(view -> {
            intent = new Intent(SignupActivity.this, SignupwithEmailActivity.class);
            startActivity(intent);
        });

        // Handle login text click
        loginText.setOnClickListener(view -> {
            intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
