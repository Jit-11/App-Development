package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupwithEmailActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private ImageView passwordToggle, confirmPasswordToggle;
    private Button signupButton;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private RadioGroup roleRadioGroup; // RadioGroup for role selection
    private String selectedRole = "Participant"; // Default role

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupwith_email);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        nameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        passwordToggle = findViewById(R.id.passwordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        signupButton = findViewById(R.id.signupButton);
        auth = FirebaseAuth.getInstance();
        roleRadioGroup = findViewById(R.id.radioGroupRole); // Initialize RadioGroup

        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordToggle.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        // Handle sign up button click
        signupButton.setOnClickListener(v -> performSignUp());
    }

    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.visibility);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.visibility_off);
        }
        passwordInput.setSelection(passwordInput.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    // Method to toggle confirm password visibility
    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.visibility);
        } else {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.visibility_off);
        }
        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    // Method to perform sign up and save user data to Firebase
    private void performSignUp() {

            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Get selected role from RadioGroup
            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.radioOrganizer) {
                selectedRole = "Organizer";
            } else {
                selectedRole = "Participant"; // Default if none selected
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User created successfully
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            // Save additional user data to Firebase Database
                            HelperClass user = new HelperClass(email, name, password, "", selectedRole);
                            databaseReference.child(userId).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                                            // Redirect to the appropriate activity based on user role
                                            Intent intent;
                                            if ("Organizer".equals(selectedRole)) {
                                                intent = new Intent(SignupwithEmailActivity.this, Organizer.class); // Replace with actual Organizer activity
                                            } else {
                                                intent = new Intent(SignupwithEmailActivity.this, DashboardActivity.class); // Participant activity
                                            }
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


    }
}
