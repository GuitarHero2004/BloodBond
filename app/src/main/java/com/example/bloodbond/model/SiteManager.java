package com.example.bloodbond.model;

import java.util.ArrayList;
import java.util.List;

public class SiteManager extends UserModel {
    private String phoneNumber;
    private List<String> sitesManagedNames;

    public SiteManager(String userId, String name, String dateOfBirth, String email, String role, String phoneNumber, List<String> sitesManagedNames) {
        super(userId, name, dateOfBirth, email, role);
        this.phoneNumber = phoneNumber;
        this.sitesManagedNames = sitesManagedNames != null ? sitesManagedNames : new ArrayList<>();
    }

    // Default constructor required for Firestore
    public SiteManager() {
        super();
        this.phoneNumber = "";
        this.sitesManagedNames = new ArrayList<>();
    }

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getSitesManagedNames() {
        return sitesManagedNames;
    }
    public void setSitesManagedNames(List<String> sitesManagedNames) {
        this.sitesManagedNames = sitesManagedNames;
    }
}
