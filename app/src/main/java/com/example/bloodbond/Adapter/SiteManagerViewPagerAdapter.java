package com.example.bloodbond.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bloodbond.Fragment.SiteManagerMainFragment;
import com.example.bloodbond.Fragment.SiteManagerUserFragment;
import com.example.bloodbond.SiteManagerView;

public class SiteManagerViewPagerAdapter extends FragmentStateAdapter {
    public SiteManagerViewPagerAdapter(@NonNull SiteManagerView fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        switch (position) {
            case 0:
                return new SiteManagerMainFragment();  // First tab
            case 1:
                return new SiteManagerUserFragment();   // Second tab
            default:
                return new SiteManagerMainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
