package com.google.android.gms.location.sample.geofencing.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.sample.geofencing.BackgroundLocationUpdate.LocationRequestHelper;
import com.google.android.gms.location.sample.geofencing.BackgroundLocationUpdate.LocationUpdatesBroadcastReceiver;
import com.google.android.gms.location.sample.geofencing.R;

import androidx.work.WorkerParameters;

public class MyWorker extends androidx.work.Worker implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 10 * 6000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */

    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    /*
     * This method is responsible for doing the work
     * so whatever work that is needed to be performed
     * we will put it here
     *
     * For example, here I am calling the method displayNotification()
     * It will display a notification
     * So that we will understand the work is executed
     * */


    @NonNull
    @Override
    public Result doWork() {

        buildGoogleApiClient();

        //displayNotification("My MyWorker", "Inside");

        return Result.success();

    }

//    *
//            * The method is doing nothing but only generating
//    * a simple notification
//    * If you are confused about it
//    * you should check the Android Notification Tutorial
//    * *


    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.drawable.geofencing);

        notificationManager.notify(1, notification.build());



    }

    public void requestLocationUpdates() {

        //displayNotification("My MyWorker", "Requesting location update");

        try {
            Log.i("MyWorker", "Starting location updates");
            LocationRequestHelper.setRequesting(getApplicationContext(), true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            LocationRequestHelper.setRequesting(getApplicationContext(), false);
            e.printStackTrace();
        }
    }


    private void buildGoogleApiClient() {

        //displayNotification("My MyWorker", "Building google api client");

        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
//                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }



    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
