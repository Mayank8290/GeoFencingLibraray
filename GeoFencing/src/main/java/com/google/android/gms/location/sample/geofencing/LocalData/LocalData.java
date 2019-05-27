package com.google.android.gms.location.sample.geofencing.LocalData;

import android.content.Context;

/**
 * Created by rakeshgupta on 09/01/18.
 */

public class LocalData {


    android.content.SharedPreferences.Editor editor;
    android.content.SharedPreferences pref;
    public Context context;
    public LocalData(Context context)
    {
        this.context = context;
        pref = context.getSharedPreferences("myPref",0);
        editor=pref.edit();

    }



    public void setpunchinandpounchout(String punchinout)
    {
        editor.putString("punchinout",punchinout);
        editor.commit();
    }
    public String getpunchinandpounchout()
    {
        return  pref.getString("punchinout","punchin");

    }



    public void setDime(String from)
    {
        editor.putString("datapast",from);
        editor.commit();
        editor.apply();
    }
    public String getDime()
    {
        return  pref.getString("datapast","0");

    }



    public void setuserselctedlocation(String location)
    {
        editor.putString("userselectedlocation",location);
        editor.commit();
        editor.apply();
    }


    public String getuserselctedlocation()
    {
        return  pref.getString("userselectedlocation","");
    }




}
