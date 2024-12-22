package com.example.bloodbond;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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

import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.*;

import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Calendar;

public class AddDonationSiteActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private EditText siteName, siteAddress, phoneNumber, dateOpen, dateEnd, openingHours, closingHours, description;
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
        return  !siteName.getText().toString().isEmpty() &&
                !siteAddress.getText().toString().isEmpty() &&
                !bloodTypeSpinner.getSelectedItem().toString().isEmpty() &&
                !phoneNumber.getText().toString().isEmpty() &&
                !dateOpen.getText().toString().isEmpty() &&
                !dateEnd.getText().toString().isEmpty() &&
                !openingHours.getText().toString().isEmpty() &&
                !closingHours.getText().toString().isEmpty() &&
                !description.getText().toString().isEmpty();
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

        double latitude = 0.0;
        double longitude = 0.0;

        // Retrieve latitude and longitude from the siteAddress field's tag if available
        if (siteAddress.getTag() instanceof double[]) {
            double[] latLng = (double[]) siteAddress.getTag();
            latitude = latLng[0];
            longitude = latLng[1];
        }

        return new DonationSite(name, address, phone, dateOpened, dateClosed, siteOpeningHours, siteClosingHours, desc, bloodTypes, latitude, longitude);
    }

    private void storeDonationSite(DonationSite donationSite) {
        firestoreHelper.storeDonationSiteData(donationSite, new FirestoreHelper.OnDataOperationListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddDonationSiteActivity.this, "Donation Site added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddDonationSiteActivity.this, "Failed to add Donation Site: " + error, Toast.LENGTH_SHORT).show();
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
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void openPlaceAutocomplete() {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                handlePlaceSelection(data);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                handleError(data);
            }
        }
    }

    private void handlePlaceSelection(Intent data) {
        Place place = Autocomplete.getPlaceFromIntent(data);
        String address = place.getAddress();
        LatLng latLng = place.getLatLng();

        if (latLng != null) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            // Update the address field and store latitude/longitude in temporary variables
            siteAddress.setText(address);

            // Optionally, store the latitude/longitude values in temporary variables for later use
            siteAddress.setTag(new double[]{latitude, longitude});
        } else {
            Log.d("AddDonationSiteActivity", "Latitude and Longitude are null.");
            Toast.makeText(this, "No latitude/longitude available", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleError(Intent data) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e("AddDonationSiteActivity", "Error: " + status.getStatusMessage());
        Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }
}
