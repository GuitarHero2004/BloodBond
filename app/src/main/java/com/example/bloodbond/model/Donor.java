package com.example.bloodbond.model;

import java.util.List;
import java.util.Random;

public class Donor extends UserModel {
    private String bloodType;
    private double bloodAmountDonated;

    public Donor(String userId, String name, String dateOfBirth, String email, String role, String bloodType, double bloodAmountDonated) {
        super(userId, name, dateOfBirth, email, role);
        this.bloodType = bloodType;
        this.bloodAmountDonated = bloodAmountDonated;
    }

    // Default constructor required for Firestore
    public Donor() {
        super();
        this.bloodType = "";
        this.bloodAmountDonated = 0.0;
    }

    // Getters and setters
    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public double getBloodAmountDonated() {
        return bloodAmountDonated;
    }

    public void setBloodAmountDonated(double bloodAmountDonated) {
        this.bloodAmountDonated = bloodAmountDonated;
    }
}
