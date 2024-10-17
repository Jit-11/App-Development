package com.example.eventapplication;

public class Event {
    private String date;
    private String name;
    private String imageUrl; // Assuming this will be added later or is not in the provided structure.

    // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    public Event() {
    }

    public Event(String date, String name, String imageUrl) {
        this.date = date;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    /*private String eventId;
    private String eventName;
    private String eventDate;
    private String eventVenue;
    private int eventCapacity;
    private String organizerId; // Add organizer ID field

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String eventId, String eventName, String eventDate, String eventVenue, int eventCapacity, String organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventVenue = eventVenue;
        this.eventCapacity = eventCapacity;
        this.organizerId = organizerId; // Initialize organizer ID
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }*/
}
