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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpView extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText emailInput, passwordInput, reenterPasswordInput, nameInput, dobInput, phoneNumberInput;
    private RadioGroup roleGroup;
    private Spinner bloodTypeSpinner;
    private LinearLayout bloodTypeLayout;
    private Button signUpButton;
    private TextView loginLink;

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
        signUpButton = findViewById(R.id.buttonSignup);
        loginLink = findViewById(R.id.signUpLink);

        // Set up listeners
        signUpButton.setOnClickListener(v -> signUpUser());
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUpView.this, LoginView.class)));

        // Initially hide bloodType spinner if not a donor
        if (roleGroup.getCheckedRadioButtonId() == R.id.radioDonor) {
            bloodTypeLayout.setVisibility(View.VISIBLE);
        } else {
            bloodTypeLayout.setVisibility(View.GONE);
        }

        // Show or hide the bloodType spinner based on role selection
        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDonor) {
                bloodTypeLayout.setVisibility(View.VISIBLE);
            } else {
                bloodTypeLayout.setVisibility(View.GONE);
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


    private void signUpUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String reenterPassword = reenterPasswordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty() || name.isEmpty() || dob.isEmpty() || phoneNumber.isEmpty()) {
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
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        storeUserDataInFirestore(uid, email, selectedRole, name, dob, phoneNumber);
                    } else {
                        Toast.makeText(SignUpView.this, "Signup failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserDataInFirestore(String uid, String email, String role, String name, String dob, String phoneNumber) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", role);
        userData.put("name", name);
        userData.put("dateOfBirth", dob);
        userData.put("phoneNumber", phoneNumber);
        userData.put("createdAt", Timestamp.now());

        // If Donor, add blood type info
        if ("Donor".equals(role)) {
            // Check if spinner is initialized and has a selected item
            if (bloodTypeSpinner != null && bloodTypeSpinner.getVisibility() == View.VISIBLE) {
                String bloodType = bloodTypeSpinner.getSelectedItem() != null ? bloodTypeSpinner.getSelectedItem().toString() : "";

                // Debug log to check the value of bloodType
                Log.d("SignUpView", "Selected Blood Type: " + bloodType);

                if (!bloodType.isEmpty()) {
                    userData.put("bloodType", bloodType);
                } else {
                    Toast.makeText(SignUpView.this, "Please select a blood type", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(SignUpView.this, "Blood Type Spinner is not initialized properly", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        firestore.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpView.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpView.this, LoginView.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpView.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
}

