package com.example.eventapplication;

public class Notification {
    private String title;
    private String description;

    // Default Constructor
    public Notification() {
        this.title = "Event Notification";
        this.description = "You have a new notification.";
    }

    // Parameterized Constructor
    public Notification(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
