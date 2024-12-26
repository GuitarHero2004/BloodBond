package com.example.bloodbond.model;

import java.util.List;

public class Donor extends UserModel {
    private String bloodType;
    private int donationCount;

    public Donor(String userId, String name, String dateOfBirth, String email, String role, String bloodType, int donationCount) {
        super(userId, name, dateOfBirth, email, role);
        this.bloodType = bloodType;
        this.donationCount = donationCount;
    }

    // Default constructor required for Firestore
    public Donor() {
        super();
        this.bloodType = "";
        this.donationCount = 0;
    }

    // Getters and setters
    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public void setDonationCount(int donationCount) {
        this.donationCount = donationCount;
    }
}
