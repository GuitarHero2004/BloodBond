package com.example.bloodbond;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.example.bloodbond.model.Donor;
import com.example.bloodbond.model.SiteManager;

import java.util.List;

public class DonationSiteDetailActivity extends AppCompatActivity {
    private static final int UPDATE_DONATION_SITE_REQUEST_CODE = 1;
    private final AuthHelper authHelper = new AuthHelper();
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private DonationSite donationSite;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_site_detail);

        // Retrieve the donation site and user role
        donationSite = (DonationSite) getIntent().getSerializableExtra("donationSite");
        String userRole = getIntent().getStringExtra("userRole");
        userId = authHelper.getUserId();

        // Check if donationSite is null and handle the error
        if (donationSite == null) {
            Toast.makeText(this, "Error: Donation site data is missing", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if data is missing
            return;
        }

        // Initialize UI components
        TextView siteName = findViewById(R.id.donationSiteName);
        TextView siteAddress = findViewById(R.id.donationSiteAddress);
        TextView siteContact = findViewById(R.id.donationSiteContact);
        TextView siteDateOpened = findViewById(R.id.donationSiteDateOpened);
        TextView siteDateClosed = findViewById(R.id.donationSiteDateClosed);
        TextView siteWorkingHours = findViewById(R.id.donationSiteOpeningHours);
        TextView siteClosingHours = findViewById(R.id.donationSiteClosingHours);
        TextView siteDescription = findViewById(R.id.donationSiteDescription);
        TextView bloodTypes = findViewById(R.id.bloodTypesNeeded);
        Button donorRegisterButton = findViewById(R.id.donationSiteDonorRegisterButton);
        Button volunteerRegisterButton = findViewById(R.id.donationSiteVolunteerRegisterButton);
        Button viewRegisteredDonorsButton = findViewById(R.id.viewRegisteredDonorsButton);
        Button viewRegisteredVolunteersButton = findViewById(R.id.viewRegisteredVolunteersButton);
        Button editButton = findViewById(R.id.editDonationSiteButton);
        Button deleteButton = findViewById(R.id.deleteDonationSiteButton);

        // Populate the details
        siteName.setText(donationSite.getSiteName());
        siteAddress.setText(donationSite.getAddress());
        siteContact.setText(donationSite.getPhoneNumber());
        siteDateOpened.setText(donationSite.getDateOpened());
        siteDateClosed.setText(donationSite.getDateClosed());
        siteWorkingHours.setText(donationSite.getOpeningHours());
        siteClosingHours.setText(donationSite.getClosingHours());
        siteDescription.setText(donationSite.getDescription());
        bloodTypes.setText(donationSite.getBloodTypes());

        // Show/hide buttons based on user role
        if ("donors".equals(userRole)) {
            volunteerRegisterButton.setVisibility(View.GONE);
            viewRegisteredDonorsButton.setVisibility(View.GONE);
            viewRegisteredVolunteersButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else if ("siteManagers".equals(userRole)) {
            donorRegisterButton.setVisibility(View.GONE);
        } else {
            donorRegisterButton.setVisibility(View.GONE);
            volunteerRegisterButton.setVisibility(View.GONE);
            viewRegisteredDonorsButton.setVisibility(View.GONE);
            viewRegisteredVolunteersButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        // Set button click listeners
        donorRegisterButton.setOnClickListener(v -> registerDonor());
        volunteerRegisterButton.setOnClickListener(v -> registerVolunteer());
        viewRegisteredDonorsButton.setOnClickListener(v -> viewRegisteredDonors());
        viewRegisteredVolunteersButton.setOnClickListener(v -> viewRegisteredVolunteers());
        editButton.setOnClickListener(v -> {
            // Open the UpdateDonationSiteDetailActivity with the donation site data
            Intent intent = new Intent(DonationSiteDetailActivity.this, UpdateDonationSiteDetailActivity.class);
            intent.putExtra("donationSite", donationSite);
            startActivityForResult(intent, UPDATE_DONATION_SITE_REQUEST_CODE);
        });
        deleteButton.setOnClickListener(v -> deleteDonationSite());
    }

    private void showBottomSheetDialog(String title, String content) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_info, null);
        TextView bottomSheetTitle = bottomSheetView.findViewById(R.id.bottomSheetTitle);
        TextView bottomSheetContent = bottomSheetView.findViewById(R.id.bottomSheetContent);

        bottomSheetTitle.setText(title);
        bottomSheetContent.setText(content);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void viewRegisteredDonors() {
        StringBuilder donorNames = new StringBuilder();
        for (Donor donor : donationSite.getRegisteredDonors()) {
            donorNames.append(donor.getName()).append("\n");
        }
        showBottomSheetDialog("Registered Donors:", donorNames.toString());
    }

    private void viewRegisteredVolunteers() {
        StringBuilder volunteerNames = new StringBuilder();
        for (SiteManager volunteer : donationSite.getRegisteredVolunteers()) {
            volunteerNames.append(volunteer.getName()).append("\n");
        }
        showBottomSheetDialog("Registered Volunteers:", volunteerNames.toString());
    }

    private void deleteDonationSite() {
        firestoreHelper.deleteDonationSiteData(donationSite.getSiteId(), new FirestoreHelper.OnDataOperationListener() {
            @Override
            public void onSuccess() {
                // Remove the site name from the site manager's sitesManagedNames list
                firestoreHelper.fetchSiteManagerData(userId, new FirestoreHelper.OnUserDataFetchListener() {
                    @Override
                    public void onSuccess(Object data) {
                        SiteManager siteManager = (SiteManager) data;
                        List<String> sitesManagedNames = siteManager.getSitesManagedNames();
                        sitesManagedNames.remove(donationSite.getSiteName());

                        firestoreHelper.updateSiteManagerSitesManaged(userId, sitesManagedNames, new FirestoreHelper.OnDataOperationListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(DonationSiteDetailActivity.this, "Donation site deleted successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close the activity after deleting the donation site
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(DonationSiteDetailActivity.this, "Failed to update sites managed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(DonationSiteDetailActivity.this, "Failed to fetch site manager data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DonationSiteDetailActivity.this, "Failed to delete donation site: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserAlreadyRegisteredAsDonor() {
        for (Donor donor : donationSite.getRegisteredDonors()) {
            if (donor.getUserId() != null && donor.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserAlreadyRegisteredAsVolunteer() {
        for (SiteManager volunteer : donationSite.getRegisteredVolunteers()) {
            if (volunteer.getUserId() != null && volunteer.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private void registerDonor() {
        if (userId == null) {
            Toast.makeText(this, "Error: User ID is missing", Toast.LENGTH_LONG).show();
            return;
        }

        if (isUserAlreadyRegisteredAsDonor()) {
            Toast.makeText(this, "You are already registered as a donor at this site", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreHelper.fetchDonorData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                Donor donor = (Donor) data;
                String donorBloodType = donor.getBloodType();
                String requiredBloodTypes = donationSite.getBloodTypes();

                if (requiredBloodTypes.contains(donorBloodType)) {
                    donationSite.getRegisteredDonors().add(donor);
                    firestoreHelper.updateDonationSite(donationSite, new FirestoreHelper.OnDataOperationListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(DonationSiteDetailActivity.this, "Donor registered successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after registering the donor
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(DonationSiteDetailActivity.this, "Failed to register donor: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(DonationSiteDetailActivity.this, "Your blood type does not match the required blood types for this donation site", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DonationSiteDetailActivity.this, "Failed to fetch donor data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerVolunteer() {
        if (userId == null) {
            Toast.makeText(this, "Error: User ID is missing", Toast.LENGTH_LONG).show();
            return;
        }

        if (isUserAlreadyRegisteredAsVolunteer()) {
            Toast.makeText(this, "You are already registered as a volunteer at this site", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreHelper.fetchSiteManagerData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                SiteManager siteManager = (SiteManager) data;
                donationSite.getRegisteredVolunteers().add(siteManager);
                firestoreHelper.updateDonationSite(donationSite, new FirestoreHelper.OnDataOperationListener() {
                    @Override
                    public void onSuccess() {
                        updateSiteManagerSitesManaged(siteManager);
                        Toast.makeText(DonationSiteDetailActivity.this, "Volunteer registered successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after registering the volunteer
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(DonationSiteDetailActivity.this, "Failed to register volunteer: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DonationSiteDetailActivity.this, "Failed to fetch site manager data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSiteManagerSitesManaged(SiteManager siteManager) {
        List<String> sitesManagedNames = siteManager.getSitesManagedNames();
        String oldSiteName = donationSite.getSiteName();
        String newSiteName = donationSite.getSiteName();

        // Replace the old site name with the new site name
        for (int i = 0; i < sitesManagedNames.size(); i++) {
            if (sitesManagedNames.get(i).equals(oldSiteName)) {
                sitesManagedNames.set(i, newSiteName);
                break;
            }
        }

        firestoreHelper.updateSiteManagerSitesManaged(userId, sitesManagedNames, new FirestoreHelper.OnDataOperationListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(DonationSiteDetailActivity.this, "Sites managed updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after updating the sites managed
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(DonationSiteDetailActivity.this, "Failed to update sites managed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_DONATION_SITE_REQUEST_CODE && resultCode == RESULT_OK) {
            DonationSite updatedDonationSite = (DonationSite) data.getSerializableExtra("updatedDonationSite");
            if (updatedDonationSite != null) {
                // Update the UI with the updated donation site data
                TextView siteName = findViewById(R.id.donationSiteName);
                TextView siteAddress = findViewById(R.id.donationSiteAddress);
                TextView siteContact = findViewById(R.id.donationSiteContact);
                TextView siteDateOpened = findViewById(R.id.donationSiteDateOpened);
                TextView siteDateClosed = findViewById(R.id.donationSiteDateClosed);
                TextView siteWorkingHours = findViewById(R.id.donationSiteOpeningHours);
                TextView siteClosingHours = findViewById(R.id.donationSiteClosingHours);
                TextView siteDescription = findViewById(R.id.donationSiteDescription);
                TextView bloodTypes = findViewById(R.id.bloodTypesNeeded);

                siteName.setText(updatedDonationSite.getSiteName());
                siteAddress.setText(updatedDonationSite.getAddress());
                siteContact.setText(updatedDonationSite.getPhoneNumber());
                siteDateOpened.setText(updatedDonationSite.getDateOpened());
                siteDateClosed.setText(updatedDonationSite.getDateClosed());
                siteWorkingHours.setText(updatedDonationSite.getOpeningHours());
                siteClosingHours.setText(updatedDonationSite.getClosingHours());
                siteDescription.setText(updatedDonationSite.getDescription());
                bloodTypes.setText(updatedDonationSite.getBloodTypes());

                // Update the donationSite object with the new data
                donationSite = updatedDonationSite;
            }
        }
    }
}