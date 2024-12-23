package com.example.bloodbond.model;

import java.util.ArrayList;
import java.util.List;

public class SiteManager extends UserModel {
    private String phoneNumber;
    private List<DonationSite> sitesManaged;

    public SiteManager(String name, String dateOfBirth, String email, String role, String phoneNumber, List<DonationSite> sitesManaged) {
        super(name, dateOfBirth, email, role);
        this.phoneNumber = phoneNumber;
        this.sitesManaged = sitesManaged != null ? sitesManaged : new ArrayList<>();
    }

    // Default constructor required for Firestore
    public SiteManager() {
        super();
        this.phoneNumber = "";
        this.sitesManaged = new ArrayList<>();
    }

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<DonationSite> getSitesManaged() {
        return sitesManaged;
    }

    public void setSitesManaged(List<DonationSite> sitesManaged) {
        this.sitesManaged = sitesManaged;
    }
}
