package com.example.eventapplication;

public class HelperClass {

    String name,email,password;
    private String profileImageUrl;

    public HelperClass() {
    }

    public HelperClass(String email, String name, String password , String profileImageUrl) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}