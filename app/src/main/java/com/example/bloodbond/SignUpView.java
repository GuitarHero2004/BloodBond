package com.example.bloodbond;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SignUpView extends AppCompatActivity {
    private AuthHelper authHelper;
    private FirestoreHelper firestoreHelper;
    private EditText emailInput, passwordInput, reenterPasswordInput, nameInput, dobInput, phoneNumberInput;
    private RadioGroup roleGroup;
    private Spinner bloodTypeSpinner;
    private LinearLayout bloodTypeLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authHelper = new AuthHelper();
        firestoreHelper = new FirestoreHelper();

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        reenterPasswordInput = findViewById(R.id.editTextReenterPassword);
        nameInput = findViewById(R.id.editTextName);
        dobInput = findViewById(R.id.editTextDOB);
        phoneNumberInput = findViewById(R.id.editTextPhoneNumber);
        roleGroup = findViewById(R.id.roleGroup);
        bloodTypeLayout = findViewById(R.id.bloodTypeLayout);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        progressBar = findViewById(R.id.progressBar);

        Button signUpButton = findViewById(R.id.buttonSignup);
        TextView loginLink = findViewById(R.id.signUpLink);

        phoneNumberInput.setVisibility(View.GONE);

        signUpButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            signUpUser();
        });
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUpView.this, LoginView.class)));

        if (roleGroup.getCheckedRadioButtonId() == R.id.radioDonor) {
            bloodTypeLayout.setVisibility(View.VISIBLE);
        } else {
            bloodTypeLayout.setVisibility(View.GONE);
        }

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDonor) {
                bloodTypeLayout.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioSiteManager) {
                bloodTypeLayout.setVisibility(View.GONE);
                phoneNumberInput.setVisibility(View.VISIBLE);
            }
        });

        dobInput.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar eighteenYearsAgo = Calendar.getInstance();
        eighteenYearsAgo.add(Calendar.YEAR, -18);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUpView.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobInput.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(eighteenYearsAgo.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isAgeValid(String dob) {
        try {
            String[] dobParts = dob.split("/");
            int day = Integer.parseInt(dobParts[0]);
            int month = Integer.parseInt(dobParts[1]) - 1;
            int year = Integer.parseInt(dobParts[2]);

            Calendar currentDate = Calendar.getInstance();
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

            int age = currentYear - year;
            if (currentMonth < month || (currentMonth == month && currentDay < day)) {
                age--;
            }

            return age >= 18;
        } catch (Exception e) {
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

        if (email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty() || name.isEmpty() || dob.isEmpty()) {
            Toast.makeText(SignUpView.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(reenterPassword)) {
            Toast.makeText(SignUpView.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(SignUpView.this, "Password must have at least 8 characters, including an uppercase letter, a digit, and a special character.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!isAgeValid(dob)) {
            Toast.makeText(SignUpView.this, "You must be at least 18 years old to donate blood.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(SignUpView.this, "Please select a role", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String selectedRole = ((RadioButton) findViewById(selectedRoleId)).getText().toString();
        if ("Donor".equals(selectedRole) && bloodTypeLayout.getVisibility() == View.VISIBLE) {
            if (bloodTypeSpinner != null) {
                String selectedBloodType = bloodTypeSpinner.getSelectedItem() != null ? bloodTypeSpinner.getSelectedItem().toString() : "";
                if (selectedBloodType.isEmpty()) {
                    Toast.makeText(SignUpView.this, "Please select a blood type", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
            }
        } else if ("Blood Donation Site Manager".equals(selectedRole)) {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(SignUpView.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        }

        authHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(authHelper.getCurrentUser()).getUid();
                        storeUserDataInFirestore(uid, email, selectedRole, name, dob);
                    } else {
                        Toast.makeText(SignUpView.this, "Signup failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void storeUserDataInFirestore(String uid, String email, String role, String name, String dob) {
        if ("Donor".equals(role)) {
            String bloodType = bloodTypeSpinner.getSelectedItem().toString();
            Donor donor = new Donor(name, dob, email, role, bloodType, "", 0);

            firestoreHelper.storeDonorData(uid, donor, new FirestoreHelper.OnDataOperationListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(SignUpView.this, "Sign Up Successful as Donor!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(SignUpView.this, LoginView.class));
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(SignUpView.this, "Error saving Donor data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        } else if ("Blood Donation Site Manager".equals(role)) {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            List<String> sitesManaged = new ArrayList<>();

            SiteManager siteManager = new SiteManager(name, dob, email, role, phoneNumber, sitesManaged);

            firestoreHelper.storeSiteManagerData(uid, siteManager, new FirestoreHelper.OnDataOperationListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(SignUpView.this, "Sign Up Successful as Site Manager!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(SignUpView.this, LoginView.class));
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle Firestore error
                    Toast.makeText(SignUpView.this, "Error saving Donor data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    // Consider deleting the user account from Firebase Authentication
                    authHelper.getCurrentUser().delete()
                            .addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    Toast.makeText(SignUpView.this, "User account deleted due to data storage error.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpView.this, "Failed to delete user account after data storage error.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else {
            Toast.makeText(SignUpView.this, "Invalid role selected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }
}