package com.example.bloodbond.Model;

import java.util.List;

public class SuperUser extends UserModel{
    private boolean canGenerateReports;
    private List<String> donationSitesList;

    public SuperUser() {
        super();
        this.canGenerateReports = false;
        this.donationSitesList = null;
    }

    public SuperUser(String name, String dateOfBirth, String email, String role, boolean canGenerateReports, List<String> donationSitesList) {
        super(name, dateOfBirth, email, role);
        this.canGenerateReports = canGenerateReports;
        this.donationSitesList = donationSitesList;
    }

    public boolean isCanGenerateReports() {
        return canGenerateReports;
    }

    public void setCanGenerateReports(boolean canGenerateReports) {
        this.canGenerateReports = canGenerateReports;
    }

    public List<String> getDonationSitesList() {
        return donationSitesList;
    }

    public void setDonationSitesList(List<String> donationSitesList) {
        this.donationSitesList = donationSitesList;
    }
}
