package com.zed_one.zed_one;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

public class LocationHelper {

    private Context context;
    private LocationManager locationManager;

    public LocationHelper(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // Check if location permission is granted
    public boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    // Request location updates
    public void requestLocationUpdates(LocationListener locationListener) {
        if (isLocationPermissionGranted()) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000, // minimum time interval between updates (in milliseconds)
                        10, // minimum distance interval between updates (in meters)
                        locationListener
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // Stop receiving location updates
    public void stopLocationUpdates(LocationListener locationListener) {
        if (isLocationPermissionGranted()) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
