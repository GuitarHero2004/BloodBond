package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.LoginView;
import com.example.bloodbond.model.*;
import com.example.bloodbond.R;

import java.util.Objects;

public class SiteManagerUserFragment extends Fragment {

    private final AuthHelper authHelper = new AuthHelper();
    private TextView siteManagerNameTextView, siteManagerEmailTextView, siteManagerPhoneNumberTextView, siteManagerRoleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site_manager_user, container, false);

        // Initialize views
        siteManagerNameTextView = view.findViewById(R.id.userName);
        siteManagerEmailTextView = view.findViewById(R.id.userEmail);
        siteManagerPhoneNumberTextView = view.findViewById(R.id.userPhoneNumber);
        siteManagerRoleTextView = view.findViewById(R.id.userRole);
        LinearLayout logoutButton = view.findViewById(R.id.customLogoutButton);

        // Fetch site manager data
        fetchUserData();

        // Set up logout button
        logoutButton.setOnClickListener(v -> {
            authHelper.logout();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void fetchUserData() {
        String userId = Objects.requireNonNull(authHelper.getCurrentUser()).getUid();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        firestoreHelper.fetchSiteManagerData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Object data) {
                if (data instanceof SiteManager) {
                    SiteManager siteManager = (SiteManager) data;

                    // Map SiteManager data to UI components
                    siteManagerNameTextView.setText(siteManager.getName() != null ? "Name: " + siteManager.getName() : "N/A");
                    siteManagerEmailTextView.setText(siteManager.getEmail() != null ? "Email: " + siteManager.getEmail() : "N/A");
                    siteManagerPhoneNumberTextView.setText(siteManager.getPhoneNumber() != null ? "Phone Number: " + siteManager.getPhoneNumber() : "N/A");
                    siteManagerRoleTextView.setText("Role: " + siteManager.getRole());
                } else {
                    Toast.makeText(getContext(), "Unexpected user type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch user data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
