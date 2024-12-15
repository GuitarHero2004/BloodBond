package com.example.bloodbond.Helper;

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

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    public interface RoleCallback {
        void onRoleFetched(String role);
        void onFailure(Exception e);
    }
}
