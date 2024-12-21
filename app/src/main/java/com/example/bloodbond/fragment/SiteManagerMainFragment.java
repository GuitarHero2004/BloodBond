package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.AddDonationSiteActivity;
import com.example.bloodbond.adapter.DonationSiteAdapter;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.example.bloodbond.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SiteManagerMainFragment extends Fragment {

    private RecyclerView recyclerView;
    private DonationSiteAdapter adapter;
    private List<DonationSite> donationSites = new ArrayList<>();
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private Button addDonationSiteButton;
    private String userRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_manager_main, container, false);

        // Retrieve the user role from the arguments
        userRole = getArguments() != null ? getArguments().getString("userRole") : "siteManagers";

        // Initialize Add Donation Site button
        addDonationSiteButton = view.findViewById(R.id.addDonationSiteButton);

        addDonationSiteButton.setOnClickListener(v -> startActivity(new Intent(getContext(), AddDonationSiteActivity.class)));

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.donationSitesRecyclerView);
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
            }
        });
    }
}