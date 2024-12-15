package com.example.bloodbond.Helper;

import com.example.bloodbond.Model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore firestore;

    public FirestoreHelper() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void storeUserData(String userId, User userData, OnDataOperationListener listener) {
        firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void fetchUserData(String userId, OnDataFetchListener listener) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onSuccess(documentSnapshot.getData());
                    } else {
                        listener.onFailure("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnDataFetchListener {
        void onSuccess(Map<String, Object> data);
        void onFailure(String errorMessage);
    }

    public interface OnDataOperationListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
