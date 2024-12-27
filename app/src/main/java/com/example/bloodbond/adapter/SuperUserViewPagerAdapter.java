package com.example.bloodbond.adapter;

import com.example.bloodbond.SuperUserView;
import com.example.bloodbond.fragment.SuperUserMainFragment;
import com.example.bloodbond.fragment.SuperUserUserFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SuperUserViewPagerAdapter extends FragmentStateAdapter {
    public SuperUserViewPagerAdapter(@NonNull SuperUserView fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        switch (position) {
            case 0:
                return new SuperUserMainFragment();  // First tab
            case 1:
                return new SuperUserUserFragment();   // Second tab
            default:
                return new SuperUserMainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
