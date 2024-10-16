package com.example.eventapplication;

public class Venue {
    private String name;
    private String address;
    private String contactNumber;
    private String capacity;

    public Venue() {}

    public Venue(String name, String address, String contactNumber, String capacity) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCapacity() {
        return capacity;
    }
}
