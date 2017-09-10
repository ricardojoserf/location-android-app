package com.example.fry.salvavidapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Fry on 09-Sep-17.
 */

public class LocationUtils {

    public interface LocationCallbackInterface {
        void onComplete(Location location);
    }

    public static void getLocation(final Context context, final LocationCallbackInterface callback) {

        final Location[] location = {null};

        GoogleApiClient googleApiClient;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        GoogleApiClient googleApiClient = null;
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            location[0] = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            //Toast.makeText(App.getContext(), "location came :" + location[0].toString(), Toast.LENGTH_LONG).show();
                            callback.onComplete(location[0]);
                        }

                        googleApiClient.disconnect();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

}

