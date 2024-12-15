package com.example.bloodbond.Model;

public class Donor extends User {
    private String bloodType;
    private String lastDonationDate;

    public Donor(String name, String dateOfBirth, String email, String role, String phoneNumber, String bloodGroup, String lastDonationDate) {
        super(name, dateOfBirth, email, role, phoneNumber);
        this.bloodType = bloodGroup;
        this.lastDonationDate = lastDonationDate;
    }

    // Default constructor required for Firestore
    public Donor() {}

    // Getters and setters
    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(String lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }
}
