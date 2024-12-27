package com.example.bloodbond.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DonationSite implements Serializable {
    private String siteId;
    private String siteManagerId;
    private String siteName;
    private String address;
    private String phoneNumber;
    private String dateOpened;
    private String dateClosed;
    private String openingHours;
    private String closingHours;
    private String description;
    private String bloodTypes;
    private double latitude;
    private double longitude;
    private double bloodAmountNeeded;
    private double bloodAmountCollected;
    private List<Donor> registeredDonors = new ArrayList<>();
    private List<SiteManager> registeredVolunteers = new ArrayList<>();

    public DonationSite(String siteName, String siteManagerId, String address, String phoneNumber, String dateOpened, String dateClosed, String openingHours, String closingHours, String description, String bloodTypes, double latitude, double longitude, double bloodAmountNeeded, double bloodAmountCollected, List<Donor> registeredDonors, List<SiteManager> registeredVolunteers) {
        this.siteName = siteName;
        this.siteManagerId = siteManagerId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOpened = dateOpened;
        this.dateClosed = dateClosed;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.description = description;
        this.bloodTypes = bloodTypes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bloodAmountNeeded = bloodAmountNeeded;
        this.bloodAmountCollected = bloodAmountCollected;
        this.registeredDonors = registeredDonors != null ? registeredDonors : new ArrayList<>();
        this.registeredVolunteers = registeredVolunteers != null ? registeredVolunteers : new ArrayList<>();
    }

    // Default constructor required for Firestore
    public DonationSite() {}

    // Getters and setters
    public String getSiteId() {
        return siteId;
    }
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    public String getSiteManagerId() {
        return siteManagerId;
    }
    public void setSiteManagerId(String siteManagerId) {
        this.siteManagerId = siteManagerId;
    }
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodTypes() {
        return bloodTypes;
    }

    public void setBloodTypes(String bloodTypes) {
        this.bloodTypes = bloodTypes;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(String closingHours) {
        this.closingHours = closingHours;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public double getBloodAmountNeeded() {
        return bloodAmountNeeded;
    }
    public void setBloodAmountNeeded(double bloodAmountNeeded) {
        this.bloodAmountNeeded = bloodAmountNeeded;
    }

    public double getBloodAmountCollected() {
        return bloodAmountCollected;
    }

    public void setBloodAmountCollected(double bloodAmountCollected) {
        this.bloodAmountCollected = bloodAmountCollected;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Donor> getRegisteredDonors() {
        return registeredDonors;
    }

    public void setRegisteredDonors(List<Donor> registeredDonors) {
        this.registeredDonors = registeredDonors;
    }

    public List<SiteManager> getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(List<SiteManager> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }
}