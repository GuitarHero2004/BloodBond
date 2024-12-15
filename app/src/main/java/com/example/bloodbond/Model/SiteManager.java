package com.example.bloodbond.Model;

public class SiteManager extends User {
    private String siteName;
    private String siteLocation;

    public SiteManager(String siteName, String siteLocation) {
        this.siteName = siteName;
        this.siteLocation = siteLocation;
    }

    // Default constructor required for Firestore
    public SiteManager() {}

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
