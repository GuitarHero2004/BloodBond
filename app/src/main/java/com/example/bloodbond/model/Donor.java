package com.example.bloodbond.model;

import java.util.List;

public class Donor extends UserModel {
    private String bloodType;
    private String lastDonationDate;
    private int donationCount;
    private List<DonationSite> donationSitesRegistered;

    public Donor(String name, String dateOfBirth, String email, String role, String bloodType, String lastDonationDate, int donationCount, List<DonationSite> donationSitesRegistered) {
        super(name, dateOfBirth, email, role);
        this.bloodType = bloodType;
        this.lastDonationDate = lastDonationDate;
        this.donationCount = donationCount;
        this.donationSitesRegistered = donationSitesRegistered;
    }

    // Default constructor required for Firestore
    public Donor() {
        super();
        this.bloodType = "";
        this.lastDonationDate = "";
        this.donationCount = 0;
        this.donationSitesRegistered = null;
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

    public List<DonationSite> getDonationSitesRegistered() {
        return donationSitesRegistered;
    }

    public void setDonationSitesRegistered(List<DonationSite> donationSitesRegistered) {
        this.donationSitesRegistered = donationSitesRegistered;
    }
}
