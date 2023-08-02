package com.zed_one.zed_one;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zed_one.zed_one.databinding.ActivityMainBinding;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String UserID;
    private FirebaseDatabase db =null;
    // Firebase Declaration
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialized the authentication details
        FirebaseApp.initializeApp(this);
        // Get the instance of FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Check if the user is currently signed in
        user = auth.getCurrentUser();

        String UID = user.getUid();


        // Handling database connection
        db = FirebaseDatabase.getInstance();
        DatabaseReference reference= db.getReference().child("Users");

        // Fetching Data and Updating ListView
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    String uid = s.getValue(String.class);
                    if (uid.equals(UID)) {
                        UserID = s.getKey();
                        break; // Exit the loop once the linkedUserID is found
                    }
                }

                // linkedUserID will contain the UserID that is linked to the UID of the current user
                if (UserID != null) {
                    // The linkedUserID has been found
                    Log.d(TAG, "UserID: " + UserID);
                    binding  =ActivityMainBinding.inflate(getLayoutInflater());
                    setContentView(binding.getRoot());

                    binding.bottomNavigationView.setBackground(null);

                    replaceFragment(new DashboardFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.Dashboard);

                    binding.bottomNavigationView.setOnItemSelectedListener(item ->{
                        if (item.getItemId()==R.id.Dashboard) {
                            replaceFragment(DashboardFragment.newInstance(UserID));
                        } else if (item.getItemId()==R.id.Tracking) {
                            replaceFragment(TrakingFragment.newInstance(UserID));
                        } else if (item.getItemId()==R.id.Devices) {
                            replaceFragment(DevicesFragment.newInstance(UserID));
                        } else if (item.getItemId()== R.id.LogOut) {
                            signOut();
                        }
                        return true;
                    });
                } else {
                    // The linkedUserID was not found (handle the case if necessary)
                    Log.d(TAG, "Linked UserID not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putString("UserID", UserID);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Log_in_activity.class);
        startActivity(intent);
        finish();
    }
}