package com.example.bloodbond.model;

import java.util.List;

public class SiteManager extends UserModel {
    private String phoneNumber;
    private List<String> sitesManaged;

    public SiteManager(String name, String dateOfBirth, String email, String role, String phoneNumber, List<String> sitesManaged) {
        super(name, dateOfBirth, email, role);
        this.phoneNumber = phoneNumber;
        this.sitesManaged = sitesManaged;
    }

    // Default constructor required for Firestore
    public SiteManager() {
        super();
        this.phoneNumber = "";
        this.sitesManaged = null;
    }

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getSitesManaged() {
        return sitesManaged;
    }

    public void setSitesManaged(List<String> sitesManaged) {
        this.sitesManaged = sitesManaged;
    }
}
