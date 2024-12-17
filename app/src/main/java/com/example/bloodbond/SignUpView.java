package com.example.bloodbond;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.Model.Donor;
import com.example.bloodbond.Model.SiteManager;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SignUpView extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText emailInput, passwordInput, reenterPasswordInput, nameInput, dobInput, phoneNumberInput;
    private RadioGroup roleGroup;
    private Spinner bloodTypeSpinner;
    private LinearLayout bloodTypeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        reenterPasswordInput = findViewById(R.id.editTextReenterPassword);
        nameInput = findViewById(R.id.editTextName);
        dobInput = findViewById(R.id.editTextDOB);
        phoneNumberInput = findViewById(R.id.editTextPhoneNumber);
        roleGroup = findViewById(R.id.roleGroup);
        bloodTypeLayout = findViewById(R.id.bloodTypeLayout);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);

        Button signUpButton = findViewById(R.id.buttonSignup);
        TextView loginLink = findViewById(R.id.signUpLink);

        phoneNumberInput.setVisibility(View.GONE);  // Initially hide phone number input

        // Set up listeners
        signUpButton.setOnClickListener(v -> signUpUser());
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUpView.this, LoginView.class)));

        // Initially hide bloodType spinner if not a donor
        if (roleGroup.getCheckedRadioButtonId() == R.id.radioDonor) {
            bloodTypeLayout.setVisibility(View.VISIBLE);
        } else {
            bloodTypeLayout.setVisibility(View.GONE);
        }

        // Show or hide the bloodType spinner and phoneNumberInput based on role selection
        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDonor) {
                bloodTypeLayout.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.GONE);  // Hide phone number input when donor is selected
            } else if (checkedId == R.id.radioSiteManager) {
                bloodTypeLayout.setVisibility(View.GONE);  // Hide blood type spinner for site managers
                phoneNumberInput.setVisibility(View.VISIBLE);  // Show phone number input for site manager
            }
        });

        // Set up the listener for the DOB field
        dobInput.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Calculate the date that is 18 years before the current date
        Calendar eighteenYearsAgo = Calendar.getInstance();
        eighteenYearsAgo.add(Calendar.YEAR, -18);  // Subtract 18 years

        // Create a DatePickerDialog with a max date of 18 years ago
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpView.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the EditText with the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobInput.setText(selectedDate);
                },
                year, month, day);

        // Set the maximum date to 18 years ago
        datePickerDialog.getDatePicker().setMaxDate(eighteenYearsAgo.getTimeInMillis());

        // Show the date picker
        datePickerDialog.show();
    }

    private boolean isValidPassword(String password) {
        // Regex: At least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isAgeValid(String dob) {
        // Format: dd/MM/yyyy
        try {
            String[] dobParts = dob.split("/");
            int day = Integer.parseInt(dobParts[0]);
            int month = Integer.parseInt(dobParts[1]) - 1; // Month is 0-based in Calendar
            int year = Integer.parseInt(dobParts[2]);

            // Get current date
            Calendar currentDate = Calendar.getInstance();
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

            // Calculate age
            int age = currentYear - year;
            if (currentMonth < month || (currentMonth == month && currentDay < day)) {
                age--; // If birthday hasn't happened yet this year, subtract 1 from age
            }

            // Return true if age is 18 or older
            return age >= 18;
        } catch (Exception e) {
            // In case of invalid date format
            Toast.makeText(SignUpView.this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void signUpUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String reenterPassword = reenterPasswordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty() || name.isEmpty() || dob.isEmpty()) {
            Toast.makeText(SignUpView.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(reenterPassword)) {
            Toast.makeText(SignUpView.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(SignUpView.this, "Password must have at least 8 characters, including an uppercase letter, a digit, and a special character.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check age validation
        if (!isAgeValid(dob)) {
            Toast.makeText(SignUpView.this, "You must be at least 18 years old to donate blood.", Toast.LENGTH_LONG).show();
            return;
        }

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(SignUpView.this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedRole = ((RadioButton) findViewById(selectedRoleId)).getText().toString();
        // Check if the bloodTypeLayout is visible and the spinner is not null
        if ("Donor".equals(selectedRole) && bloodTypeLayout.getVisibility() == View.VISIBLE) {
            // Check if the spinner is initialized
            if (bloodTypeSpinner != null) {
                String selectedBloodType = bloodTypeSpinner.getSelectedItem() != null ? bloodTypeSpinner.getSelectedItem().toString() : "";
                if (selectedBloodType.isEmpty()) {
                    Toast.makeText(SignUpView.this, "Please select a blood type", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if ("Blood Donation Site Manager".equals(selectedRole)) {
            // Enter their phone number if they are a site manager
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(SignUpView.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        storeUserDataInFirestore(uid, email, selectedRole, name, dob);
                    } else {
                        Toast.makeText(SignUpView.this, "Signup failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserDataInFirestore(String uid, String email, String role, String name, String dob) {
        if ("Donor".equals(role)) {
            // For Donor role, create a Donor object
            String bloodType = bloodTypeSpinner.getSelectedItem().toString();
            Donor donor = new Donor(name, dob, email, role, bloodType, "", 0);

            // Store Donor data
            firestore.collection("users").document(uid)
                    .set(donor)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpView.this, "Sign Up Successful as Donor!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpView.this, LoginView.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SignUpView.this, "Error saving Donor data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else if ("Blood Donation Site Manager".equals(role)) {
            // For Site Manager role, create a SiteManager object
            String phoneNumber = phoneNumberInput.getText().toString().trim();

            // Create an empty list for sites managed
            List<String> sitesManaged = new ArrayList<>();

            SiteManager siteManager = new SiteManager(name, dob, email, role, phoneNumber, sitesManaged);

            // Store SiteManager data
            firestore.collection("users").document(uid)
                    .set(siteManager)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpView.this, "Sign Up Successful as Site Manager!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpView.this, LoginView.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SignUpView.this, "Error saving SiteManager data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Handle case where the role is invalid
            Toast.makeText(SignUpView.this, "Invalid role selected", Toast.LENGTH_SHORT).show();
        }
    }
}

