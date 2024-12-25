package com.example.bloodbond.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.R;
import com.example.bloodbond.DonationSiteDetailActivity;
import com.example.bloodbond.model.DonationSite;

import java.util.ArrayList;
import java.util.List;

public class DonationSiteAdapter extends RecyclerView.Adapter<DonationSiteAdapter.ViewHolder> implements Filterable {

    private final List<DonationSite> donationSites;
    private final List<DonationSite> donationSitesFull;
    private final Context context;
    private final String userRole;

    public DonationSiteAdapter(List<DonationSite> donationSites, Context context, String userRole) {
        this.donationSites = donationSites;
        this.donationSitesFull = new ArrayList<>(donationSites);
        this.context = context;
        this.userRole = userRole;
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

        // Set a click listener for the view details button
        holder.viewDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DonationSiteDetailActivity.class);
            intent.putExtra("donationSite", site);
            intent.putExtra("userRole", userRole);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return donationSites.size();
    }

    @Override
    public Filter getFilter() {
        return donationSiteFilter;
    }

    private final Filter donationSiteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DonationSite> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(donationSitesFull); // Show full list if no query
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DonationSite site : donationSitesFull) {
                    // Match donationSiteName or bloodTypesNeeded
                    if (site.getSiteName().toLowerCase().contains(filterPattern) ||
                            site.getBloodTypes().toLowerCase().contains(filterPattern)) {
                        filteredList.add(site);
                    }
                }
            }

            // Debugging logs
            Log.d("DonationSiteAdapter", "Filter pattern: " + constraint);
            Log.d("DonationSiteAdapter", "Filtered list size: " + filteredList.size());

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            donationSites.clear();
            donationSites.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateFullList(List<DonationSite> newList) {
        donationSitesFull.clear();
        donationSitesFull.addAll(newList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView donationSiteName;
        public TextView donationSiteAddress;
        public TextView donationSiteDate;
        public TextView bloodTypesNeeded;
        public Button viewDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Match the IDs from the provided XML
            donationSiteName = itemView.findViewById(R.id.donationSiteName);
            donationSiteAddress = itemView.findViewById(R.id.donationSiteAddress);
            donationSiteDate = itemView.findViewById(R.id.donationSiteDate);
            bloodTypesNeeded = itemView.findViewById(R.id.bloodTypesNeeded);
            viewDetailsButton = itemView.findViewById(R.id.donationSiteDetailButton);
        }
    }
}