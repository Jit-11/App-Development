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

        loginpasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
        loginButton.setOnClickListener(v -> verifyLogin());

        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
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
        loginpasswordInput.setSelection(loginpasswordInput.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void verifyLogin() {
        String usernameOrEmail = emailUsernameInput.getText().toString().trim();
        String password = loginpasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(usernameOrEmail)) {
            Toast.makeText(this, "Please enter email or username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
            // Input is an email
            loginWithEmail(usernameOrEmail, password);
        } else {
            // Input is a username, find the email associated with this username
            findEmailByUsername(usernameOrEmail, password);
        }
    }

    private void loginWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Check the user's role in the database
                            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        HelperClass userData = dataSnapshot.getValue(HelperClass.class);
                                        if (userData != null) {
                                            String role = userData.getRole();
                                            Log.d("LoginActivity", "User role: " + role);
                                            // Validate credentials and navigate based on role
                                            validateUserCredentials(email, password, role);
                                        } else {
                                            showError("User data is null.");
                                        }
                                    } else {
                                        showError("User data not found.");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("LoginActivity", "Database Error: " + databaseError.getMessage());
                                    showError("Database Error: " + databaseError.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.e("LoginActivity", "Login Failed: " + task.getException().getMessage());
                        showError("Login Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void findEmailByUsername(String username, String password) {
        databaseReference.orderByChild("name").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                HelperClass user = snapshot.getValue(HelperClass.class);
                                if (user != null) {
                                    String email = user.getEmail();
                                    String role = user.getRole();
                                    // Check credentials and navigate based on role
                                    validateUserCredentials(email, password, role);
                                    break;
                                }
                            }
                        } else {
                            showError("Username not found.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("LoginActivity", "Database Error: " + databaseError.getMessage());
                        showError("Database Error: " + databaseError.getMessage());
                    }
                });
    }

    private void validateUserCredentials(String email, String password, String role) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Navigate based on role
                        navigateToRoleBasedActivity(role);
                    } else {
                        Log.e("LoginActivity", "Login Failed: " + task.getException().getMessage());
                        showError("Login Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void navigateToRoleBasedActivity(String role) {
        Intent intent;
        if (role.equals("Organizer")) { // Case-sensitive check
            intent = new Intent(LoginActivity.this, Organizer.class);
        } else if (role.equals("Participant")) { // Case-sensitive check
            intent = new Intent(LoginActivity.this, DashboardActivity.class);
        } else {
            showError("Role not recognized.");
            return;
        }
        startActivity(intent);
        finish(); // Close the login activity
    }

    private void showError(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
