package com.zed_one.zed_one;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;

public class TrakingFragment extends Fragment {



    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    // Map Declaration
    private MapView map = null;
    private IMapController mapController;

    // Firebase Declaration
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db =null;
    private String User_ID="User_2";


    private ListView listview =null;
    private ArrayList<String> Device_Name_List =null;
    private ArrayAdapter adapter =null;
    private ArrayList<OverlayItem> OverlayList=null;
    private ItemizedOverlayWithFocus<OverlayItem> overlay;

    private Button add_btn = null;
    private EditText ID_TextEdit= null;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_traking, container, false);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        // Setting up the MapView

        map = (MapView) rootView.findViewById(R.id.map);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(5.00);

        // Setting up view
        double userLat =35.392895,userLong=-1.091218;
        centralizeMapView(userLat,userLong,19);

        // Get the permission needed
        String[] permissions = new String[]{
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        requestPermissionsIfNecessary(permissions);

        // initialize the overlay
        OverlayList = new ArrayList<>();

        overlay = new ItemizedOverlayWithFocus<>(
                ctx,
                OverlayList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        // Handle tap on marker
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        // Handle long press on marker
                        return false;
                    }
                });

        overlay.setFocusItemsOnTap(true);

        // Add the overlay to the map
        map.getOverlays().add(overlay);

        // Setting up the ListView
        listview = (ListView) rootView.findViewById(R.id.Devices_ListView);
        Device_Name_List = new ArrayList<>();
        adapter = new ArrayAdapter<>(ctx,R.layout.devices_list, Device_Name_List);

        listview.setAdapter(adapter);

        // Handling database connection
        db = FirebaseDatabase.getInstance();
        DatabaseReference reference= db.getReference().child(User_ID).child("Devices");


        // Fetching Data and Updating ListView
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Reset the Lists
                Device_Name_List.clear();

                // Read the through the Response object
                for (DataSnapshot s : dataSnapshot.getChildren()){

                    //extracting data from the firebase snapshot
                    Device device = s.getValue(Device.class);

                    // Appending the Device ID to Device List
                    Device_Name_List.add(device.getName());
                }

                // Update Adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        // setting up the ADD section
        add_btn = rootView.findViewById(R.id.add_btn);
        ID_TextEdit =rootView.findViewById(R.id.search_bar);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID_text = ID_TextEdit.getText().toString();
                if(ID_text.isEmpty())
                    Toast.makeText(getContext(),"Enter ID", Toast.LENGTH_SHORT).show();
                else {
                    getData(ID_text);
                }
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void centralizeMapView(double UserLat, double UserLong, double Zoom){
        // Setting up initial point
        mapController = map.getController();
        mapController.setZoom(Zoom);
        GeoPoint startPoint = new GeoPoint(UserLat, UserLong);
        mapController.setCenter(startPoint);
    }

    private void getData(String DeviceID){

        // Handling database connection
        db = FirebaseDatabase.getInstance();
        DatabaseReference reference= db.getReference().child(User_ID).child("Devices").child(DeviceID);


        // Fetching Data and Updating ListView
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                OverlayList.clear();
                // Clear the existing overlay items
                overlay.removeAllItems();

                Device device = dataSnapshot.getValue(Device.class);
                if (device != null) {
                    // Data for the specific DeviceID is retrieved
                    Toast.makeText(getContext(), "" + device.getName(), Toast.LENGTH_SHORT).show();

                // Read the through the Response object


                for (Coordinates l : device.getLoc().values()){

                    // Creating Marker Point
                    GeoPoint markerPoint = new GeoPoint( l.getLatitude(), l.getLongitude() );

                    // Creating Overlay Object
                    OverlayItem DeviceOverlay = new OverlayItem(device.getName(), l.getTimestamp(), markerPoint);

                    // Appending Overlay to the list
                    OverlayList.add(DeviceOverlay);

                }

                } else {
                    // Data not found for the specified DeviceID
                    Toast.makeText(getContext(), "Device not found", Toast.LENGTH_SHORT).show();
                }
                // Add the updated overlay items
                overlay.addItems(OverlayList);

                // Add the overlay back to the map
                map.getOverlays().add(overlay);

                // Refresh the map
                map.invalidate();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}