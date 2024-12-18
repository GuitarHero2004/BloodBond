package com.example.bloodbond;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bloodbond.adapter.DonorViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DonorView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        // Find TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setUserInputEnabled(false);  // Disable swipe

        // Set up the ViewPagerAdapter
        DonorViewPagerAdapter adapter = new DonorViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach the TabLayout to the ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Dashboard");  // Label for first tab
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setText("Map");  // Label for second tab
                    tab.setIcon(R.drawable.map);
                    break;
                case 2:
                    tab.setText("Profile");  // Label for third tab
                    tab.setIcon(R.drawable.profile);
                    break;
            }
        }).attach();
    }
}
