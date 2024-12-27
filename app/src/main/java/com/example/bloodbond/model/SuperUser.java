package com.example.bloodbond.model;

import java.util.List;

public class SuperUser extends UserModel{
    private boolean canGenerateReports;

    public SuperUser() {
        super();
        this.canGenerateReports = false;
    }

    public SuperUser(String userId, String name, String dateOfBirth, String email, String role, boolean canGenerateReports) {
        super(userId, name, dateOfBirth, email, role);
        this.canGenerateReports = canGenerateReports;
    }

    public boolean isCanGenerateReports() {
        return canGenerateReports;
    }

    public void setCanGenerateReports(boolean canGenerateReports) {
        this.canGenerateReports = canGenerateReports;
    }
}
