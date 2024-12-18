package com.example.bloodbond;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.Model.DonationSite;

public class DonationSiteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_site_detail);

        // Retrieve the donation site and user role
        DonationSite donationSite = (DonationSite) getIntent().getSerializableExtra("donationSite");
        String userRole = getIntent().getStringExtra("userRole");

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
        }

        // Set button click listeners
        donorRegisterButton.setOnClickListener(v -> {
            // Handle donor registration logic
        });

        volunteerRegisterButton.setOnClickListener(v -> {
            // Handle volunteer registration logic
        });
    }
}
