package com.example.bloodbond;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.example.bloodbond.model.Donor;
import com.example.bloodbond.model.SiteManager;

public class DonationSiteDetailActivity extends AppCompatActivity {
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
        } else if ("siteManagers".equals(userRole)) {
            donorRegisterButton.setVisibility(View.GONE);
        } else {
            donorRegisterButton.setVisibility(View.GONE);
            volunteerRegisterButton.setVisibility(View.GONE);
        }

        // Set button click listeners
        donorRegisterButton.setOnClickListener(v -> registerDonor());
        volunteerRegisterButton.setOnClickListener(v -> registerVolunteer());
    }

    private void registerDonor() {
        if (userId == null) {
            Toast.makeText(this, "Error: User ID is missing", Toast.LENGTH_LONG).show();
            return;
        }

        firestoreHelper.fetchDonorData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                Donor donor = (Donor) data;
                donationSite.getRegisteredDonors().add(donor);
                firestoreHelper.updateDonationSite(donationSite, new FirestoreHelper.OnDataOperationListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DonationSiteDetailActivity.this, "Donor registered successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(DonationSiteDetailActivity.this, "Failed to register donor: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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

        firestoreHelper.fetchSiteManagerData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                SiteManager siteManager = (SiteManager) data;
                donationSite.getRegisteredVolunteers().add(siteManager);
                firestoreHelper.updateDonationSite(donationSite, new FirestoreHelper.OnDataOperationListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DonationSiteDetailActivity.this, "Volunteer registered successfully", Toast.LENGTH_SHORT).show();
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
}