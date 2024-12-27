package com.example.bloodbond;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

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
        Button viewBloodAmountCollectedButton = findViewById(R.id.viewBloodAmountCollectedButton);
        Button generateReportButton = findViewById(R.id.generateReportButton);
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
            viewBloodAmountCollectedButton.setVisibility(View.GONE);
            generateReportButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else if ("siteManagers".equals(userRole)) {
            donorRegisterButton.setVisibility(View.GONE);
            generateReportButton.setVisibility(View.GONE);
        } else if ("superUsers".equals(userRole)) {
            donorRegisterButton.setVisibility(View.GONE);
            volunteerRegisterButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
            donorRegisterButton.setVisibility(View.GONE);
            volunteerRegisterButton.setVisibility(View.GONE);
            viewRegisteredDonorsButton.setVisibility(View.GONE);
            viewRegisteredVolunteersButton.setVisibility(View.GONE);
            viewBloodAmountCollectedButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        // Set button click listeners
        donorRegisterButton.setOnClickListener(v -> registerDonor());
        volunteerRegisterButton.setOnClickListener(v -> registerVolunteer());
        viewRegisteredDonorsButton.setOnClickListener(v -> viewRegisteredDonors());
        viewRegisteredVolunteersButton.setOnClickListener(v -> viewRegisteredVolunteers());
        viewBloodAmountCollectedButton.setOnClickListener(v -> viewBloodAmountCollected());
        generateReportButton.setOnClickListener(v -> generateReport());
        editButton.setOnClickListener(v -> {
            // Open the UpdateDonationSiteDetailActivity with the donation site data
            Intent intent = new Intent(DonationSiteDetailActivity.this, UpdateDonationSiteDetailActivity.class);
            intent.putExtra("donationSite", donationSite);
            startActivityForResult(intent, UPDATE_DONATION_SITE_REQUEST_CODE);
        });
        deleteButton.setOnClickListener(v -> deleteDonationSite());
    }

    private void generateReport() {
        // Generate a report with the donation site data
        String report = "Donation Site Name: " + donationSite.getSiteName() + "\n" +
                "Address: " + donationSite.getAddress() + "\n" +
                "Phone Contact: " + donationSite.getPhoneNumber() + "\n" +
                "Operation Date: " + donationSite.getDateOpened() + " - "+ donationSite.getDateClosed() + "\n" +
                "Operation Hours: " + donationSite.getOpeningHours() + " - " + donationSite.getClosingHours() + "\n" +
                "Description: " + donationSite.getDescription() + "\n" +
                "Blood Types Needed: " + donationSite.getBloodTypes() + "\n" +
                "Blood Amount Collected: " + donationSite.getBloodAmountCollected() + " ml/" + donationSite.getBloodAmountNeeded() + " ml"+ "\n" +
                "Number of Registered Donors: " + donationSite.getRegisteredDonors().size() + "\n" +
                "Number of Registered Volunteers: " + donationSite.getRegisteredVolunteers().size();
        showBottomSheetDialog("Donation Site Report", report);
    }

    private void viewBloodAmountCollected() {
        String bloodAmountCollected = String.valueOf(donationSite.getBloodAmountCollected());
        String bloodAmountNeeded = String.valueOf(donationSite.getBloodAmountNeeded());
        showBottomSheetDialog("Blood Amount Collected:", "Collected: " + bloodAmountCollected + " ml/" + bloodAmountNeeded + " ml");
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

    private void showBottomSheetDialog(String title, String content) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_info, null);
        TextView bottomSheetTitle = bottomSheetView.findViewById(R.id.bottomSheetTitle);
        TextView bottomSheetContent = bottomSheetView.findViewById(R.id.bottomSheetContent);
        Button saveToPdfButton = bottomSheetView.findViewById(R.id.saveAsPdfButton);

        bottomSheetTitle.setText(title);
        bottomSheetContent.setText(content);

        saveToPdfButton.setOnClickListener(v -> saveContentToPdf(title, content));

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void saveContentToPdf(String title, String content) {
        PdfDocument pdfDocument = getPdfDocument(title, content);

        // Save the PDF file
        String filePath = getExternalFilesDir(null) + "/report.pdf";
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "PDF saved to " + filePath, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }

    private PdfDocument getPdfDocument(String title, String content) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Set title style
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        int x = 40, y = 50;
        canvas.drawText(title, x, y, paint);

        // Draw a line below the title
        paint.setStrokeWidth(2);
        canvas.drawLine(x, y + 10, pageInfo.getPageWidth() - 40, y + 10, paint);

        // Set content style
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        y += 40;

        for (String line : content.split("\n")) {
            y += (int) (paint.descent() - paint.ascent());
            canvas.drawText(line, x, y, paint);
        }

        // Add footer
        paint.setTextSize(10);
        y = pageInfo.getPageHeight() - 40;
        canvas.drawText("Generated by BloodBond App", x, y, paint);

        pdfDocument.finishPage(page);
        return pdfDocument;
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

    // Java
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
                    // Generate random blood amount donated
                    double bloodAmountDonated = generateRandomBloodAmount();
                    donor.setBloodAmountDonated(bloodAmountDonated);

                    // Update the blood amount collected
                    double newBloodAmountCollected = donationSite.getBloodAmountCollected() + bloodAmountDonated;
                    donationSite.setBloodAmountCollected(newBloodAmountCollected);

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

    private double generateRandomBloodAmount() {
        Random random = new Random();
        int randomInt = 5 + random.nextInt(11); // Generates a random integer between 5 and 15
        return randomInt * 10.0; // Converts to a value like 50.0, 60.0, ..., 150.0
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