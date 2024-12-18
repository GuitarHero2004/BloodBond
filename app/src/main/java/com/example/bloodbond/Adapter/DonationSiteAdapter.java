package com.example.bloodbond.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.Model.DonationSite;
import com.example.bloodbond.R;
import com.example.bloodbond.DonationSiteDetailActivity;

import java.util.List;

public class DonationSiteAdapter extends RecyclerView.Adapter<DonationSiteAdapter.ViewHolder> {

    private final List<DonationSite> donationSites;
    private final Context context;

    public DonationSiteAdapter(List<DonationSite> donationSites, Context context) {
        this.donationSites = donationSites;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_site, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationSite site = donationSites.get(position);

        // Set values for the views in the item layout
        holder.donationSiteName.setText(site.getSiteName());
        holder.donationSiteAddress.setText(site.getAddress());
        holder.donationSiteDate.setText(site.getDateOpened() + " - " + site.getDateClosed());
        holder.bloodTypesNeeded.setText(site.getBloodTypes());

        // Handle item clicks
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DonationSiteDetailActivity.class);
            intent.putExtra("donationSite", site);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return donationSites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView donationSiteName;
        public TextView donationSiteAddress;
        public TextView donationSiteDate;
        public TextView bloodTypesNeeded;

        public ViewHolder(View itemView) {
            super(itemView);

            // Match the IDs from the provided XML
            donationSiteName = itemView.findViewById(R.id.donationSiteName);
            donationSiteAddress = itemView.findViewById(R.id.donationSiteAddress);
            donationSiteDate = itemView.findViewById(R.id.donationSiteDate);
            bloodTypesNeeded = itemView.findViewById(R.id.bloodTypesNeeded);
        }
    }
}
