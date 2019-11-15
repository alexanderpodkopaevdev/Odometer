package com.example.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.content.ContextCompat;


public class OdometerService extends Service {
    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    private final IBinder binder = new OdometerBinder();


    private LocationListener listener;
    private LocationManager locationManager;

    private static double distansInMeters;
    private static Location lastLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distansInMeters += location.distanceTo(lastLocation);
                lastLocation = location;

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }
    }

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    public OdometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    public double getDistance() {
        return distansInMeters / 1609.344;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null && locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING) ==
                    PackageManager.PERMISSION_GRANTED)
                locationManager.removeUpdates(listener);
            locationManager = null;
            listener = null;
        }
    }
}
