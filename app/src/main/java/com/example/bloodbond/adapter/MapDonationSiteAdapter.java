package com.example.bloodbond.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bloodbond.R;
import com.example.bloodbond.model.DonationSite;

import java.util.List;

public class MapDonationSiteAdapter extends RecyclerView.Adapter<MapDonationSiteAdapter.ViewHolder> {
    private final List<DonationSite> donationSites;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DonationSite donationSite);
    }

    public MapDonationSiteAdapter(List<DonationSite> donationSites, OnItemClickListener listener) {
        this.donationSites = donationSites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_donation_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationSite site = donationSites.get(position);

        // Set values for the views in the item layout
        holder.donationSiteName.setText(site.getSiteName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(site));
    }

    @Override
    public int getItemCount() {
        return donationSites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView donationSiteName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            donationSiteName = itemView.findViewById(R.id.donationSiteName);
        }
    }
}
