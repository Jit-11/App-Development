package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    private TextView login_text;
    private EditText emailbutton;
    private LinearLayout next;
    Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailbutton = findViewById(R.id.continue_with_email);
        login_text = findViewById(R.id.login_text);
        next = findViewById(R.id.next);

        next.setOnClickListener(view -> {
            intent = new Intent(SignupActivity.this, SignupwithEmailActivity.class);
            startActivity(intent);
        });

        login_text.setOnClickListener(view -> {
            intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}