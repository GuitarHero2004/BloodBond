package com.example.bloodbond;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginView extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth & Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        TextView signUpLink = findViewById(R.id.signUpLink);

        // Check if user is already logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // If the user is already logged in, sign them out before showing the login screen
            auth.signOut();  // This will sign the user out

            // Optionally, show a message or handle actions after signing out
            Toast.makeText(LoginView.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
        }

        // Set up listeners
        loginButton.setOnClickListener(v -> loginUser());
        signUpLink.setOnClickListener(v -> startActivity(new Intent(LoginView.this, SignUpView.class)));
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginView.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        redirectToRoleBasedActivity(user.getUid());
                    } else {
                        Toast.makeText(LoginView.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToRoleBasedActivity(String userId) {
        firestore.collection("users").document(userId)  // Use UID as the document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Intent intent;
                        switch (Objects.requireNonNull(role)) {
                            case "Donor":
                                intent = new Intent(LoginView.this, DonorView.class);
                                break;
                            case "Blood Donation Site Manager":
                                intent = new Intent(LoginView.this, SiteManagerView.class);
                                break;
                            case "Super User":
                                intent = new Intent(LoginView.this, SuperUserView.class);
                                break;
                            default:
                                intent = new Intent(LoginView.this, LoginView.class);
                                Toast.makeText(LoginView.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginView.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("LoginView", "Failed to fetch role", e));
    }
}
