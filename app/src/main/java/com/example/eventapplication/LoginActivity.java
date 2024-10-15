package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private EditText emailUsernameInput, loginpasswordInput;
    private Button loginButton;
    private TextView forgotPassword;
    private ImageView loginpasswordToggle;
    private boolean isPasswordVisible = false;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUsernameInput = findViewById(R.id.editTextEmailUsername);
        loginpasswordInput = findViewById(R.id.loginPasswordInput);
        loginpasswordToggle = findViewById(R.id.loginPasswordToggle);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgot_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        loginpasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

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

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            loginpasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginpasswordToggle.setImageResource(R.drawable.visibility);
        } else {
            loginpasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginpasswordToggle.setImageResource(R.drawable.visibility_off);
        }
        loginpasswordInput.setSelection(loginpasswordInput.getText().length()); // Move cursor to the end
        isPasswordVisible = !isPasswordVisible;
    }

    private void verifyLogin() {
        String usernameOrEmail = emailUsernameInput.getText().toString().trim();
        String password = loginpasswordInput.getText().toString().trim();
        loginpasswordInput = findViewById(R.id.loginPasswordInput);
        loginpasswordToggle = findViewById(R.id.loginPasswordToggle);

        // Validate input fields
        if (TextUtils.isEmpty(usernameOrEmail)) {
            Toast.makeText(this, "Please enter email or username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the input is an email or a username
        if (Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
            // Input is an email
            loginWithEmail(usernameOrEmail, password);
        } else {
            // Input is a username, so we need to find the email associated with this username
            findEmailByUsername(usernameOrEmail, password);
        }
    }

    private void loginWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        }
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to find email by username
    private void findEmailByUsername(String username, String password) {
        databaseReference.orderByChild("name").equalTo(username)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming username is unique, get the first match
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                HelperClass user = snapshot.getValue(HelperClass.class);
                                if (user != null) {
                                    String email = user.getEmail();
                                    // Now we have the email, proceed to login
                                    loginWithEmail(email, password);
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
