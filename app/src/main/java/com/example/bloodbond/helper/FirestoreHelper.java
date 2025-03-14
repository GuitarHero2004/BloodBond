package com.example.bloodbond.helper;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.bloodbond.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirestoreHelper {

    private final FirebaseFirestore firestore;

    public FirestoreHelper() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void storeDonorData(String uid, Donor donor, OnDataOperationListener listener) {
        firestore.collection("donors")
                .document(uid)
                .set(donor)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void storeSiteManagerData(String uid, SiteManager siteManager, OnDataOperationListener listener) {
        firestore.collection("siteManagers")
                .document(uid)
                .set(siteManager)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void fetchDonorData(String donorId, OnUserDataFetchListener listener) {
        firestore.collection("donors")
                .document(donorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(task.getResult().toObject(Donor.class));
                    } else {
                        listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void fetchSiteManagerData(String siteManagerId, OnUserDataFetchListener listener) {
        firestore.collection("siteManagers")
                .document(siteManagerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(task.getResult().toObject(SiteManager.class));
                    } else {
                        listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void fetchSuperUserData(String superUserId, OnUserDataFetchListener listener) {
        firestore.collection("superUsers")
                .document(superUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(task.getResult().toObject(SuperUser.class));
                    } else {
                        listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void storeDonationSiteData(DonationSite donationSite, OnDataOperationListener listener) {
        firestore.collection("donationSites")
                .add(donationSite)
                .addOnSuccessListener(documentReference -> {
                    donationSite.setSiteId(documentReference.getId());
                    firestore.collection("donationSites")
                            .document(documentReference.getId())
                            .set(donationSite)
                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                })
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
                            site.setSiteId(document.getId());
                            sites.add(site);
                        }
                        listener.onSuccess(sites);
                    } else {
                        listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void fetchSiteManagerPhoneNumber(String siteManagerId, OnUserDataFetchListener listener) {
        firestore.collection("siteManagers")
                .document(siteManagerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            SiteManager siteManager = task.getResult().toObject(SiteManager.class);
                            if (siteManager != null) {
                                listener.onSuccess(siteManager.getPhoneNumber());
                            } else {
                                listener.onFailure("SiteManager data is missing or malformed");
                            }
                        } else {
                            listener.onFailure("SiteManager document does not exist");
                        }
                    } else {
                        listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void updateSiteManagerSitesManaged(String siteManagerId, List<String> sitesManagedNames, OnDataOperationListener listener) {
        firestore.collection("siteManagers")
                .document(siteManagerId)
                .update("sitesManagedNames", sitesManagedNames)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void fetchManagedDonationSites(String siteManagerId, OnDonationSitesFetchListener listener) {
        Log.d("FirestoreHelper", "Fetching managed donation sites for siteManagerId: " + siteManagerId);
        firestore.collection("donationSites")
                .whereEqualTo("siteManagerId", siteManagerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DonationSite> sites = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DonationSite site = document.toObject(DonationSite.class);
                            site.setSiteId(document.getId());
                            sites.add(site);
                        }
                        Log.d("FirestoreHelper", "Fetched " + sites.size() + " donation sites");
                        listener.onSuccess(sites);
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Log.e("FirestoreHelper", "Error fetching donation sites: " + errorMessage);
                        listener.onFailure(errorMessage);
                    }
                });
    }

    public void listenForManagedDonationSitesUpdates(String siteManagerId, OnDonationSitesFetchListener listener) {
        Log.d("FirestoreHelper", "Listening for updates to managed donation sites for siteManagerId: " + siteManagerId);
        firestore.collection("donationSites")
                .whereEqualTo("siteManagerId", siteManagerId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreHelper", "Error listening for updates: " + error.getMessage());
                        listener.onFailure(error.getMessage());
                        return;
                    }
                    List<DonationSite> sites = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        DonationSite site = document.toObject(DonationSite.class);
                        site.setSiteId(document.getId());
                        sites.add(site);
                    }
                    Log.d("FirestoreHelper", "Received update with " + sites.size() + " donation sites");
                    listener.onSuccess(sites);
                });
    }

    public void updateDonationSite(DonationSite donationSite, OnDataOperationListener listener) {
        firestore.collection("donationSites")
                .document(donationSite.getSiteId())
                .set(donationSite)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void deleteDonationSiteData(String siteId, OnDataOperationListener listener) {
        firestore.collection("donationSites")
                .document(siteId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
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