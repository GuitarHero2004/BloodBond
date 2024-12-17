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

    private DonationSiteAdapter adapter;
    private final List<DonationSite> donationSites = new ArrayList<>();
    private final FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_manager_main, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.donationSitesVolunteerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DonationSiteAdapter(donationSites);
        recyclerView.setAdapter(adapter);

        LinearLayout addDonationSiteButton = view.findViewById(R.id.customAddDonationSiteButton);
        addDonationSiteButton.setOnClickListener(v -> {
            // Navigate to AddDonationSiteActivity
            Intent intent = new Intent(getContext(), AddDonationSiteActivity.class);
            startActivity(intent);
        });

        loadDonationSites();

        return view;
    }

    private void loadDonationSites() {
        firestoreHelper.fetchDonationSites(new FirestoreHelper.OnDonationSitesFetchListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<DonationSite> sites) {
                donationSites.clear();
                donationSites.addAll(sites);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error
            }
        });
    }
}