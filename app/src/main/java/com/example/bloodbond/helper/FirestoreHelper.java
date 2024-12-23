package com.example.bloodbond.helper;

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
                        List<DonationSite> sites = new ArrayList<DonationSite>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DonationSite site = document.toObject(DonationSite.class);
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

    public void updateSiteManagerSitesManaged(String siteManagerId, List<DonationSite> sitesManaged, OnDataOperationListener listener) {
        firestore.collection("siteManagers")
                .document(siteManagerId)
                .update("sitesManaged", sitesManaged)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void listenForDonationSitesUpdates(OnDonationSitesFetchListener listener) {
        firestore.collection("donationSites")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onFailure(error.getMessage());
                        return;
                    }
                    List<DonationSite> sites = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        DonationSite site = document.toObject(DonationSite.class);
                        sites.add(site);
                    }
                    listener.onSuccess(sites);
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