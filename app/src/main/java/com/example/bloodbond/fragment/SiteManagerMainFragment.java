package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.AddDonationSiteActivity;
import com.example.bloodbond.UpdateDonationSiteDetailActivity;
import com.example.bloodbond.adapter.DonationSiteAdapter;
import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.example.bloodbond.R;

import java.util.ArrayList;
import java.util.List;

public class SiteManagerMainFragment extends Fragment {

    private DonationSiteAdapter adapter;
    private final List<DonationSite> donationSites = new ArrayList<>();
    private final AuthHelper authHelper = new AuthHelper();
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_manager_main, container, false);

        // Retrieve the user role from the arguments
        String userRole = getArguments() != null ? getArguments().getString("userRole") : "siteManagers";

        // Initialize Add Donation Site button
        Button addDonationSiteButton = view.findViewById(R.id.addDonationSiteButton);

        addDonationSiteButton.setOnClickListener(v -> fetchPhoneNumber());

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.donationSitesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonationSiteAdapter(donationSites, getContext(), userRole);
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progressBar);

        fetchDonationSites();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listenForDonationSitesUpdates();
    }

    private void listenForDonationSitesUpdates() {
        progressBar.setVisibility(View.VISIBLE);  // Show loading indicator

        String siteManagerId = authHelper.getUserId();
        firestoreHelper.listenForManagedDonationSitesUpdates(siteManagerId, new FirestoreHelper.OnDonationSitesFetchListener() {
            @Override
            public void onSuccess(List<DonationSite> data) {
                donationSites.clear();
                donationSites.addAll(data);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);  // Hide loading indicator
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SiteManagerMainFragment", "Error fetching donation sites: " + errorMessage);
                Toast.makeText(getContext(), "Failed to fetch donation sites", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);  // Hide loading indicator
            }
        });
    }

    private void fetchDonationSites() {
        String siteManagerId = authHelper.getUserId();
        firestoreHelper.fetchManagedDonationSites(siteManagerId, new FirestoreHelper.OnDonationSitesFetchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<DonationSite> data) {
                donationSites.clear();
                donationSites.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("fetchDonationSites", "Error fetching donation sites: " + errorMessage);
                Toast.makeText(getContext(), "Failed to fetch donation sites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPhoneNumber() {
        String siteManagerId = authHelper.getUserId();

        firestoreHelper.fetchSiteManagerPhoneNumber(siteManagerId, new FirestoreHelper.OnUserDataFetchListener() {
            @Override
            public void onSuccess(Object data) {
                String phoneNumber = (String) data;
                Intent intent = new Intent(getContext(), AddDonationSiteActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("fetchPhoneNumber", "Error fetching phone number: " + errorMessage);
                Toast.makeText(getContext(), "Failed to fetch phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}