package com.example.bloodbond;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.example.bloodbond.model.SiteManager;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddDonationSiteActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private final AuthHelper authHelper = new AuthHelper();
    private EditText siteName, siteAddress, phoneNumber, dateOpen, dateEnd, openingHours, closingHours, description, bloodAmount;
    private Spinner bloodTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation_site);

        // Initialize Places SDK
        String apiKey = BuildConfig.MAPS_API_KEY;
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Initialize views
        siteName = findViewById(R.id.siteName);
        siteAddress = findViewById(R.id.siteAddress);
        phoneNumber = findViewById(R.id.phoneNumber);
        dateOpen = findViewById(R.id.dateOpen);
        dateEnd = findViewById(R.id.dateEnd);
        openingHours = findViewById(R.id.openingHours);
        closingHours = findViewById(R.id.closingHours);
        description = findViewById(R.id.description);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        bloodAmount = findViewById(R.id.bloodAmountNeeded);

        // Set up Places Autocomplete
        siteAddress.setOnClickListener(v -> openPlaceAutocomplete());

        // Set the phone number from Intent if available
        setPhoneNumberFromIntent();

        // Set button click listener for saving donation site
        setSaveDonationSiteButtonListener();

        // Set onClickListeners for date and time fields
        setDateAndTimePickers();
    }

    private void setPhoneNumberFromIntent() {
        String siteManagerPhoneNumber = getIntent().getStringExtra("phoneNumber");
        if (siteManagerPhoneNumber != null) {
            phoneNumber.setText(siteManagerPhoneNumber);
        }
    }

    private void setSaveDonationSiteButtonListener() {
        LinearLayout customAddDonationSiteButton = findViewById(R.id.customAddDonationSiteButton);
        customAddDonationSiteButton.setOnClickListener(v -> {
            if (isFormValid()) {
                DonationSite donationSite = createDonationSite();
                storeDonationSite(donationSite);
            } else {
                Toast.makeText(AddDonationSiteActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDateAndTimePickers() {
        dateOpen.setOnClickListener(v -> showDatePickerDialog(dateOpen));
        dateEnd.setOnClickListener(v -> showDatePickerDialog(dateEnd));
        openingHours.setOnClickListener(v -> showTimePickerDialog(openingHours));
        closingHours.setOnClickListener(v -> showTimePickerDialog(closingHours));
    }

    private boolean isFormValid() {
        boolean isValid = true;

        // Validate if the start date is from the current date onwards
        String startDate = dateOpen.getText().toString();
        if (isStartDateValid(startDate)) {
            isValid = false;
            Toast.makeText(AddDonationSiteActivity.this, "Start date must be today or later", Toast.LENGTH_SHORT).show();
        }

        // Validate if the end date is not before the start date
        String endDate = dateEnd.getText().toString();
        if (!isEndDateValid(startDate, endDate)) {
            isValid = false;
            Toast.makeText(AddDonationSiteActivity.this, "End date cannot be before the start date", Toast.LENGTH_SHORT).show();
        }

        // Validate if opening and closing hours are within the allowed range
        String openingHour = openingHours.getText().toString();
        String closingHour = closingHours.getText().toString();
        if (!areOpeningClosingHoursValid(openingHour, closingHour)) {
            isValid = false;
            Toast.makeText(AddDonationSiteActivity.this, "Opening and closing hours must be between 7:00 AM and 5:00 PM, and closing cannot be before opening", Toast.LENGTH_SHORT).show();
        }

        // Validate the rest of the form fields
        isValid = isValid && !siteName.getText().toString().isEmpty() &&
                !siteAddress.getText().toString().isEmpty() &&
                !bloodTypeSpinner.getSelectedItem().toString().isEmpty() &&
                !phoneNumber.getText().toString().isEmpty() &&
                !dateOpen.getText().toString().isEmpty() &&
                !dateEnd.getText().toString().isEmpty() &&
                !openingHours.getText().toString().isEmpty() &&
                !closingHours.getText().toString().isEmpty() &&
                !description.getText().toString().isEmpty() && !bloodAmount.getText().toString().isEmpty();

        return isValid;
    }

    private boolean isStartDateValid(String startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            // Parse the start date entered by the user
            Date start = sdf.parse(startDate);

            // Get the current date
            Calendar currentDateCalendar = Calendar.getInstance();
            currentDateCalendar.set(Calendar.HOUR_OF_DAY, 0);  // Reset time to 00:00
            currentDateCalendar.set(Calendar.MINUTE, 0);
            currentDateCalendar.set(Calendar.SECOND, 0);
            currentDateCalendar.set(Calendar.MILLISECOND, 0);

            // Compare only date (without time) part
            if (start != null && start.before(currentDateCalendar.getTime())) {
                return true;  // Invalid if start date is before today
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;  // Valid if date is today or later
    }

    private boolean isEndDateValid(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && (start == null || !end.before(start)); // End date must not be before start date
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean areOpeningClosingHoursValid(String openingHour, String closingHour) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date openTime = timeFormat.parse(openingHour);
            Date closeTime = timeFormat.parse(closingHour);

            // Define valid time range (7:00 AM - 5:00 PM)
            Date validOpenTime = timeFormat.parse("07:00");
            Date validCloseTime = timeFormat.parse("17:00");

            if (openTime == null || closeTime == null) {
                return false;
            }

            return !openTime.before(validOpenTime) && !closeTime.after(validCloseTime) && closeTime.after(openTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private DonationSite createDonationSite() {
        String name = siteName.getText().toString();
        String address = siteAddress.getText().toString();
        String phone = phoneNumber.getText().toString();
        String dateOpened = dateOpen.getText().toString();
        String dateClosed = dateEnd.getText().toString();
        String siteOpeningHours = openingHours.getText().toString();
        String siteClosingHours = closingHours.getText().toString();
        String desc = description.getText().toString();
        String bloodTypes = bloodTypeSpinner.getSelectedItem().toString();
        int bloodAmountNeeded = Integer.parseInt(bloodAmount.getText().toString());

        double latitude = 0.0;
        double longitude = 0.0;

        // Retrieve latitude and longitude from the siteAddress field's tag if available
        if (siteAddress.getTag() instanceof double[]) {
            double[] latLng = (double[]) siteAddress.getTag();
            latitude = latLng[0];
            longitude = latLng[1];
        }

        String siteManagerId = authHelper.getUserId();

        return new DonationSite(name, siteManagerId, address, phone, dateOpened, dateClosed, siteOpeningHours, siteClosingHours, desc, bloodTypes, latitude, longitude, bloodAmountNeeded, 0.0, null, null);
    }

    private void storeDonationSite(DonationSite donationSite) {
        firestoreHelper.storeDonationSiteData(donationSite, new FirestoreHelper.OnDataOperationListener() {
            @Override
            public void onSuccess() {
                updateSiteManagerWithNewSite(donationSite);
                Toast.makeText(AddDonationSiteActivity.this, "Donation Site added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddDonationSiteActivity.this, "Failed to add Donation Site: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSiteManagerWithNewSite(DonationSite donationSite) {
        // Retrieve the SiteManager's ID (e.g., from the current authenticated user)
        String siteManagerId = authHelper.getUserId();

        firestoreHelper.fetchSiteManagerData(siteManagerId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                SiteManager siteManager = (SiteManager) data;

                // Add the new donation site to the sitesManaged list
                List<String> updatedSitesManagedNames = siteManager.getSitesManagedNames();
                updatedSitesManagedNames.add(donationSite.getSiteName());

                // Update the SiteManager document with the new sitesManaged list
                firestoreHelper.updateSiteManagerSitesManaged(siteManagerId, updatedSitesManagedNames, new FirestoreHelper.OnDataOperationListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AddDonationSiteActivity.this, "Donation Site added and SiteManager updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(AddDonationSiteActivity.this, "Failed to update SiteManager: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddDonationSiteActivity.this, "Failed to fetch SiteManager data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            editText.setText(date);
        }, year, month, day);

        // Disable past dates
        calendar.add(Calendar.DATE, 0);  // Set to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void openPlaceAutocomplete() {
        // Initialize and start the place autocomplete activity
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the place details from the returned intent
            Place place = Autocomplete.getPlaceFromIntent(data);
            siteAddress.setText(place.getAddress());

            // Store latitude and longitude in the tag for later use
            siteAddress.setTag(new double[]{place.getLatLng().latitude, place.getLatLng().longitude});
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("AddDonationSite", "Error: " + status.getStatusMessage());
        }
    }
}