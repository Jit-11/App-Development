package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailUsernameInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUsernameInput = findViewById(R.id.editTextEmailUsername);
        passwordInput = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgot_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verifyLogin() {
        String input = emailUsernameInput.getText().toString().trim();
        String enteredPassword = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            loginWithEmail(input, enteredPassword);
        } else if (Patterns.PHONE.matcher(input).matches()) {
            loginWithPhoneNumber(input, enteredPassword);
        } else {
            Toast.makeText(this, "Please enter a valid email or phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithEmail(String email, String password) {
        // Check if the user is already logged in
        if (auth.getCurrentUser() != null) {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            navigateToDashboard();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkUserInDatabase(user.getUid());
                        }
                    } else {
                        loginFailed(task.getException());
                    }
                });
    }

    private void loginWithPhoneNumber(String phoneNumber, String password) {
        // Check if the user is already logged in
        if (auth.getCurrentUser() != null) {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            navigateToDashboard();
            return;
        }

        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        String email = userSnapshot.child("email").getValue(String.class);

                        if (email != null) {
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(signInTask -> {
                                        if (signInTask.isSuccessful()) {
                                            checkUserInDatabase(userId);
                                        } else {
                                            loginFailed(signInTask.getException());
                                        }
                                    });
                        } else {
                            loginFailed(new Exception("Email not found for the phone number"));
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Phone number not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void checkUserInDatabase(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                } else {
                    loginFailed(new Exception("User data not found"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(LoginActivity.this, "Login failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginFailed(Exception exception) {
        String errorMessage = (exception != null) ? exception.getMessage() : "Invalid email/phone or password";
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
