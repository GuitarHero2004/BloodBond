package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import com.example.bloodbond.R;
import com.example.bloodbond.adapter.DonationSiteAdapter;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;

import java.util.ArrayList;
import java.util.List;

public class DonorMainFragment extends Fragment {
    private DonationSiteAdapter adapter;
    private final List<DonationSite> donationSites = new ArrayList<>();
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donor_main, container, false);

        // Initialize the search bar
        SearchView searchView = view.findViewById(R.id.donationSitesSearchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.requestFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Retrieve the user role from the arguments
        String userRole = getArguments() != null ? getArguments().getString("userRole") : "donors";

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
                donationSites.clear();
                donationSites.addAll(data);

                // Update the full list in the adapter
                adapter.updateFullList(data);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors here (e.g., show a Toast or Log the error)
            }
        });
    }
}