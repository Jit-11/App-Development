package com.example.eventapplication;

import static android.media.MediaSyncEvent.createEvent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private Spinner venueSpinner;
    ImageView selectDateImageView;
    private Button createEventButton;
    private TextView selectDateText;
    private EditText eventNameEditText, ticketPriceEditText;
    private ArrayList<String> venueList = new ArrayList<>();
    private ArrayList<String> venueIds = new ArrayList<>();
    private String selectedDate;
    private String selectedVenueId;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        venueSpinner = findViewById(R.id.venueSpinner);
        selectDateImageView = findViewById(R.id.selectDateImageView);
        selectDateText = findViewById(R.id.selectDateText);
        createEventButton = findViewById(R.id.createEventButton);
        eventNameEditText = findViewById(R.id.eventNameEditText);
        ticketPriceEditText = findViewById(R.id.ticketPriceEditText);
        auth = FirebaseAuth.getInstance();
        // Fetch venues from Firebase
        fetchVenues();

        // Set up date picker
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        // Optionally, set the OnClickListener on the ImageView too
        selectDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        // Set up create event button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
    }

    private void fetchVenues() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Venues");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venueList.clear();
                venueIds.clear();
                venueList.add("--Select Venue--"); // Hint text
                venueIds.add(""); // Empty ID for the hint, as a placeholder

                for (DataSnapshot venueSnapshot : dataSnapshot.getChildren()) {
                    String venueName = venueSnapshot.child("name").getValue(String.class);
                    String venueId = venueSnapshot.getKey();
                    venueList.add(venueName);
                    venueIds.add(venueId);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateEventActivity.this,
                        R.layout.spinner_item, venueList) {
                    @Override
                    public boolean isEnabled(int position) {
                        // Disable the "Select Venue" hint item
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set hint item color to gray
                            tv.setTextColor(Color.BLUE);
                        } else {
                            tv.setTextColor(Color.WHITE);
                        }
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                venueSpinner.setAdapter(adapter);

                venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            selectedVenueId = venueIds.get(position); // Valid selection
                        } else {
                            selectedVenueId = null; // Hint selected, invalid selection
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedVenueId = null;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreateEventActivity.this, "Error fetching venues", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectDateText.setText(selectedDate); // Update the TextView with the selected date
                }, year, month, day);

        // Set the minimum date to tomorrow
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to next day
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void createEvent() {
        String eventName = eventNameEditText.getText().toString().trim(); // Get event name
        String ticketPrice = ticketPriceEditText.getText().toString().trim(); // Get ticket price

        // Get the current user's organizer ID
        String organizerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (selectedVenueId != null && !selectedVenueId.isEmpty() && selectedDate != null
                && !eventName.isEmpty() && !ticketPrice.isEmpty() && organizerId != null) {
            // Create event data
            Map<String, Object> event = new HashMap<>();
            event.put("venueId", selectedVenueId);
            event.put("date", selectedDate);
            event.put("name", eventName); // Add event name
            event.put("ticketPrice", ticketPrice); // Add ticket price
            event.put("organizerId", organizerId); // Use the actual organizer ID

            // Store event in Firebase
            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
            eventsRef.push().setValue(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}