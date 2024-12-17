package com.example.bloodbond.Model;

import java.util.ArrayList;
import java.util.List;

public class DonationSite {
    private String name;
    private String address;
    private String bloodTypes;
    private String openingHours;
    private String closingHours;
    private String phoneNumber;
    private String description;
    private String dateOpened;
    private String dateClosed;
    private List<Donor> registeredDonors = new ArrayList<>();
    private List<SiteManager> registeredVolunteers = new ArrayList<>();

    public DonationSite(String name, String address, String bloodTypes, String openingHours, String closingHours, String phoneNumber, String description, String dateOpened, String dateClosed) {
        this.name = name;
        this.address = address;
        this.bloodTypes = bloodTypes;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.dateOpened = dateOpened;
        this.dateClosed = dateClosed;
    }

    // Default constructor required for Firestore
    public DonationSite() {}

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<com.example.bloodbond.Model.Donor> getRegisteredDonors() {
        return registeredDonors;
    }

    public void setRegisteredDonors(List<com.example.bloodbond.Model.Donor> registeredDonors) {
        this.registeredDonors = registeredDonors;
    }

    public List<com.example.bloodbond.Model.SiteManager> getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(List<com.example.bloodbond.Model.SiteManager> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }
}