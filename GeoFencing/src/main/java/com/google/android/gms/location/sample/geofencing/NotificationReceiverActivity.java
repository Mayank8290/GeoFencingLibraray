package com.google.android.gms.location.sample.geofencing;


import com.google.android.gms.common.api.ApiException;
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


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
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

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;



/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class NotificationReceiverActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        OnCompleteListener<Void>,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

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
    private static final float MIN_DISTANCE = 1000;


    ImageView openspinnerimage;

    LinearLayout punchinoutbutton;


    // for geo fencing

    private static final String TAG = NotificationReceiverActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

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


    TextView punchinouttext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);


        //getSupportActionBar().hide();

        punchinoutbutton = (LinearLayout) findViewById(R.id.punchinoutbutton);

        punchinouttext = (TextView)findViewById(R.id.punchinouttext);

        if(new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchin"))
        {
            punchinouttext.setText("Punch In");

        }
        else if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchout"))
        {
            punchinouttext.setText("Punch Out");

        }

        openspinnerimage = (ImageView)findViewById(R.id.openspinnerimage);
        openspinnerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });

        punchinoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchin"))
               {
                   // alert user for punch

                   new AlertDialog.Builder(NotificationReceiverActivity.this)
                           .setTitle("Punch In")
                           .setMessage("Are you sure you want to punch in?")

                           // Specifying a listener allows you to take an action before dismissing the dialog.
                           // The dialog is automatically dismissed when a dialog button is clicked.
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // Continue with delete operation
                                   punchinouttext.setText("Punch Out");
                                   new LocalData(getApplicationContext()).setpunchinandpounchout("punchout");
                               }
                           })

                           // A null listener allows the button to dismiss the dialog and take no further action.
                           .setNegativeButton(android.R.string.no, null)
                           .setIcon(android.R.drawable.ic_dialog_alert)
                           .show();




               }
               else if (new LocalData(getApplicationContext()).getpunchinandpounchout().equals("punchout"))
               {
                   new AlertDialog.Builder(NotificationReceiverActivity.this)
                           .setTitle("Punch Out")
                           .setMessage("Are you sure you want to punch out?")

                           // Specifying a listener allows you to take an action before dismissing the dialog.
                           // The dialog is automatically dismissed when a dialog button is clicked.
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // Continue with delete operation
                                   punchinouttext.setText("Punch In");
                                   new LocalData(getApplicationContext()).setpunchinandpounchout("punchin");
                               }
                           })

                           // A null listener allows the button to dismiss the dialog and take no further action.
                           .setNegativeButton(android.R.string.no, null)
                           .setIcon(android.R.drawable.ic_dialog_alert)
                           .show();


               }

            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //turnuserlocationon();




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


        spinner = (Spinner)findViewById(R.id.spinner);





        ArrayList<String> contacts = new ArrayList<>();
        contacts.add("Select Location");
       contacts.add("HO , The Grand Hotel");
       contacts.add("HMCI , Sohna Road");
       contacts.add("HMCO");

//        GeoFenceArraylist data4 = new GeoFenceArraylist();
//        data4.setName("Halor Plant");
//        contacts.add(data4);
//
//        GeoFenceArraylist data5 = new GeoFenceArraylist();
//        data5.setName("Gurgaon Plant");
//        contacts.add(data5);


        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  R.layout.simple_spinner_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        // set the user choosed to the spinner


        if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HO , The Grand Hotel"))
        {
           spinner.setSelection(1);
        }
        else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCI , Sohna Road"))
        {
            spinner.setSelection(2);
        }
        else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCO"))
        {
            spinner.setSelection(3);
        }

        //


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                String location = contacts.get(position);
                if(position > 0)
                {
                    //Toast.makeText(getApplicationContext(),contacts.get(position),Toast.LENGTH_LONG).show();
                    //mAddGeofencesButton.setVisibility(View.VISIBLE);

                    mGeofenceList.clear();

                    new LocalData(getApplicationContext()).setuserselctedlocation(location);

                    Toast.makeText(getApplicationContext(),new LocalData(getApplicationContext()).getuserselctedlocation(),Toast.LENGTH_LONG).show();

                }



                if(position == 1)
                {


                    mMap.clear();

                    LatLng geofencelatLng = new LatLng(28.5388096,77.1514862);

                    mMap.addCircle(new CircleOptions()
                            .center(geofencelatLng)
                            .radius(Constants.GEOFENCE_RADIUS_HO_THE_GRAND)
                            .strokeWidth(0f)
                            .fillColor(0x5500ff00));


                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
                    mMap.animateCamera(cameraUpdate);
                    locationManager.removeUpdates(NotificationReceiverActivity.this);



                    populateGeofenceList();

                    if (!checkPermissions()) {
                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
                        requestPermissions();
                        return;
                    }
                    if(getGeofencesAdded())
                    {
                        removeGeofences();
                        addGeofences();
                    }
                    else {
                        addGeofences();
                    }

                }
                else if(position == 2)
                {
                    mMap.clear();

                    LatLng geofencelatLng = new LatLng(28.412402603746372,77.0433149267526);

                    mMap.addCircle(new CircleOptions()
                            .center(geofencelatLng)
                            .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
                            .strokeWidth(0f)
                            .fillColor(0x5500ff00));

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
                    mMap.animateCamera(cameraUpdate);
                    locationManager.removeUpdates(NotificationReceiverActivity.this);


                    populateGeofenceList();


                    if (!checkPermissions()) {
                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
                        requestPermissions();
                        return;
                    }
                    if(getGeofencesAdded())
                    {
                        removeGeofences();
                        addGeofences();
                    }
                    else {
                        addGeofences();
                    }

                }
                else if(position == 3)
                {

                    mMap.clear();

                    LatLng geofencelatLng = new LatLng(28.53988,77.15401);

                    mMap.addCircle(new CircleOptions()
                            .center(geofencelatLng)
                            .radius(Constants.GEOFENCE_RADIUS_HMCO)
                            .strokeWidth(0f)
                            .fillColor(0x5500ff00));


                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(geofencelatLng, 15);
                    mMap.animateCamera(cameraUpdate);
                    locationManager.removeUpdates(NotificationReceiverActivity.this);

                    populateGeofenceList();

                    if (!checkPermissions()) {
                        mPendingGeofenceTask = NotificationReceiverActivity.PendingGeofenceTask.ADD;
                        requestPermissions();
                        return;
                    }
                    if(getGeofencesAdded())
                    {
                        removeGeofences();
                        addGeofences();
                    }
                    else {
                        addGeofences();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //


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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);

        //drawing circle


        if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HO , The Grand Hotel"))
        {
            LatLng geofencelatLng = new LatLng(28.5388096,77.1514862);

        mMap.addCircle(new CircleOptions()
                .center(geofencelatLng)
                .radius(50)
                .strokeWidth(0f)
                .fillColor(0x5500ff00));
        }
        else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCI , Sohna Road"))
        {
            LatLng geofencelatLng = new LatLng(28.412402603746372,77.0433149267526);

        mMap.addCircle(new CircleOptions()
                .center(geofencelatLng)
                .radius(50)
                .strokeWidth(0f)
                .fillColor(0x5500ff00));
        }
        else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCO"))
        {
            LatLng geofencelatLng = new LatLng(28.53988,77.15401);

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

    public void turnuserlocationon()
    {
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


    public void turnonlocationtwo()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> task=LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.


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
                        Toast.makeText(NotificationReceiverActivity.this,states.isLocationPresent()+"",Toast.LENGTH_SHORT).show();
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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(NotificationReceiverActivity.this,"Canceled",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    // for geo fencing

    @Override
    public void onStart() {
        super.onStart();

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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER


            //

            turnonlocationtwo();
            performPendingGeofenceTask();
        }
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
        if(getGeofencesAdded())
        {
            removeGeofences();
        }
        else {
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
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
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
    private void populateGeofenceList() {


     if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HO , The Grand Hotel"))
     {
         for (Map.Entry<String, LatLng> entry : Constants.HO_THE_GRAND.entrySet()) {

             mGeofenceList.add(new Geofence.Builder()
                     // Set the request ID of the geofence. This is a string to identify this
                     // geofence.
                     .setRequestId(entry.getKey())

                     // Set the circular region of this geofence.
                     .setCircularRegion(
                             entry.getValue().latitude,
                             entry.getValue().longitude,
                             Constants.GEOFENCE_RADIUS_HO_THE_GRAND
                     )

                     // Set the expiration duration of the geofence. This geofence gets automatically
                     // removed after this period of time.
                     .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                     // Set the transition types of interest. Alerts are only generated for these
                     // transition. We track entry and exit transitions in this sample.
                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                             Geofence.GEOFENCE_TRANSITION_EXIT)

                     // Create the geofence.
                     .build());
         }
     }
     else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCI , Sohna Road"))
     {
         for (Map.Entry<String, LatLng> entry : Constants.HMCI_SOHNA_ROAD.entrySet()) {

             mGeofenceList.add(new Geofence.Builder()
                     // Set the request ID of the geofence. This is a string to identify this
                     // geofence.
                     .setRequestId(entry.getKey())

                     // Set the circular region of this geofence.
                     .setCircularRegion(
                             entry.getValue().latitude,
                             entry.getValue().longitude,
                             Constants.GEOFENCE_RADIUS_IN_METERS
                     )

                     // Set the expiration duration of the geofence. This geofence gets automatically
                     // removed after this period of time.
                     .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                     // Set the transition types of interest. Alerts are only generated for these
                     // transition. We track entry and exit transitions in this sample.
                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                             Geofence.GEOFENCE_TRANSITION_EXIT)

                     // Create the geofence.
                     .build());
         }
     }
     else if(new LocalData(getApplicationContext()).getuserselctedlocation().equals("HMCO"))
     {
         for (Map.Entry<String, LatLng> entry : Constants.HMCO.entrySet()) {

             mGeofenceList.add(new Geofence.Builder()
                     // Set the request ID of the geofence. This is a string to identify this
                     // geofence.
                     .setRequestId(entry.getKey())

                     // Set the circular region of this geofence.
                     .setCircularRegion(
                             entry.getValue().latitude,
                             entry.getValue().longitude,
                             Constants.GEOFENCE_RADIUS_HMCO
                     )

                     // Set the expiration duration of the geofence. This geofence gets automatically
                     // removed after this period of time.
                     .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                     // Set the transition types of interest. Alerts are only generated for these
                     // transition. We track entry and exit transitions in this sample.
                     .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                             Geofence.GEOFENCE_TRANSITION_EXIT)

                     // Create the geofence.
                     .build());
         }
     }


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
        if(getGeofencesAdded()) {
            mAddGeofencesButton.setText("Off Geofence");
        }else
        {
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
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
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
            Log.i(TAG, "Requesting permission");
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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER


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
        getApplicationContext().registerReceiver(new GPScheck(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }


    //



}

