package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.AddDonationSiteActivity;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_manager_main, container, false);

        // Retrieve the user role from the arguments
        String userRole = getArguments() != null ? getArguments().getString("userRole") : "siteManagers";

        // Initialize Add Donation Site button
        Button addDonationSiteButton = view.findViewById(R.id.addDonationSiteButton);

        addDonationSiteButton.setOnClickListener(v -> {
            fetchPhoneNumber();
        });

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.donationSitesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonationSiteAdapter(donationSites, getContext(), userRole);
        recyclerView.setAdapter(adapter);

        fetchDonationSites();

        return view;
    }

    private void fetchDonationSites() {
        firestoreHelper.fetchDonationSites(new FirestoreHelper.OnDonationSitesFetchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<DonationSite> data) {
                // Update the list and notify the adapter
                donationSites.clear();
                donationSites.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors here (e.g., show a Toast or Log the error)
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
                // Handle the phone number here
                String phoneNumber = (String) data;
                Intent intent = new Intent(getContext(), AddDonationSiteActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors here (e.g., show a Toast or Log the error)
                Log.e("fetchPhoneNumber", "Error fetching phone number: " + errorMessage);
                Toast.makeText(getContext(), "Failed to fetch phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

}