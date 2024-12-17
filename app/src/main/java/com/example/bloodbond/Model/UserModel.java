package com.example.bloodbond.Model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String name;
    private String dateOfBirth;
    private String email;
    private String role;

    public UserModel(String name, String dateOfBirth, String email, String role) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.role = role;
    }

    // Default constructor required for Firestore
    public UserModel() {
        this.name = "";
        this.dateOfBirth = "";
        this.email = "";
        this.role = "";
    }

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
}

