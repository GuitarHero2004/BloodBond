package com.example.bloodbond.Model;

public class Donor extends UserModel {
    private String bloodType;
    private String lastDonationDate;
    private int donationCount;

    public Donor(String name, String dateOfBirth, String email, String role, String bloodType, String lastDonationDate, int donationCount) {
        super(name, dateOfBirth, email, role);
        this.bloodType = bloodType;
        this.lastDonationDate = lastDonationDate;
        this.donationCount = donationCount;
    }

    // Default constructor required for Firestore
    public Donor() {
        super();
        this.bloodType = "";
        this.lastDonationDate = "";
        this.donationCount = 0;
    }

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

    public int getDonationCount() {
        return donationCount;
    }

    public void setDonationCount(int donationCount) {
        this.donationCount = donationCount;
    }
}
