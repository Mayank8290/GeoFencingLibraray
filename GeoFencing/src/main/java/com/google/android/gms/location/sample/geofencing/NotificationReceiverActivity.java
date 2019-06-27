package com.google.android.gms.location.sample.geofencing;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.sample.geofencing.BackgroundLocationUpdate.LocationRequestHelper;
import com.google.android.gms.location.sample.geofencing.BackgroundLocationUpdate.LocationUpdatesBroadcastReceiver;
import com.google.android.gms.location.sample.geofencing.GetterSetter.GeoFenceArraylist;
import com.google.android.gms.location.sample.geofencing.GetterSetter.LocationDataGetterSetter;
import com.google.android.gms.location.sample.geofencing.LocalData.LocalData;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NotificationReceiverActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        OnCompleteListener<Void>,
        LocationListener,

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener,

        ActivityCompat.OnRequestPermissionsResultCallback {

    // for background location update

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */

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

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * The entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;


    //


    int opentime = 0;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 500;


    ImageView openspinnerimage;

    LinearLayout punchinoutbutton;

    LatLng usercurrentlocation ;
    // for geo fencing

    private static final String TAG = NotificationReceiverActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    ArrayList<LocationDataGetterSetter> locationdata = new ArrayList<>();

    ImageView information;


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public void requestLocationUpdates() {
        try {
            Log.i(TAG, "Starting location updates");
            LocationRequestHelper.setRequesting(this, true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnectionSuspended(int i) {
        final String text = "Connection suspended";
        Log.w(TAG, text + ": Error code: " + i);
        showSnackbar("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        final String text = "Exception while connecting to Google Play services";
        Log.w(TAG, text + ": " + connectionResult.getErrorMessage());
        showSnackbar(text);
    }

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;

    private NotificationReceiverActivity.PendingGeofenceTask mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.NONE;


    Button displayalldata;


    //

    Spinner spinner;
    ArrayList<String> contacts = new ArrayList<>();

    TextView punchinouttext;

   // private FirebaseAnalytics mFirebaseAnalytics;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_receiver);


        // firebase analytics
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//
////
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Opened geo Fence");
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        //

        information = (ImageView)findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                Information forgotPasswordDialog = new Information();
                forgotPasswordDialog.show(fm, "redemms");

            }
        });

        // getting data from previos activity

        try {
            String Ecno = getIntent().getStringExtra("ecno");
            String userLocation = getIntent().getStringExtra("location");
            String name = getIntent().getStringExtra("name");
            String version = getIntent().getStringExtra("version");


            new LocalData(getApplicationContext()).setVersionName(version);

            String testecno = "100000";
            new LocalData(getApplicationContext()).setuserecno(Ecno);
            new LocalData(getApplicationContext()).setName(name);

            // Toast.makeText(getApplicationContext(),"Ec No : "+Ecno+", Location : "+userLocation,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            //Toast.makeText(getApplicationContext(),"Data Not Getting",Toast.LENGTH_SHORT).show();
        }


        // data getting end

       // getSupportActionBar().hide();

        punchinoutbutton = (LinearLayout) findViewById(R.id.punchinoutbutton);

        punchinouttext = (TextView) findViewById(R.id.punchinouttext);

        String todaydate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchin")) {
            punchinouttext.setText("Punch In");

        } else if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchout")) {
            punchinouttext.setText("Punch Out");

        }

        String lastpunchdate = new LocalData(getApplicationContext()).getuserlastpunchinpunchoutdate();

        if (new LocalData(getApplicationContext()).getuserlastpunchinpunchoutdate().equals(todaydate)) {
            punchinouttext.setText("Punch Out");
            new LocalData(getApplicationContext()).setpunchinandpounchout("punchout");
        } else {
            new LocalData(getApplicationContext()).setpunchinandpounchout("punchin");
            new LocalData(getApplicationContext()).setuserlastpunchinpunchoutdate("");
            punchinouttext.setText("Punch In");
        }

        openspinnerimage = (ImageView) findViewById(R.id.openspinnerimage);
        openspinnerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });

        punchinoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try
                {
                    if(Double.valueOf(getthedifferenceinmeters())  > Double.valueOf(new LocalData(getApplicationContext()).getuserselectedradius()))
                    {
                        Toast.makeText(getApplicationContext(),"Your current location is not matching any Hero office location.",Toast.LENGTH_LONG).show();
                        return;
                    }else if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchin")) {


                        //


                        // alert user for punch

                        new AlertDialog.Builder(NotificationReceiverActivity.this)
                                .setTitle("Punch In")
                                .setMessage("Are you sure you want to punch in?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation

                                        senddatatoserver("punchin");
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    } else if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchout")) {

                        // if punch in already done

                        LinearLayout b = (LinearLayout) v;
                        TextView buttonText = (TextView) b.getChildAt(1);
                        String text = buttonText.getText().toString();
                        if (text.equals("Punch In")) {
                            Toast.makeText(getApplicationContext(), "Punch In Already Done From your location", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(NotificationReceiverActivity.this)
                                    .setTitle("Punch Out")
                                    .setMessage("Are you sure you want to punch out?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation

                                            senddatatoserver("punchout");
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }


                    }
                }
                catch (Exception e)
                {
                    Log.wtf("Error",e.toString());
                   Toast.makeText(getApplicationContext(),"Location not updated yet",Toast.LENGTH_SHORT).show();
                }




            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // for geo fencing


        // Get the UI widgets.
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
        displayalldata = (Button) findViewById(R.id.displayalldata);


        displayalldata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(NotificationReceiverActivity.this,DisplayPrevousTriggers.class));

                FragmentManager fm = getSupportFragmentManager();
                DisplayPrevousTriggers forgotPasswordDialog = new DisplayPrevousTriggers();
                forgotPasswordDialog.show(fm, "redemm");

            }
        });
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.


        mGeofencingClient = LocationServices.getGeofencingClient(this);

        //


        spinner = (Spinner) findViewById(R.id.spinner);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if (position > 0) {

                    String location = locationdata.get(position - 1).getLocation();
                    mGeofenceList.clear();

//                    if (!location.equals(new LocalData(getApplicationContext()).getuserselctedlocation())) {

                        populateGeofenceList(position - 1);
                        mMap.clear();

                        // drawing a circle on the map
                        LatLng geofencelatLng = new LatLng(Double.valueOf(locationdata.get(position - 1).getLatitude()), Double.valueOf(locationdata.get(position - 1).getLongitude()));
                        mMap.addCircle(new CircleOptions()
                                .center(geofencelatLng)
                                .radius(Float.valueOf(locationdata.get(position - 1).getRadius()))
                                .strokeWidth(0f)
                                .fillColor(0x5500ff00));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
                        mMap.animateCamera(cameraUpdate);
                       // locationManager.removeUpdates(NotificationReceiverActivity.this);

                        //setting user selected data to locaL
                        new LocalData(getApplicationContext()).setuserselctedlocation(location);
                        new LocalData(getApplicationContext()).setuserselectedlongitude(locationdata.get(position - 1).getLongitude());
                        new LocalData(getApplicationContext()).setuserselectedlatitude(locationdata.get(position - 1).getLatitude());
                        new LocalData(getApplicationContext()).setuserselectedradius(locationdata.get(position - 1).getRadius());

                        if (!checkPermissions()) {
                            mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
                            requestPermissions();
                            return;
                        }
                        if (getGeofencesAdded()) {
                            removeGeofences();
                            addGeofences();
                        } else {
                            addGeofences();
                        }

                        requestLocationUpdates();

                    //}
//                    else {
//                        mMap.clear();
//                        LatLng geofencelatLng = new LatLng(Double.valueOf(locationdata.get(position - 1).getLatitude()), Double.valueOf(locationdata.get(position - 1).getLongitude()));
//                        mMap.addCircle(new CircleOptions()
//                                .center(geofencelatLng)
//                                .radius(Float.valueOf(locationdata.get(position - 1).getRadius()))
//                                .strokeWidth(0f)
//                                .fillColor(0x5500ff00));
//
//                        removeGeofences();
//                        getthelocation();
//
//                        // Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_LONG).show();
//                    }

                } else {

                }


//                if(position == 1) {
//
//                    if (!location.equals(new LocalData(getApplicationContext()).getuserselctedlocation())) {
//                    mMap.clear();
//
//                    LatLng geofencelatLng = new LatLng(28.5388096, 77.1514862);
//
//                    mMap.addCircle(new CircleOptions()
//                            .center(geofencelatLng)
//                            .radius(Constants.GEOFENCE_RADIUS_HO_THE_GRAND)
//                            .strokeWidth(0f)
//                            .fillColor(0x5500ff00));
//
//
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
//                    mMap.animateCamera(cameraUpdate);
//                    locationManager.removeUpdates(NotificationReceiverActivity.this);
//
//                    new LocalData(getApplicationContext()).setuserselctedlocation(location);
//                    populateGeofenceList();
//
//                    if (!checkPermissions()) {
//                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
//                        requestPermissions();
//                        return;
//                    }
//                    if (getGeofencesAdded()) {
//                        removeGeofences();
//                        addGeofences();
//                    } else {
//                        addGeofences();
//                    }
//                }
//                else
//                    {
//                        mMap.clear();
//
//                        LatLng geofencelatLng = new LatLng(28.5388096, 77.1514862);
//
//                        mMap.addCircle(new CircleOptions()
//                                .center(geofencelatLng)
//                                .radius(Constants.GEOFENCE_RADIUS_HO_THE_GRAND)
//                                .strokeWidth(0f)
//                                .fillColor(0x5500ff00));
//
//                       // Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_LONG).show();
//                    }
//
//                }
//                else if(position == 2) {
//
//                    if (!location.equals(new LocalData(getApplicationContext()).getuserselctedlocation())) {
//
//                        requestLocationUpdates();
//
//                    mMap.clear();
//
//                    LatLng geofencelatLng = new LatLng(28.412402603746372, 77.0433149267526);
//
//                    mMap.addCircle(new CircleOptions()
//                            .center(geofencelatLng)
//                            .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
//                            .strokeWidth(0f)
//                            .fillColor(0x5500ff00));
//
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
//                    mMap.animateCamera(cameraUpdate);
//                    locationManager.removeUpdates(NotificationReceiverActivity.this);
//
//                    new LocalData(getApplicationContext()).setuserselctedlocation(location);
//                    populateGeofenceList();
//
//
//                    if (!checkPermissions()) {
//                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
//                        requestPermissions();
//                        return;
//                    }
//                    if (getGeofencesAdded()) {
//                        removeGeofences();
//                        addGeofences();
//                    } else {
//                        addGeofences();
//                    }
//
//                }
//                else
//                    {
//                        mMap.clear();
//
//                        LatLng geofencelatLng = new LatLng(28.412402603746372, 77.0433149267526);
//
//                        mMap.addCircle(new CircleOptions()
//                                .center(geofencelatLng)
//                                .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
//                                .strokeWidth(0f)
//                                .fillColor(0x5500ff00));
//
//
//                       // Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_LONG).show();
//                    }
//
//                }
//                else if(position == 3) {
//
//                    if (!location.equals(new LocalData(getApplicationContext()).getuserselctedlocation())) {
//
//
//
//                    mMap.clear();
//
//                    LatLng geofencelatLng = new LatLng(28.53988, 77.15401);
//
//                    mMap.addCircle(new CircleOptions()
//                            .center(geofencelatLng)
//                            .radius(Constants.GEOFENCE_RADIUS_HMCO)
//                            .strokeWidth(0f)
//                            .fillColor(0x5500ff00));
//
//
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
//                    mMap.animateCamera(cameraUpdate);
//                    locationManager.removeUpdates(NotificationReceiverActivity.this);
//
//                    new LocalData(getApplicationContext()).setuserselctedlocation(location);
//                    populateGeofenceList();
//
//                    if (!checkPermissions()) {
//                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
//                        requestPermissions();
//                        return;
//                    }
//                    if (getGeofencesAdded()) {
//                        removeGeofences();
//                        addGeofences();
//                    } else {
//                        addGeofences();
//                    }
//                }
//                else
//                    {
//                        mMap.clear();
//
//                        LatLng geofencelatLng = new LatLng(28.53988, 77.15401);
//
//                        mMap.addCircle(new CircleOptions()
//                                .center(geofencelatLng)
//                                .radius(Constants.GEOFENCE_RADIUS_HMCO)
//                                .strokeWidth(0f)
//                                .fillColor(0x5500ff00));
//                      //  Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_LONG).show();
//                    }
//
//                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        // getthelocation();

    }


    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return;
//        }
//
//        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        } else {
//            // Display the missing permission error dialog when the fragments resume.
//            mPermissionDenied = true;
//        }
//    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.wtf("CurrentLocation", latLng.toString());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
       // locationManager.removeUpdates(this);

        usercurrentlocation = latLng;
        //drawing circle


        if (new LocalData(getApplicationContext()).getuserselctedlocation().equals("HO , The Grand Hotel")) {
            LatLng geofencelatLng = new LatLng(28.5388096, 77.1514862);

            mMap.addCircle(new CircleOptions()
                    .center(geofencelatLng)
                    .radius(50)
                    .strokeWidth(0f)
                    .fillColor(0x5500ff00));
        } else if (new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCI , Sohna Road")) {
            LatLng geofencelatLng = new LatLng(28.412402603746372, 77.0433149267526);

            mMap.addCircle(new CircleOptions()
                    .center(geofencelatLng)
                    .radius(50)
                    .strokeWidth(0f)
                    .fillColor(0x5500ff00));
        } else if (new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCO")) {
            LatLng geofencelatLng = new LatLng(28.53988, 77.15401);

            mMap.addCircle(new CircleOptions()
                    .center(geofencelatLng)
                    .radius(50)
                    .strokeWidth(0f)
                    .fillColor(0x5500ff00));
        }


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

    public void turnuserlocationon() {
        int LOCATION_SETTINGS_REQUEST = 12;

        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(NotificationReceiverActivity.this,
                                                LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });

    }


    public void turnonlocationtwo() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                    if (!isNetworkAvailable()) {
                        displayinternetnotconnected();
                    } else {
                        getthelocation();
                    }
                    //


                    //

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        NotificationReceiverActivity.this,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //Toast.makeText(NotificationReceiverActivity.this,states.isLocationPresent()+"",Toast.LENGTH_SHORT).show();
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, NotificationReceiverActivity.this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
                        if (!isNetworkAvailable()) {
                            displayinternetnotconnected();
                        } else {
                            getthelocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        // or in some android system it's return canceld

                        Intent intent1 = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);


                        Toast.makeText(NotificationReceiverActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    // for geo fencing

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();

        buildGoogleApiClient();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        if (!checkPermissions()) {
            requestPermissions();
        } else {

            //

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER


            //

            turnonlocationtwo();
            performPendingGeofenceTask();
        }


    }


    public void displayinternetnotconnected() {
        Snackbar.make(findViewById(R.id.layout), "Internet is not Connected", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isNetworkAvailable()) {
                            displayinternetnotconnected();
                        } else {
                            getthelocation();
                        }

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        if (getGeofencesAdded()) {
            removeGeofences();
        } else {
            addGeofences();
        }

    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        //mMap.clear();
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     *
     * @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;

            if(getString(messageId).equals("Geofences added"))
            {
                Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
            }



            Log.wtf("geomessages",getString(messageId));

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this,
                GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private void populateGeofenceList(int postion) {


        // setting geo fence dynamically

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(String.valueOf(locationdata.get(postion).getLocation()))

                // Set the circular region of this geofence.
                .setCircularRegion(
                        Double.valueOf(locationdata.get(postion).getLatitude()),
                        Double.valueOf(locationdata.get(postion).getLongitude()),
                        Float.valueOf(locationdata.get(postion).getRadius())
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());


        //


//     if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HO , The Grand Hotel"))
//     {
//         for (Map.Entry<String, LatLng> entry : Constants.HO_THE_GRAND.entrySet()) {
//
//             mGeofenceList.add(new Geofence.Builder()
//                     // Set the request ID of the geofence. This is a string to identify this
//                     // geofence.
//                     .setRequestId(entry.getKey())
//
//                     // Set the circular region of this geofence.
//                     .setCircularRegion(
//                             entry.getValue().latitude,
//                             entry.getValue().longitude,
//                             Constants.GEOFENCE_RADIUS_HO_THE_GRAND
//                     )
//
//                     // Set the expiration duration of the geofence. This geofence gets automatically
//                     // removed after this period of time.
//                     //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                     .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                     // Set the transition types of interest. Alerts are only generated for these
//                     // transition. We track entry and exit transitions in this sample.
//                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                             Geofence.GEOFENCE_TRANSITION_EXIT)
//
//                     // Create the geofence.
//                     .build());
//         }
//     }
//     else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCI , Sohna Road"))
//     {
//         for (Map.Entry<String, LatLng> entry : Constants.HMCI_SOHNA_ROAD.entrySet()) {
//
//             mGeofenceList.add(new Geofence.Builder()
//                     // Set the request ID of the geofence. This is a string to identify this
//                     // geofence.
//                     .setRequestId(entry.getKey())
//
//                     // Set the circular region of this geofence.
//                     .setCircularRegion(
//                             entry.getValue().latitude,
//                             entry.getValue().longitude,
//                             Constants.GEOFENCE_RADIUS_IN_METERS
//                     )
//
//                     // Set the expiration duration of the geofence. This geofence gets automatically
//                     // removed after this period of time.
//                     //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                     .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                     // Set the transition types of interest. Alerts are only generated for these
//                     // transition. We track entry and exit transitions in this sample.
//                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                             Geofence.GEOFENCE_TRANSITION_EXIT)
//
//                     // Create the geofence.
//                     .build());
//         }
//     }
//     else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCO"))
//     {
//         for (Map.Entry<String, LatLng> entry : Constants.HMCO.entrySet()) {
//
//             mGeofenceList.add(new Geofence.Builder()
//                     // Set the request ID of the geofence. This is a string to identify this
//                     // geofence.
//                     .setRequestId(entry.getKey())
//
//                     // Set the circular region of this geofence.
//                     .setCircularRegion(
//                             entry.getValue().latitude,
//                             entry.getValue().longitude,
//                             Constants.GEOFENCE_RADIUS_HMCO
//                     )
//
//                     // Set the expiration duration of the geofence. This geofence gets automatically
//                     // removed after this period of time.
//                     //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                     .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                     // Set the transition types of interest. Alerts are only generated for these
//                     // transition. We track entry and exit transitions in this sample.
//                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                             Geofence.GEOFENCE_TRANSITION_EXIT)
//
//                     // Create the geofence.
//                     .build());
//         }
//     }


    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
//        if (getGeofencesAdded()) {
//            mAddGeofencesButton.setEnabled(false);
//            mRemoveGeofencesButton.setEnabled(true);
//        } else {
//            mAddGeofencesButton.setEnabled(true);
//            mRemoveGeofencesButton.setEnabled(false);
//        }
        if (getGeofencesAdded()) {
            mAddGeofencesButton.setText("Off Geofence");
        } else {
            mAddGeofencesButton.setText("On Geofence");
        }

    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == NotificationReceiverActivity.PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == NotificationReceiverActivity.PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.wtf(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(NotificationReceiverActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.wtf(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(NotificationReceiverActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");


                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER


                turnonlocationtwo();
                enableMyLocation();
                performPendingGeofenceTask();

            } else {
                // Permission denied.
                mPermissionDenied = true;
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.NONE;
            }


        }
        else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");


                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER


                turnonlocationtwo();
                enableMyLocation();
                performPendingGeofenceTask();

            } else {
                // Permission denied.
                mPermissionDenied = true;
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.NONE;
            }
        }
    }

    //


    public void addNotification(View view) {

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(mChannel);
            }

        }


        Intent resultIntent = new Intent(this, NotificationReceiverActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationReceiverActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hero Motocorp")
                .setContentText("In and Out Geo Fence")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("From Here Punch In and Punch Out will Trigger"))
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(android.R.color.holo_red_dark));
//                .addAction(R.drawable.ic_launcher, "Punch In", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "Punch Out", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "And more", resultPendingIntent);


        if (notificationManager != null) {

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }

    //

    @Override
    protected void onResume() {
        super.onResume();

//        getApplicationContext().registerReceiver(new GPScheck(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }


    // get the distance between two cordinates


    //


    public double getthedifferenceinmeters() {

        LatLng usercurrent = new LatLng(usercurrentlocation.latitude, usercurrentlocation.longitude);
        LatLng officelocation = new LatLng(Double.valueOf(new LocalData(getApplicationContext()).getuserselectedlatitude()),Double.valueOf(new LocalData(getApplicationContext()).getuserselectedlongitude()));

        Location locationA = new Location("point A");
        locationA.setLatitude(usercurrent.latitude);
        locationA.setLongitude(usercurrent.longitude);


        Location locationB = new Location("point B");
        locationB.setLatitude(officelocation.latitude);
        locationB.setLongitude(officelocation.longitude);

        double distance = locationA.distanceTo(locationB);

        return distance;
    }


    public void senddatatoserver(String event) {

            String url = ServerUrl.sendData;
            HashMap<String, String> params = new HashMap<>();
            if (event.equals("punchin")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                params.put("in_punch", currentDateandTime);
                params.put("out_punch", "");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                params.put("in_punch", "");
                params.put("out_punch", currentDateandTime);
            }

            params.put("distance",String.valueOf(getthedifferenceinmeters()));
            params.put("name", new LocalData(getApplicationContext()).getName());
            params.put("versionname", new LocalData(getApplicationContext()).getVersionName());
            params.put("ec_no", new LocalData(getApplicationContext()).getuserecno());
            params.put("location", new LocalData(getApplicationContext()).getuserselctedlocation());
            params.put("status", "M");
            params.put("coordinates", "Latitude : " + usercurrentlocation.latitude + " , Longitude : " + usercurrentlocation.longitude);


            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
//                    if(response.optString("status").equals("success")) {


                    Log.wtf("geoattendecneresponse", response.toString());

                    if (response.optString("msg").equals("Success")) {
                        // Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();


                        if (event.equals("punchin")) {
                            new LocalData(getApplicationContext()).setpunchinandpounchout("punchout");
                            punchinouttext.setText("Punch Out");

                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                            new LocalData(getApplicationContext()).setuserlastpunchinpunchoutdate(date);

                        }

                    } else if (response.optString("msg").equals("No update")) {

                    }

//                    }else
//                    {
//                        Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
//                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.wtf("Error", error.toString());
                }
            }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", System.getProperty("http.agent"));
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);






            // saving data to local
            ArrayList<GeoFenceArraylist> arraylists = new ArrayList<>();
            if(event.equals("punchout"))
            {
                if(new LocalData(getApplicationContext()).getDime().equals("0"))
                {

                }
                else
                {
                    Gson gson = new Gson();
                    String json = new LocalData(getApplicationContext()).getDime();
                    Type type = new TypeToken<ArrayList<GeoFenceArraylist>>() {}.getType();
                    arraylists =  gson.fromJson(json, type);
                }


                Date currentTime = Calendar.getInstance().getTime();

                GeoFenceArraylist data = new GeoFenceArraylist();
                data.setEvent("Exited : "+new LocalData(getApplicationContext()).getuserselctedlocation());
                data.setName("Hero Office");
                data.setTime(String.valueOf(currentTime));
                data.setProvider("Manually Geo Fence");

                arraylists.add(data);



                // save the task list to preference

                Gson gson1 = new Gson();
                String json1 = gson1.toJson(arraylists);
                new LocalData(getApplicationContext()).setDime(json1);
            }

    }


    public void getthelocation() {

        opentime = 1;

        locationdata.clear();
        contacts.clear();

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
// To dismiss the dialog


        String url = ServerUrl.getthelocation;
        HashMap<String, String> params = new HashMap<>();


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                Log.wtf("locationresponse", response.toString());
                progress.dismiss();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);

                    LocationDataGetterSetter data = new LocationDataGetterSetter();
                    data.setLatitude(jsonObject.optString("latitude"));
                    data.setLocation(jsonObject.optString("location"));
                    data.setLongitude(jsonObject.optString("longitude"));
                    data.setRadius(jsonObject.optString("radius"));

                    locationdata.add(data);
                }


                contacts.add("Select Location");
                for (int i = 0; i < locationdata.size(); i++) {
                    contacts.add(locationdata.get(i).getLocation());


                }


                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, contacts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);


                // set the user choosed to the spinner

                try {
                    if (new LocalData(getApplicationContext()).getuserselctedlocation().equals("")) {
                        spinner.setSelection(1);
                    } else {
                        for (int i = 0; i < contacts.size(); i++) {
                            if (new LocalData(getApplicationContext()).getuserselctedlocation().equals(contacts.get(i))) {
                                spinner.setSelection(i);
                            }

                        }
                    }
                } catch (Exception e) {

                }


                //


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error", error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        requestQueue.add(jsonObjectRequest);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // for background location update


        //
    }
}

