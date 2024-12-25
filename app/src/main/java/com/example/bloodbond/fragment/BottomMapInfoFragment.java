package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bloodbond.R;
import com.example.bloodbond.model.DonationSite;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomMapInfoFragment extends BottomSheetDialogFragment {
    private final DonationSite donationSite;
    private DialogInterface.OnDismissListener onDismissListener;

    public BottomMapInfoFragment(DonationSite donationSite) {
        this.donationSite = donationSite;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_map_info, container, false);

        TextView siteName = view.findViewById(R.id.siteName);
        TextView siteAddress = view.findViewById(R.id.siteAddress);
        TextView siteHours = view.findViewById(R.id.siteHours);
        TextView siteBloodTypes = view.findViewById(R.id.bloodTypesNeeded);
        Button directionsButton = view.findViewById(R.id.directionsButton);

        siteName.setText(donationSite.getSiteName());
        siteAddress.setText("Address: " + donationSite.getAddress());
        siteHours.setText("Hours: " + donationSite.getOpeningHours() + " - " + donationSite.getClosingHours());
        siteBloodTypes.setText("Blood Types Needed: " + donationSite.getBloodTypes());

        directionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + donationSite.getAddress()));
            startActivity(intent);
            dismiss();
        });

        return view;
    }
}