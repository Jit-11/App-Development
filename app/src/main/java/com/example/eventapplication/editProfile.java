package com.example.eventapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class editProfile extends AppCompatActivity {

    private EditText editUsername, editEmail, editPhoneNumber;
    private Button btnUpdate;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editUsername = findViewById(R.id.usernameEditText);
        editEmail = findViewById(R.id.emailEditText);
        editPhoneNumber = findViewById(R.id.phoneEditText);
        btnUpdate = findViewById(R.id.saveButton);

        // Get current user ID from FirebaseAuth
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Update profile on button click
        btnUpdate.setOnClickListener(view -> {
            String username = editUsername.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phoneNumber = editPhoneNumber.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(editProfile.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure either email or phone is filled, but not both
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(editProfile.this, "Fill either email or phone number, not both", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(editProfile.this, "Email or phone number is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update in the database
            userDatabaseRef.child("name").setValue(username);
            if (!TextUtils.isEmpty(email)) {
                userDatabaseRef.child("email").setValue(email);
            } else if (!TextUtils.isEmpty(phoneNumber)) {
                userDatabaseRef.child("phoneNumber").setValue(phoneNumber);
            }

            Toast.makeText(editProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
