package com.example.bloodbond;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.helper.AuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class LoginView extends AppCompatActivity implements AuthHelper.LoginCallback {
    private final AuthHelper authHelper = new AuthHelper();
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpLink = findViewById(R.id.signUpLink);
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser currentUser = authHelper.getCurrentUser();
        if (currentUser != null) {
            authHelper.logout();
            Toast.makeText(LoginView.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
        }

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            authHelper.loginUser(emailInput.getText().toString(), passwordInput.getText().toString(), this);
        });

        signUpLink.setOnClickListener(v -> startActivity(new Intent(LoginView.this, SignUpView.class)));
    }

    @Override
    public void onSuccess(FirebaseUser user) {
        progressBar.setVisibility(View.GONE);
        authHelper.redirectToRoleBasedActivity(user.getUid(), this);
    }

    @Override
    public void onFailure(String message) {
        progressBar.setVisibility(View.GONE);
        if (message.contains("network")) {
            Toast.makeText(LoginView.this, "Network error, please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginView.this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
        }
    }

}