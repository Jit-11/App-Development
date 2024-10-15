package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class changePassword extends AppCompatActivity {
    private Button change;
    private EditText user, currentPassword, newPass, confirmPass;
    private ImageView loginPasswordToggle, newPasswordToggle, confirmPasswordToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Intent intent;
    private boolean isCurPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        change = findViewById(R.id.change);
        user = findViewById(R.id.email);
        currentPassword = findViewById(R.id.curPassword);
        newPass = findViewById(R.id.newPassword);
        confirmPass = findViewById(R.id.confirmPassword);
        loginPasswordToggle = findViewById(R.id.loginPasswordToggle);
        newPasswordToggle = findViewById(R.id.newPasswordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);

        setupPasswordToggle(currentPassword, loginPasswordToggle, "cur");
        setupPasswordToggle(newPass, newPasswordToggle, "new");
        setupPasswordToggle(confirmPass, confirmPasswordToggle, "confirm");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");  // Reference to your users in Firebase

        change.setOnClickListener(v -> changePassword());
    }

    private void setupPasswordToggle(EditText passwordField, ImageView toggleButton, String passwordType) {
        toggleButton.setOnClickListener(v -> {
            switch (passwordType) {
                case "cur":
                    isCurPasswordVisible = !isCurPasswordVisible;
                    togglePasswordVisibility(passwordField, toggleButton, isCurPasswordVisible);
                    break;
                case "new":
                    isNewPasswordVisible = !isNewPasswordVisible;
                    togglePasswordVisibility(passwordField, toggleButton, isNewPasswordVisible);
                    break;
                case "confirm":
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    togglePasswordVisibility(passwordField, toggleButton, isConfirmPasswordVisible);
                    break;
            }
        });
    }

    private void togglePasswordVisibility(EditText passwordField, ImageView toggleButton, boolean isVisible) {
        if (isVisible) {
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleButton.setImageResource(R.drawable.visibility_off);  // Use an icon for visibility off
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.visibility);  // Use an icon for visibility on
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void changePassword() {
        String userInput = user.getText().toString();
        String currentPwd = currentPassword.getText().toString();
        String newPwd = newPass.getText().toString();
        String confirmPwd = confirmPass.getText().toString();

        if (TextUtils.isEmpty(userInput) || TextUtils.isEmpty(currentPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
            Toast.makeText(changePassword.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(changePassword.this, "New Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(userInput, currentPwd);
            reauthenticateAndChangePassword(firebaseUser, credential, newPwd);
        } else {
            Toast.makeText(changePassword.this, "No logged-in user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void reauthenticateAndChangePassword(FirebaseUser user, AuthCredential credential, String newPwd) {
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPwd).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        String userId = user.getUid();
                        updatePasswordInDatabase(userId, newPwd);
                    } else {
                        Toast.makeText(changePassword.this, "Password update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(changePassword.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordInDatabase(String userId, String newPwd) {
        // Retrieve existing user data from Firebase and update the password field
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Retrieve user data using HelperClass
                HelperClass userData = task.getResult().getValue(HelperClass.class);

                if (userData != null) {
                    // Update password in the HelperClass object
                    userData.setPassword(newPwd);

                    // Push updated data back to the database
                    databaseReference.child(userId).setValue(userData).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(changePassword.this, "Password updated ", Toast.LENGTH_SHORT).show();
                            intent = new Intent(changePassword.this, DashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(changePassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(changePassword.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
