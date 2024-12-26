package com.example.bloodbond.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbond.R;
import com.example.bloodbond.adapter.MapDonationSiteAdapter;
import com.example.bloodbond.helper.FirestoreHelper;
import com.example.bloodbond.model.DonationSite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

public class DonorMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap myMap;
    private FusedLocationProviderClient locationClient;
    private RecyclerView searchResultsRecyclerView;
    private MapDonationSiteAdapter adapter;
    private final List<DonationSite> donationSites = new ArrayList<>();
    private final List<DonationSite> filteredSites = new ArrayList<>();
    private boolean isSearching = false;  // Flag to track if the user is searching

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_map, container, false);

        setupRecyclerView(view);
        setupSearchView(view);
        initializeLocationClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        return view;
    }

    private void setupRecyclerView(View view) {
        searchResultsRecyclerView = view.findViewById(R.id.donationSitesRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MapDonationSiteAdapter(filteredSites, donationSite -> {
            moveCameraToSite(donationSite); // Move camera to the selected site
            resetFilteredSites(); // Reset the filtered list to show all sites
            searchResultsRecyclerView.setVisibility(View.GONE); // Hide the results
        });

        searchResultsRecyclerView.setAdapter(adapter);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = !query.isEmpty();  // Set flag based on whether query is empty or not
                filterSites(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearching = !newText.isEmpty();  // Update flag when text changes
                filterSites(newText);
                return false;
            }
        });
    }

    private void initializeLocationClient() {
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setupMapSettings();
        fetchDonationSites();

        // Set up camera idle listener to filter sites based on the camera view
        myMap.setOnCameraIdleListener(this::filterSitesBasedOnCameraView);

        // When the camera starts moving (user interacts with the map)
        myMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                // Clear the search query and remove focus from the SearchView
                SearchView searchView = getView().findViewById(R.id.searchView);
                searchView.setQuery("", false);  // Clear the search query
                searchView.clearFocus();  // Remove focus from the SearchView

                // Reset filtered sites to show all available sites
                resetFilteredSites();
            }
        });

        // Add markers on click event
        myMap.setOnMarkerClickListener(marker -> {
            DonationSite donationSite = (DonationSite) marker.getTag();
            if (donationSite != null) {
                showBottomSheet(donationSite);
            }
            return false;
        });
    }

    private void showBottomSheet(DonationSite donationSite) {
        // Show the BottomMapInfoFragment with the selected DonationSite
        BottomMapInfoFragment bottomSheetFragment = new BottomMapInfoFragment(donationSite);
        bottomSheetFragment.setOnDismissListener(dialog -> {
            resetFilteredSites();  // Reset the filtered list
            searchResultsRecyclerView.setVisibility(filteredSites.isEmpty() ? View.GONE : View.VISIBLE); // Hide if empty
            adapter.notifyDataSetChanged(); // Update the RecyclerView
        });
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }

    // Reset filtered sites to show all available sites
    private void resetFilteredSites() {
        filteredSites.clear();  // Clear the filtered list
        filteredSites.addAll(donationSites);  // Add all donation sites back
        searchResultsRecyclerView.setVisibility(filteredSites.isEmpty() ? View.GONE : View.VISIBLE); // Hide if empty
        adapter.notifyDataSetChanged(); // Update the RecyclerView
    }

    private void filterSites(String query) {
        filteredSites.clear();  // Clear filtered list

        if (!query.isEmpty()) {
            for (DonationSite site : donationSites) {
                if (site.getSiteName().toLowerCase().contains(query.toLowerCase())) {
                    filteredSites.add(site);  // Add matching sites
                }
            }
        } else {
            // If the query is empty, reset to show all sites
            resetFilteredSites();
        }

        // Update the RecyclerView visibility
        searchResultsRecyclerView.setVisibility(filteredSites.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    // Set up camera view to hold every donation sites existed in the database
    private void filterSitesBasedOnCameraView() {
        // Check if we are currently searching or not
        if (isSearching) return;

        // Only apply the filter if the SearchView isn't active
        VisibleRegion visibleRegion = myMap.getProjection().getVisibleRegion();
        double minLat = visibleRegion.latLngBounds.southwest.latitude;
        double maxLat = visibleRegion.latLngBounds.northeast.latitude;
        double minLng = visibleRegion.latLngBounds.southwest.longitude;
        double maxLng = visibleRegion.latLngBounds.northeast.longitude;
        float zoomLevel = myMap.getCameraPosition().zoom;

        // Filter sites based on the current visible area of the map and zoom level
        filteredSites.clear();
        for (DonationSite site : donationSites) {
            if (site.getLatitude() >= minLat && site.getLatitude() <= maxLat
                    && site.getLongitude() >= minLng && site.getLongitude() <= maxLng) {

                // Optionally, adjust the filter based on zoom level, showing more/less sites
                if (zoomLevel > 12) { // Zoom level 12 is a good threshold for more specific filtering
                    filteredSites.add(site);
                } else {
                    filteredSites.add(site); // Show more sites at lower zoom levels
                }
            }
        }

        // Update the RecyclerView to display the filtered list of donation sites
        searchResultsRecyclerView.setVisibility(filteredSites.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void setupMapSettings() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        myMap.setMyLocationEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        showCurrentLocation();
    }

    private void showCurrentLocation() {
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });
    }

    private void fetchDonationSites() {
        new FirestoreHelper().fetchDonationSites(new FirestoreHelper.OnDonationSitesFetchListener() {
            @Override
            public void onSuccess(List<DonationSite> data) {
                donationSites.addAll(data);
                filteredSites.addAll(data);
                adapter.notifyDataSetChanged();

                // Set a custom marker icon
                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.blood_bag);

                for (DonationSite site : data) {
                    LatLng location = new LatLng(site.getLatitude(), site.getLongitude());
                    Marker marker = myMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(site.getSiteName())
                            .icon(customMarker));

                    // Attach the DonationSite object to the marker
                    marker.setTag(site);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveCameraToSite(DonationSite donationSite) {
        LatLng location = new LatLng(donationSite.getLatitude(), donationSite.getLongitude());
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // Zoom in to the site

        // Clear the filtered list and reset search results
        resetFilteredSites();
        searchResultsRecyclerView.setVisibility(View.GONE);  // Hide the search results
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(myMap);
        }
    }
}
