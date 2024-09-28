package com.example.eventapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    ImageView backbtn;
    Intent intent;
    private static final String PROFILE_IMAGE_PATH = "profile_images/"; // Firebase path
    private static final int CAMERA_PERMISSION_REQUEST = 101;

    private FirebaseUser currentUser;
    private DatabaseReference userDatabaseRef;

    private ShapeableImageView profileImageView;
    private boolean isProfileImageSet = false;

    // Firebase Storage reference
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backbtn= (ImageView) findViewById(R.id.backButton);
        profileImageView = findViewById(R.id.profileImage);

        // Initialize Firebase Storage
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_images/").child(currentUser.getUid() + ".png");

        // Load the profile image from Firebase Storage
        loadProfileImage();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        // Handle profile image click to show image selection dialog
        profileImageView.setOnClickListener(v -> showImageSelectionDialog());
    }

    private void setProfileImage(Bitmap bitmap) {
        profileImageView.setImageBitmap(bitmap);
        isProfileImageSet = true;
        uploadProfileImage(bitmap);
    }

    // Upload the profile image to Firebase Storage
    private void uploadProfileImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload image to Firebase Storage using the current user's UID
        StorageReference profileRef = storageReference.child(currentUser.getUid() + ".png");
        UploadTask uploadTask = profileRef.putBytes(imageData);

        // Once upload is successful, get the image download URL and save it to Firebase Realtime Database
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the profile image URL in the Realtime Database under the current user
                userDatabaseRef.child("profileImageUrl").setValue(uri.toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile photo uploaded", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to save image URL", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }).addOnFailureListener(e ->
                Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Load the profile image from Firebase Storage
    /*private void loadProfileImage() {
        StorageReference profileRef = storageReference.child(PROFILE_IMAGE_PATH);
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(ProfileActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.profile) // Placeholder while loading
                    .into(profileImageView);
            isProfileImageSet = true;
        }).addOnFailureListener(e ->
                Toast.makeText(ProfileActivity.this, "Failed to load profile photo", Toast.LENGTH_SHORT).show());
    }*/
    private void loadProfileImage() {
        userDatabaseRef.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImageUrl = dataSnapshot.getValue(String.class);
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    // Load image using Glide
                    Glide.with(ProfileActivity.this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.profile)  // Placeholder while loading
                            .into(profileImageView);
                    isProfileImageSet = true;
                } else {
                    profileImageView.setImageResource(R.drawable.profile); // Default image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show dialog for selecting image options
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] options = isProfileImageSet ?
                new String[]{"Choose from Gallery", "Take a Photo", "Remove Profile Photo"} :
                new String[]{"Choose from Gallery", "Take a Photo"};

        builder.setTitle("Select Profile Photo")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openGallery();
                            break;
                        case 1:
                            openCamera();
                            break;
                        case 2:
                            if (isProfileImageSet) removeProfileImage();
                            break;
                    }
                });

        builder.show();
    }

    // Open the gallery to choose an image
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    // Open the camera to take a photo
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        }
    }

    // Remove profile image from Firebase Storage
    private void removeProfileImage() {
        StorageReference profileRef = storageReference.child(PROFILE_IMAGE_PATH);
        profileRef.delete().addOnSuccessListener(aVoid -> {
            profileImageView.setImageResource(R.drawable.profile);
            isProfileImageSet = false;
            Toast.makeText(ProfileActivity.this, "Profile photo removed", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(ProfileActivity.this, "Failed to remove profile photo", Toast.LENGTH_SHORT).show());
    }

    // Handle camera permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Activity Result Launchers for Camera and Gallery
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        setProfileImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    setProfileImage(photo);
                }
            });
}