package com.example.bloodbond.helper;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bloodbond.DonorView;
import com.example.bloodbond.SiteManagerView;
import com.example.bloodbond.SuperUserView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AuthHelper {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public AuthHelper() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void loginUser(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Login failed: User is null.");
                        }
                    } else {
                        callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void redirectToRoleBasedActivity(String userId, Context context) {
        firestore.collection("donors")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        context.startActivity(new Intent(context, DonorView.class));
                    } else {
                        firestore.collection("siteManagers")
                                .document(userId)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful() && task1.getResult().exists()) {
                                        context.startActivity(new Intent(context, SiteManagerView.class));
                                    } else {
                                        context.startActivity(new Intent(context, SuperUserView.class));
                                    }
                                });
                    }
                }).addOnFailureListener(e -> {
                    // Handle error (e.g., Firebase exceptions)
                    Toast.makeText(context, "Error fetching user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }
}