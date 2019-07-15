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




    public void setName(String name)
    {
        editor.putString("nameofusernamegeo",name);
        editor.commit();
        editor.apply();
    }
    public String getName()
    {
        return  pref.getString("nameofusernamegeo","0");

    }



    public void setVersionName(String version)
    {
        editor.putString("versionname",version);
        editor.commit();
        editor.apply();
    }
    public String getVersionName()
    {
        return  pref.getString("versionname","");

    }

    public void setVersionCode(String version)
    {
        editor.putString("versioncode",version);
        editor.commit();
        editor.apply();
    }
    public String getVersionCode()
    {
        return  pref.getString("versioncode","");

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


    public String getuserevent()
    {
        return  pref.getString("userevent","exit");
    }

    public void setuserevent(String event)
    {
        editor.putString("userevent",event);
        editor.commit();
        editor.apply();
    }


    public String getuserecno()
    {
        return  pref.getString("ecno","");
    }

    public void setuserecno(String event)
    {
        editor.putString("ecno",event);
        editor.commit();
        editor.apply();
    }



    public String getuserlocation()
    {
        return  pref.getString("userlocation","");
    }

    public void setuserlocation(String event)
    {
        editor.putString("userlocation",event);
        editor.commit();
        editor.apply();
    }


    //

    public String getuserlastpunchinpunchoutdate()
    {
        return  pref.getString("userlastpunchinoutdate","");
    }

    public void setuserlastpunchinpunchoutdate(String date)
    {
        editor.putString("userlastpunchinoutdate",date);
        editor.commit();
        editor.apply();
    }



    public String getuserselectedlatitude()
    {
        return  pref.getString("userselectedlatitude","");
    }

    public void setuserselectedlatitude(String latitude)
    {
        editor.putString("userselectedlatitude",latitude);
        editor.commit();
        editor.apply();
    }


    public String getuserselectedlongitude()
    {
        return  pref.getString("userselectedlongitude","");
    }

    public void setuserselectedlongitude(String longitude)
    {
        editor.putString("userselectedlongitude",longitude);
        editor.commit();
        editor.apply();
    }

    public String getuserselectedradius()
    {
        return  pref.getString("userselectedradius","");
    }

    public void setuserselectedradius(String radius)
    {
        editor.putString("userselectedradius",radius);
        editor.commit();
        editor.apply();
    }


}
