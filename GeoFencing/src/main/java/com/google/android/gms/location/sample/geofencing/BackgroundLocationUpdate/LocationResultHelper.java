/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.geofencing.BackgroundLocationUpdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.sample.geofencing.LocalData.LocalData;
import com.google.android.gms.location.sample.geofencing.NotificationReceiverActivity;
import com.google.android.gms.location.sample.geofencing.R;
import com.google.android.gms.location.sample.geofencing.ServerUrl;
import com.google.android.gms.maps.model.Circle;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

/**
 * Class to process location results.
 */
class LocationResultHelper {

    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";

    final private static String PRIMARY_CHANNEL = "default";


    private Context mContext;
    private List<Location> mLocations;
    private NotificationManager mNotificationManager;

    LocationResultHelper(Context context, List<Location> locations) {
        mContext = context;
        mLocations = locations;

        NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,
                context.getString(R.string.default_channel), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(channel);
    }

    /**
     * Returns the title for reporting about a list of {@link Location} objects.
     */
    private String getLocationResultTitle() {
        String numLocationsReported = mContext.getResources().getQuantityString(
                R.plurals.num_locations_reported, mLocations.size(), mLocations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }

    private String getLocationResultText() {
        if (mLocations.isEmpty()) {
            return mContext.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : mLocations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Saves location result as a string to {@link android.content.SharedPreferences}.
     */
    void saveResults() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle() + "\n" +
                        getLocationResultText())
                .apply();
    }

    /**
     * Fetches location results from {@link android.content.SharedPreferences}.
     */
    static String getSavedLocationResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    /**
     * Get the notification mNotificationManager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * Displays a notification with the location results.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    void showNotification() {


        // costumize the notification according to the user selected location
        String userselectedgeolocation = new LocalData(mContext).getuserselctedlocation();
        Boolean isinside = getthedifference();
        String notificationtitle= "";

        if(userselectedgeolocation.equals("HO , The Grand Hotel"))
        {
           if(isinside)
           {
               notificationtitle = "Entered : HO , The Grand Hotel";

           }
           else
           {
               notificationtitle = "Exited : HO , The Grand Hotel";

           }
        }
        else if(userselectedgeolocation.equals("HMCI , Sohna Road"))
        {
            if(isinside)
            {
                notificationtitle = "Entered : HMCI , Sohna Road";

            }
            else
            {
                notificationtitle = "Exited : HMCI , Sohna Road";

            }
        }
        else if(userselectedgeolocation.equals("HMCO"))
        {
            if(isinside)
            {
                notificationtitle = "Entered : HMCO";

            }
            else
            {
                notificationtitle = "Exited : HMCO";

            }
        }

        //

        if(isinside==true && new LocalData(mContext).getuserevent().equals("exit"))
        {
            Intent notificationIntent = new Intent(mContext, NotificationReceiverActivity.class);

            // Construct a task stack.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

            // Add the main Activity to the task stack as the parent.
            stackBuilder.addParentStack(NotificationReceiverActivity.class);

            // Push the content Intent onto the stack.
            stackBuilder.addNextIntent(notificationIntent);

            // Get a PendingIntent containing the entire back stack.
            PendingIntent notificationPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder = new Notification.Builder(mContext,
                    PRIMARY_CHANNEL)
                    .setContentTitle(notificationtitle)
                    .setContentText(mContext.getString(R.string.geofence_transition_notification_text))
                    .setSmallIcon(R.drawable.geofencing)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.geofencing))
                    .setAutoCancel(true)
                    .setContentIntent(notificationPendingIntent);

            getNotificationManager().notify(0, notificationBuilder.build());


            getthedifference();

            new LocalData(mContext).setuserevent("enter");

        }

        if(!isinside && new LocalData(mContext).getuserevent().equals("enter"))
        {
            Intent notificationIntent = new Intent(mContext, NotificationReceiverActivity.class);

            // Construct a task stack.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

            // Add the main Activity to the task stack as the parent.
            stackBuilder.addParentStack(NotificationReceiverActivity.class);

            // Push the content Intent onto the stack.
            stackBuilder.addNextIntent(notificationIntent);

            // Get a PendingIntent containing the entire back stack.
            PendingIntent notificationPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder = new Notification.Builder(mContext,
                    PRIMARY_CHANNEL)
                    .setContentTitle(notificationtitle)
                    .setContentText(mContext.getString(R.string.geofence_transition_notification_text))
                    .setSmallIcon(R.drawable.geofencing)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.geofencing))
                    .setAutoCancel(true)
                    .setContentIntent(notificationPendingIntent);

            getNotificationManager().notify(0, notificationBuilder.build());


            getthedifference();

            new LocalData(mContext).setuserevent("exit");

        }



    }

    public boolean getthedifference()
    {

         Boolean isinside = false;

       for(int i=0;i<mLocations.size();i++)
       {
          Double Lati =  mLocations.get(i).getLatitude();
          Double Longi = mLocations.get(i).getLongitude();

          if(checkInside(Longi,Lati))
          {
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                      //Toast.makeText(mContext,"Inside the Geo Fence",Toast.LENGTH_SHORT).show();



                  }
              });

              isinside = true;
          }
          else
          {
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                      //Toast.makeText(mContext,"Outside",Toast.LENGTH_SHORT).show();


                  }
              });
              isinside = false;
          }

       }

       if(isinside)
       {
           if(new LocalData(mContext).getpunchinandpounchout().equals("punchin"))
           {
               senddatatoserver("Entered",String.valueOf(mLocations.get(mLocations.size()-1).getLatitude()),String.valueOf(mLocations.get(mLocations.size()-1).getLongitude()));
           }
       }
       else
       {
           if(new LocalData(mContext).getpunchinandpounchout().equals("punchout")) {
               senddatatoserver("Exited", String.valueOf(mLocations.get(mLocations.size()-1).getLatitude()), String.valueOf(mLocations.get(mLocations.size()-1).getLongitude()));
           }
       }

      return isinside;
    }

    boolean checkInside(double longitude, double latitude) {
        return calculateDistance(
                77.0433149267526, 28.412402603746372, longitude, latitude
        ) < 50;
    }

    double calculateDistance(
            double longitude1, double latitude1,
            double longitude2, double latitude2) {
        double c =
                Math.sin(Math.toRadians(latitude1)) *
                        Math.sin(Math.toRadians(latitude2)) +
                        Math.cos(Math.toRadians(latitude1)) *
                                Math.cos(Math.toRadians(latitude2)) *
                                Math.cos(Math.toRadians(longitude2) -
                                        Math.toRadians(longitude1));
        c = c > 0 ? Math.min(1, c) : Math.max(-1, c);
        return 3959 * 1.609 * 1000 * Math.acos(c);
    }


    // sending data to server


    public void senddatatoserver(String event , String latitude , String longitude)
    {

        Log.wtf("SendingData","Called");

        String url = ServerUrl.sendData;
        HashMap<String,String> params = new HashMap<>();
        if(event.equals("Entered"))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            params.put("in_punch", currentDateandTime);
            params.put("out_punch","");
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            params.put("in_punch","");
            params.put("out_punch",currentDateandTime);
        }

        params.put("ec_no","10046");
        params.put("location",new LocalData(mContext).getuserselctedlocation());
        params.put("status","MG");
        params.put("coordinates","Latitude : "+latitude+" , Longitude : "+longitude);


        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.getCache().clear();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.wtf("geoattendecneresponse",response.toString());

                if(response.optString("msg").equals("Success"))
                {
                    //Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();



                    if(event.equals("Entered"))
                    {
                        new LocalData(mContext).setpunchinandpounchout("punchout");

                    }

                }
                else if(response.optString("msg").equals("No update"))
                {

                }

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error",error.toString());
            }
        })
        {
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }


    //


}
