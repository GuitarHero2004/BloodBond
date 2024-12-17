package com.example.bloodbond.Helper;

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

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void redirectToRoleBasedActivity(String userId, Context context) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Intent intent;
                        switch (Objects.requireNonNull(role)) {
                            case "Donor":
                                intent = new Intent(context, DonorView.class);
                                break;
                            case "Blood Donation Site Manager":
                                intent = new Intent(context, SiteManagerView.class);
                                break;
                            case "Super User":
                                intent = new Intent(context, SuperUserView.class);
                                break;
                            default:
                                intent = new Intent(context, context.getClass());
                                Toast.makeText(context, "Role not recognized", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to fetch role", Toast.LENGTH_SHORT).show());
    }

    public void fetchUserRole(String userId, RoleCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = (String) documentSnapshot.get("role");
                        if (role != null) {
                            callback.onRoleFetched(role);
                        } else {
                            callback.onFailure(new Exception("Role not found"));
                        }
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void logout() {
        auth.signOut();
    }

    // Fetch Phone Number for Current User
    public void fetchUserPhoneNumber(String userId, PhoneNumberCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phoneNumber = (String) documentSnapshot.get("phoneNumber");
                        if (phoneNumber != null) {
                            callback.onPhoneNumberFetched(phoneNumber);
                        } else {
                            callback.onFailure(new Exception("Phone number not found"));
                        }
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    public interface RoleCallback {
        void onRoleFetched(String role);
        void onFailure(Exception e);
    }

    public interface PhoneNumberCallback {
        void onPhoneNumberFetched(String phoneNumber);
        void onFailure(Exception e);
    }
}
