package com.example.bloodbond;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbond.adapter.SuperUserViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class SuperUserView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        // Find TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.superUserTabLayout);
        ViewPager2 viewPager = findViewById(R.id.superUserViewPager);

        viewPager.setUserInputEnabled(false);  // Disable swipe

        // Set up the ViewPagerAdapter
        SuperUserViewPagerAdapter adapter = new SuperUserViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach the TabLayout to the ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Dashboard");
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.profile);
                    break;
            }
        }).attach();
    }
}