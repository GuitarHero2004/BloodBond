package com.example.bloodbond.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DonationSite implements Serializable {
    private String siteName;
    private String address;
    private String phoneNumber;
    private String dateOpened;
    private String dateClosed;
    private String openingHours;
    private String closingHours;
    private String description;
    private String bloodTypes;
    private List<String> registeredDonors = new ArrayList<>();
    private List<String> registeredVolunteers = new ArrayList<>();

    public DonationSite(String siteName, String address, String phoneNumber, String dateOpened, String dateClosed, String openingHours, String closingHours, String description, String bloodTypes) {
        this.siteName = siteName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOpened = dateOpened;
        this.dateClosed = dateClosed;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.description = description;
        this.bloodTypes = bloodTypes;
    }

    // Default constructor required for Firestore
    public DonationSite() {}

    // Getters and setters
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

    public List<String> getRegisteredDonors() {
        return registeredDonors;
    }

    public void setRegisteredDonors(List<String> registeredDonors) {
        this.registeredDonors = registeredDonors;
    }

    public List<String> getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(List<String> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }
}