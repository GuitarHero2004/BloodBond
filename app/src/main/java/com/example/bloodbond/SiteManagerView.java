package com.example.bloodbond;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bloodbond.adapter.SiteManagerViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SiteManagerView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_manager);

        // Find TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.siteManagerTabLayout);
        ViewPager2 viewPager = findViewById(R.id.siteManagerViewPager);

        viewPager.setUserInputEnabled(false);  // Disable swipe

        // Set up the ViewPagerAdapter
        SiteManagerViewPagerAdapter adapter = new SiteManagerViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach the TabLayout to the ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Dashboard");
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setText("Donation Sites");
                    tab.setIcon(R.drawable.history);
                    break;
                case 2:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.profile);
                    break;
            }
        }).attach();
    }
}