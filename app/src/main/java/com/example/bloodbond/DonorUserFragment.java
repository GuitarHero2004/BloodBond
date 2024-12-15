package com.example.bloodbond;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bloodbond.Helper.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.Objects;

public class DonorUserFragment extends Fragment {

    private FirebaseAuth auth;
    private TextView donorNameTextView, donorEmailTextView, donorBloodTypeTextView, donorPhoneTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donor_user, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        donorNameTextView = view.findViewById(R.id.userName);
        donorEmailTextView = view.findViewById(R.id.userEmail);
        donorBloodTypeTextView = view.findViewById(R.id.userBloodType);
        donorPhoneTextView = view.findViewById(R.id.userPhoneNumber);
        LinearLayout logoutButton = view.findViewById(R.id.customLogoutButton);

        // Fetch donor data
        fetchUserData();

        // Set up logout button
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void fetchUserData() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        firestoreHelper.fetchUserData(userId, new FirestoreHelper.OnDataFetchListener() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Map the data to UI components
                String name = (String) data.get("name");
                String email = (String) data.get("email");
                String bloodType = (String) data.get("bloodType");
                String phoneNumber = (String) data.get("phoneNumber");

                donorNameTextView.setText(name != null ? "Name: " + name : "N/A");
                donorEmailTextView.setText(email != null ? "Email: " + email : "N/A");
                donorBloodTypeTextView.setText(bloodType != null ? "Blood Type: " + bloodType : "N/A");
                donorPhoneTextView.setText(phoneNumber != null ? "Phone Number: " + phoneNumber : "N/A");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch user data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
