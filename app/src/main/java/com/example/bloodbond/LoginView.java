package com.example.bloodbond;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.Helper.AuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class LoginView extends AppCompatActivity implements AuthHelper.LoginCallback {
    private final AuthHelper authHelper = new AuthHelper();
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        TextView signUpLink = findViewById(R.id.signUpLink);

        // Check if user is already logged in
        FirebaseUser currentUser = authHelper.getCurrentUser();
        if (currentUser != null) {
            // If the user is already logged in, sign them out before showing the login screen
            authHelper.logout();

            // Optionally, show a message or handle actions after signing out
            Toast.makeText(LoginView.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
        }

        // Set up listeners
        loginButton.setOnClickListener(v -> {
            authHelper.loginUser(emailInput.getText().toString(), passwordInput.getText().toString(), this);
        });

        signUpLink.setOnClickListener(v -> startActivity(new Intent(LoginView.this, SignUpView.class)));
    }

    @Override
    public void onSuccess(FirebaseUser user) {
        // Use AuthHelper to handle redirection based on the role
        authHelper.redirectToRoleBasedActivity(user.getUid(), this);
    }

    @Override
    public void onFailure(String message) {
        // Handle login failure
        Toast.makeText(LoginView.this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
    }
}
