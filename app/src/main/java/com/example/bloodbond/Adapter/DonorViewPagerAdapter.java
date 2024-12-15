package com.example.bloodbond.Adapter;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bloodbond.DonorMainFragment;
import com.example.bloodbond.DonorMapFragment;
import com.example.bloodbond.DonorView;

public class DonorViewPagerAdapter extends FragmentStateAdapter {
    public DonorViewPagerAdapter(@NonNull DonorView fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        switch (position) {
            case 0:
                return new DonorMainFragment();  // First tab
            case 1:
                return new DonorMapFragment();   // Second tab
            default:
                return new DonorMainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
