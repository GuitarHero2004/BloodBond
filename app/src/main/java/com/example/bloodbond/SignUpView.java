package com.example.bloodbond;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpView extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText emailInput, passwordInput, reenterPasswordInput, nameInput, dobInput;
    private RadioGroup roleGroup;
    private Spinner bloodTypeSpinner;
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
        roleGroup = findViewById(R.id.roleGroup);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        signUpButton = findViewById(R.id.buttonSignup);
        loginLink = findViewById(R.id.signUpLink);

        // Set up listeners
        signUpButton.setOnClickListener(v -> signUpUser());
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUpView.this, LoginView.class)));

        // Initially hide bloodType spinner if not a donor
        if (roleGroup.getCheckedRadioButtonId() == R.id.radioDonor) {
            bloodTypeSpinner.setVisibility(View.VISIBLE);
        } else {
            bloodTypeSpinner.setVisibility(View.GONE);
        }

        // Show or hide the bloodType spinner based on role selection
        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDonor) {
                bloodTypeSpinner.setVisibility(View.VISIBLE);
            } else {
                bloodTypeSpinner.setVisibility(View.GONE);
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

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpView.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the EditText with the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobInput.setText(selectedDate);
                },
                year, month, day);

        // Optionally, set a date range
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Prevent future dates
        datePickerDialog.show();
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

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(SignUpView.this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedRole = ((RadioButton) findViewById(selectedRoleId)).getText().toString();

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
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", role);
        userData.put("name", name);
        userData.put("dateOfBirth", dob);

        // If Donor, add blood type info
        if ("Donor".equals(role)) {
            String bloodType = bloodTypeSpinner.getSelectedItem().toString();
            userData.put("bloodType", bloodType);
        }

        firestore.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpView.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpView.this, LoginView.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpView.this, "Error saving data", Toast.LENGTH_SHORT).show());
    }
}
