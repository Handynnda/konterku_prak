package com.example.konterku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Dashboard extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi TabLayout dan ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ImageView iconlogout = findViewById(R.id.imagelogout);

        iconlogout.setOnClickListener(view -> {
            Intent intent = new Intent(Dashboard.this, Login.class);
            startActivity(intent);
        });

        ImageView iconpesan = findViewById(R.id.imagepesan);

        iconpesan.setOnClickListener(view -> {
            String phoneNumber = "62895413920668";

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                intent.setPackage("com.whatsapp");
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(Dashboard.this, "WhatsApp tidak terpasang di perangkat ini", Toast.LENGTH_SHORT).show();
            }
        });

        // Set Adapter untuk ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Sinkronkan TabLayout dengan ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Produk");
                        break;
                    case 1:
                        tab.setText("Histori");
                        break;
                    case 2:
                        tab.setText("Profil");
                        break;
                }
            }
        }).attach();
    }
}
