package com.example.bloodbond.Helper;

import com.example.bloodbond.Model.DonationSite;
import com.example.bloodbond.Model.UserModel;
import com.example.bloodbond.Model.Donor;
import com.example.bloodbond.Model.SiteManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {

    private final FirebaseFirestore firestore;

    public FirestoreHelper() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void storeUserData(String uid, Object user, OnDataOperationListener listener) {
        firestore.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void fetchUserData(String userId, OnUserDataFetchListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("Donor".equals(role)) {
                            Donor donor = documentSnapshot.toObject(Donor.class);
                            if (donor != null) {
                                listener.onSuccess(donor); // Pass Donor object directly
                            } else {
                                listener.onFailure("Failed to fetch donor data");
                            }
                        } else if ("Blood Donation Site Manager".equals(role)) {
                            SiteManager siteManager = documentSnapshot.toObject(SiteManager.class);
                            if (siteManager != null) {
                                listener.onSuccess(siteManager); // Pass SiteManager object directly
                            } else {
                                listener.onFailure("Failed to fetch site manager data");
                            }
                        } else {
                            listener.onFailure("Unknown role: " + role);
                        }
                    } else {
                        listener.onFailure("User does not exist");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void storeDonationSiteData(DonationSite donationSite, OnDataOperationListener listener) {
        firestore.collection("donationSites")
                .add(donationSite)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void fetchDonationSites(OnDonationSitesFetchListener listener) {
        firestore.collection("donationSites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DonationSite> sites = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DonationSite site = document.toObject(DonationSite.class);
                            sites.add(site);
                        }
                        listener.onSuccess(sites);
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface OnUserDataFetchListener {
        void onSuccess(Object data);
        void onFailure(String errorMessage);
    }

    public interface OnDonationSitesFetchListener {
        void onSuccess(List<DonationSite> data);
        void onFailure(String errorMessage);
    }

    public interface OnDataOperationListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}