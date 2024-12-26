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
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateDonationSiteDetailActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private final AuthHelper authHelper = new AuthHelper();
    private EditText siteName, siteAddress, phoneNumber, dateOpen, dateEnd, openingHours, closingHours, description;
    private Spinner bloodTypeSpinner;
    private DonationSite donationSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_donation_site_detail);

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

        // Retrieve the donation site from the intent
        donationSite = (DonationSite) getIntent().getSerializableExtra("donationSite");

        if (donationSite == null) {
            Toast.makeText(this, "Error: Donation site data is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the fields with current data
        populateFields();

        // Set up Places Autocomplete
        siteAddress.setOnClickListener(v -> openPlaceAutocomplete());

        // Set button click listener for updating donation site
        setUpdateDonationSiteButtonListener();

        // Set onClickListeners for date and time fields
        setDateAndTimePickers();
    }

    private void populateFields() {
        siteName.setText(donationSite.getSiteName());
        siteAddress.setText(donationSite.getAddress());
        phoneNumber.setText(donationSite.getPhoneNumber());
        dateOpen.setText(donationSite.getDateOpened());
        dateEnd.setText(donationSite.getDateClosed());
        openingHours.setText(donationSite.getOpeningHours());
        closingHours.setText(donationSite.getClosingHours());
        description.setText(donationSite.getDescription());
        // Set the spinner value for blood types if needed
    }

    private void setUpdateDonationSiteButtonListener() {
        LinearLayout customUpdateDonationSiteButton = findViewById(R.id.customUpdateDonationSiteButton);
        customUpdateDonationSiteButton.setOnClickListener(v -> {
            if (isFormValid()) {
                updateDonationSite();
            } else {
                Toast.makeText(UpdateDonationSiteDetailActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDonationSite() {
        // Update the donation site object with new data
        donationSite.setSiteName(siteName.getText().toString());
        donationSite.setAddress(siteAddress.getText().toString());
        donationSite.setPhoneNumber(phoneNumber.getText().toString());
        donationSite.setDateOpened(dateOpen.getText().toString());
        donationSite.setDateClosed(dateEnd.getText().toString());
        donationSite.setOpeningHours(openingHours.getText().toString());
        donationSite.setClosingHours(closingHours.getText().toString());
        donationSite.setDescription(description.getText().toString());
        donationSite.setBloodTypes(bloodTypeSpinner.getSelectedItem().toString());

        // Update the data in Firestore
        firestoreHelper.updateDonationSite(donationSite, new FirestoreHelper.OnDataOperationListener() {
            @Override
            public void onSuccess() {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedDonationSite", donationSite);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(UpdateDonationSiteDetailActivity.this, "Donation Site updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UpdateDonationSiteDetailActivity.this, "Failed to update Donation Site: " + error, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UpdateDonationSiteDetailActivity.this, "Start date must be today or later", Toast.LENGTH_SHORT).show();
        }

        // Validate if the end date is not before the start date
        String endDate = dateEnd.getText().toString();
        if (!isEndDateValid(startDate, endDate)) {
            isValid = false;
            Toast.makeText(UpdateDonationSiteDetailActivity.this, "End date cannot be before the start date", Toast.LENGTH_SHORT).show();
        }

        // Validate if opening and closing hours are within the allowed range
        String openingHour = openingHours.getText().toString();
        String closingHour = closingHours.getText().toString();
        if (!areOpeningClosingHoursValid(openingHour, closingHour)) {
            isValid = false;
            Toast.makeText(UpdateDonationSiteDetailActivity.this, "Opening and closing hours must be between 7:00 AM and 5:00 PM, and closing cannot be before opening", Toast.LENGTH_SHORT).show();
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
                !description.getText().toString().isEmpty();

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
            Log.i("UpdateDonationSite", "Error: " + status.getStatusMessage());
        }
    }
}