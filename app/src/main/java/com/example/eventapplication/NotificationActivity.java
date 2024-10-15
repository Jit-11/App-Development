package com.example.eventapplication;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private ImageView closeIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();

        // For demonstration, adding static data
        notificationList.add(new Notification("Event Reminder","Your event starts in 1 hour."));
        notificationList.add(new Notification("New Event", "New event added near you."));
        notificationList.add(new Notification("Event Update", "The event has been rescheduled."));
        // Initialize the adapter and set it to the RecyclerView
        notificationAdapter = new NotificationAdapter(notificationList);
        notificationRecyclerView.setAdapter(notificationAdapter);

        closeIcon = findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> {
            finish();
        });

    }
}