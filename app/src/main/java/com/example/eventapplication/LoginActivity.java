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

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUsernameInput = findViewById(R.id.editTextEmailUsername);
        passwordInput = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgot_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

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
            // Input is an email address
            verifyUserByEmail(input, enteredPassword);
        } else if (Patterns.PHONE.matcher(input).matches()) {
            // Input is a phone number
            verifyUserByPhoneNumber(input, enteredPassword);
        } else {
            // Input is neither email nor phone number
            Toast.makeText(this, "Please enter a valid email or phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyUserByEmail(String email, String password) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                HelperClass user = snapshot.getValue(HelperClass.class);
                                if (user != null && user.getPassword().equals(password)) {
                                    loginSuccess();
                                    return;
                                }
                            }
                        }
                        loginFailed();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                        Toast.makeText(LoginActivity.this, "Login failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyUserByPhoneNumber(String phoneNumber, String password) {
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                HelperClass user = snapshot.getValue(HelperClass.class);
                                if (user != null && user.getPassword().equals(password)) {
                                    loginSuccess();
                                    return;
                                }
                            }
                        }
                        loginFailed();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                        Toast.makeText(LoginActivity.this, "Login failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginSuccess() {
        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginFailed() {
        Toast.makeText(LoginActivity.this, "Invalid email/phone or password", Toast.LENGTH_SHORT).show();
    }
}
