package com.zed_one.zed_one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.zed_one.zed_one.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        replaceFragment(new DashboardFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.Dashboard);

        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if (item.getItemId()==R.id.Dashboard) {
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId()==R.id.Tracking) {
                replaceFragment(new TrakingFragment());
            } else if (item.getItemId()==R.id.Devices) {
                replaceFragment(new DevicesFragment());
            }
            return true;
        });
    }
    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}