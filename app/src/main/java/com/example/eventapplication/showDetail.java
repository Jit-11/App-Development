package com.example.eventapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showDetail extends AppCompatActivity {

    private ImageView eventImage;
    private TextView eventName, eventDate, ticketPrice;
    private Button ticketBookingButton; // Button for booking tickets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail); // Ensure this layout matches your updated XML

        // Initialize views
        eventImage = findViewById(R.id.eventImage);
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        ticketPrice = findViewById(R.id.ticketPrice);
        ticketBookingButton = findViewById(R.id.ticketBookingButton); // Button for booking

        // Get the intent that started this activity
        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String date = intent.getStringExtra("eventDate");
        String imageUrl = intent.getStringExtra("eventImageUrl");

        // Set the data to views
        eventName.setText(name);
        eventDate.setText(date);

        // Load the image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(eventImage);
        } else {
            eventImage.setImageResource(R.drawable.event_image_placeholder); // Default image
        }

        // Fetch ticket price from the database
        String defaultEventId = "known_valid_event_id"; // Replace with an actual event ID for testing
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events").child(defaultEventId).child("ticketPrice");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String price = dataSnapshot.getValue(String.class);
                    Log.d("showDetail", "Fetched price: " + price); // Log the price value
                    ticketPrice.setText("Ticket Price: " + price);
                } else {
                    ticketPrice.setText("Price not available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ticketPrice.setText("Error fetching price");
                Log.e("showDetail", "Database error: " + databaseError.getMessage());
            }
        });


        ticketBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookingIntent = new Intent(showDetail.this, showDetail.class); // Changed to TicketBooking class
                // Pass the default event ID or any relevant data needed for booking
                bookingIntent.putExtra("eventId", defaultEventId);
                startActivity(bookingIntent);
            }
        });
    }
}
