package com.google.android.gms.location.sample.geofencing;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

public class GPScheck extends BroadcastReceiver {

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    Context context;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

       this.context = context;

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "GPS is Required",
                    Toast.LENGTH_SHORT).show();
          // turnonlocationtwo();
            Intent intent1 = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);

        }
    }

    public void turnonlocationtwo()
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setTitle("GPS Not Enables")  // GPS not found
                    .setMessage("Please enable the GPS") // Want to enable?
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("NO", null)
                    .show();
        }
    }

}
