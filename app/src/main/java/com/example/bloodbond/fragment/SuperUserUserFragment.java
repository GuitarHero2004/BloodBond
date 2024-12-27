// Java
package com.example.bloodbond.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodbond.LoginView;
import com.example.bloodbond.R;
import com.example.bloodbond.helper.AuthHelper;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.SuperUser;

import java.util.Objects;

public class SuperUserUserFragment extends Fragment {
    private AuthHelper authHelper = new AuthHelper();
    private TextView superUserNameTextView, superUserEmailTextView, superUserRoleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_user_user, container, false);

        superUserNameTextView = view.findViewById(R.id.userName);
        superUserEmailTextView = view.findViewById(R.id.userEmail);
        superUserRoleTextView = view.findViewById(R.id.userRole);
        LinearLayout logoutButton = view.findViewById(R.id.customLogoutButton);

        fetchUserData();

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

        firestoreHelper.fetchSuperUserData(userId, new FirestoreHelper.OnUserDataFetchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Object data) {
                if (data instanceof SuperUser) {
                    SuperUser superUser = (SuperUser) data;

                    superUserNameTextView.setText(superUser.getName() != null ? "Name: " + superUser.getName() : "N/A");
                    superUserEmailTextView.setText(superUser.getEmail() != null ? "Email: " + superUser.getEmail() : "N/A");
                    superUserRoleTextView.setText("Role: " + superUser.getRole());
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