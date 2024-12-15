package com.example.bloodbond.Model;

public class SiteManagers extends User {
    private String siteName;
    private String siteLocation;

    public SiteManagers(String name, String dateOfBirth, String email, String role, String phoneNumber, String siteName, String siteLocation) {
        super(name, dateOfBirth, email, role, phoneNumber);
        this.siteName = siteName;
        this.siteLocation = siteLocation;
    }

    // Default constructor required for Firestore
    public SiteManagers() {}

    // Getters and setters
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public void setSiteLocation(String siteLocation) {
        this.siteLocation = siteLocation;
    }
}
