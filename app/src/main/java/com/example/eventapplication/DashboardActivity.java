package com.example.eventapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private EditText searchBar;
    private LinearLayout btnHome,btnLiveEvent, btnSettings;;
    private ImageView notificationIcon, menuIcon;
    Intent intent;
    private LinearLayout featuredEventsLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnHome=(LinearLayout)findViewById(R.id.btnHome);
        searchBar = findViewById(R.id.searchBar);
        //btnHome = findViewById(R.id.textHome);
        btnLiveEvent = findViewById(R.id.btnLiveEventLayout);
        btnSettings = findViewById(R.id.btnSettings);
        notificationIcon = findViewById(R.id.notificationIcon);

        // Handle search functionality
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString();
                // Perform search logic
            }
        });

        // Set filters for events
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Upcoming"
            }
        });

        btnLiveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Popular"
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter events by "Near You"
                intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Profile icon functionality
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile activity
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Event event = snapshot.getValue(Event.class);
                        if (event != null) {
                            // Create a new card for each event
                            addEventCard(event);
                        }
                    }
                } else {
                    // Handle case where no data exists
                    Log.d("DashboardActivity", "No events found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addEventCard(Event event) {
        LinearLayout eventCard = new LinearLayout(this);
        eventCard.setOrientation(LinearLayout.HORIZONTAL);
        eventCard.setBackgroundResource(R.drawable.event_card_background);
        eventCard.setPadding(24, 16, 24, 16);

        // Create layout parameters for the event card and set margin
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardLayoutParams.setMargins(0, 0, 40, 0); // Set bottom margin (16dp)

        // Add event image
        ImageView eventImage = new ImageView(this);
        String imageUrl = event.getImageUrl(); // Ensure this is populated correctly
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(eventImage);
        } else {
            eventImage.setImageResource(R.drawable.sample_event_image); // Add your default image drawable
        }
        eventImage.setLayoutParams(new LinearLayout.LayoutParams(240, 224));
        eventImage.setPadding(0,16,0,16);

        // Add event name and date
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);

        TextView eventName = new TextView(this);
        eventName.setText(event.getName());
        eventName.setTextColor(Color.WHITE);
        eventName.setPadding(24,32,8,8);
        eventName.setTextSize(20);

        TextView eventDate = new TextView(this);
        eventDate.setText("Date: " + event.getDate());
        eventDate.setTextColor(Color.WHITE);
        eventDate.setPadding(24,0,0,0);
        eventDate.setTextSize(14);

        textLayout.addView(eventName);
        textLayout.addView(eventDate);

        // Add views to card
        eventCard.addView(eventImage);
        eventCard.addView(textLayout);

        // Set the layout parameters for the event card
        eventCard.setLayoutParams(cardLayoutParams);

        // Reference the correct LinearLayout inside HorizontalScrollView
        LinearLayout eventContainer = findViewById(R.id.eventContainer); // Ensure this ID matches the XML
        eventContainer.addView(eventCard);

    }
}