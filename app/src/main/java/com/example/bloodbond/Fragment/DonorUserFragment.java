package com.example.bloodbond.Fragment;

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

import com.example.bloodbond.Helper.AuthHelper;
import com.example.bloodbond.Helper.FirestoreHelper;
import com.example.bloodbond.LoginView;
import com.example.bloodbond.Model.Donor;
import com.example.bloodbond.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.Objects;

public class DonorUserFragment extends Fragment {

    private final AuthHelper authHelper = new AuthHelper();
    private TextView donorNameTextView, donorEmailTextView, donorBloodTypeTextView, donorRoleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donor_user, container, false);

        // Initialize views
        donorNameTextView = view.findViewById(R.id.userName);
        donorEmailTextView = view.findViewById(R.id.userEmail);
        donorBloodTypeTextView = view.findViewById(R.id.userBloodType);
        donorRoleTextView = view.findViewById(R.id.userRole);
        LinearLayout logoutButton = view.findViewById(R.id.customLogoutButton);

        // Fetch donor data
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

        firestoreHelper.fetchDonorData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Object data) {
                if (data instanceof Donor) {
                    Donor donor = (Donor) data;
                    donorNameTextView.setText("Name: " + donor.getName());
                    donorEmailTextView.setText("Email: " + donor.getEmail());
                    donorBloodTypeTextView.setText("Blood Type: " + donor.getBloodType());
                    donorRoleTextView.setText("Role: " + donor.getRole());
                } else {
                    Toast.makeText(getContext(), "Unexpected user type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to fetch user data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}