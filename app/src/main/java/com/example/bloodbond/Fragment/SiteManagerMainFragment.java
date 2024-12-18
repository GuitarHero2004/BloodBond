package com.example.bloodbond.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.AddDonationSiteActivity;
import com.example.bloodbond.Adapter.DonationSiteAdapter;
import com.example.bloodbond.Helper.FirestoreHelper;
import com.example.bloodbond.Model.DonationSite;
import com.example.bloodbond.R;

import java.util.ArrayList;
import java.util.List;

public class SiteManagerMainFragment extends Fragment {

    private RecyclerView recyclerView;
    private DonationSiteAdapter adapter;
    private List<DonationSite> donationSites = new ArrayList<>();
    private FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_manager_main, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.donationSitesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonationSiteAdapter(donationSites, getContext());
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