package com.example.bloodbond;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.*;

import java.util.Calendar;

public class AddDonationSiteActivity extends AppCompatActivity {

    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private EditText siteName, siteAddress, phoneNumber, dateOpen, dateEnd, openingHours, closingHours, description;
    private Spinner bloodTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_donation_site);

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

        // Initialize button
        LinearLayout customAddDonationSiteButton = findViewById(R.id.customAddDonationSiteButton);

        // Set onClickListener for the button
        customAddDonationSiteButton.setOnClickListener(v -> {
            String name = siteName.getText().toString();
            String address = siteAddress.getText().toString();
            String phone = phoneNumber.getText().toString();
            String dateOpened = dateOpen.getText().toString();
            String dateClosed = dateEnd.getText().toString();
            String siteOpeningHours = openingHours.getText().toString();
            String siteClosingHours = closingHours.getText().toString();
            String desc = description.getText().toString();
            String bloodTypes = bloodTypeSpinner.getSelectedItem().toString();

            // Create a new DonationSite object
            DonationSite donationSite = new DonationSite(name, address, phone, dateOpened, dateClosed, siteOpeningHours, siteClosingHours, desc, bloodTypes);
            Log.d("AddDonationSiteActivity", "Donation Site: " + donationSite);

            // Validate the input fields
            if (name.isEmpty() || address.isEmpty() || bloodTypes.isEmpty() || phone.isEmpty() || dateOpened.isEmpty() || dateClosed.isEmpty() || siteOpeningHours.isEmpty() || siteClosingHours.isEmpty() || desc.isEmpty()) {
                Toast.makeText(AddDonationSiteActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
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
        });

        // Set onClickListeners for date and time fields
        dateOpen.setOnClickListener(v -> showDatePickerDialog(dateOpen));
        dateEnd.setOnClickListener(v -> showDatePickerDialog(dateEnd));
        openingHours.setOnClickListener(v -> showTimePickerDialog(openingHours));
        closingHours.setOnClickListener(v -> showTimePickerDialog(closingHours));
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
            // Format hour and minute to always be two digits
            @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }

}