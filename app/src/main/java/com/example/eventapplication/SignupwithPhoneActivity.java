package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupwithPhoneActivity extends AppCompatActivity {

    private EditText nameInput, phoneNumberInput, passwordInput, confirmPasswordInput;
    private ImageView passwordToggle, confirmPasswordToggle;
    private Button signupButton;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupwith_phone);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        nameInput = findViewById(R.id.usernameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        passwordToggle = findViewById(R.id.passwordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        signupButton = findViewById(R.id.signupButton);

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        confirmPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignUp();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.visibility_off);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.visibility);
        }
        passwordInput.setSelection(passwordInput.getText().length()); // Move cursor to the end
        isPasswordVisible = !isPasswordVisible;
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.visibility_off);
        } else {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.visibility);
        }
        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length()); // Move cursor to the end
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    private void performSignUp() {
        String name = nameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure the phone number starts with Indian country code or valid starting digits
        if (!phoneNumber.matches("^((\\+91)?[789]\\d{9})$")) {
            Toast.makeText(this, "Phone number must be an Indian number and exactly 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use Firebase Authentication to create a new user
        auth.createUserWithEmailAndPassword(phoneNumber + "@phone.com", password) // Using phone number as pseudo email
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Save additional user data to Firebase Database
                        HelperClass user = new HelperClass("", name, password, phoneNumber, "");
                        databaseReference.child(userId).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupwithPhoneActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish(); // Close the signup activity
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
