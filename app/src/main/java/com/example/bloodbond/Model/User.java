package com.example.bloodbond.Model;

public class User {
    private String name;
    private String dateOfBirth;
    private String email;
    private String role;
    private String phoneNumber;

    public User(String name, String dateOfBirth, String email, String role, String phoneNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Default constructor required for Firestore
    public User() {}

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

