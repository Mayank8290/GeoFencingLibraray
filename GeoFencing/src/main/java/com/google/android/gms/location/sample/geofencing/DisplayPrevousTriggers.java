package com.google.android.gms.location.sample.geofencing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.sample.geofencing.GetterSetter.GeoFenceArraylist;
import com.google.android.gms.location.sample.geofencing.GetterSetter.HistoryData;
import com.google.android.gms.location.sample.geofencing.GetterSetter.LocationDataGetterSetter;
import com.google.android.gms.location.sample.geofencing.LocalData.LocalData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DisplayPrevousTriggers extends DialogFragment {

    RecyclerView recyclerviewfence;
    ArrayList<GeoFenceArraylist> arraylists = new ArrayList<>();
    RecyclerViewAdapterclass adapetr;

    ArrayList<HistoryData> historydata = new ArrayList<>();

    RelativeLayout opendatepicker;

    TextView Tdate;

    pl.droidsonroids.gif.GifImageView loader;

    String selecteddate;

    TextView textfornodata;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //return inflater.inflate(R.layout.displayprevoustrigger,container,false);

        View view = inflater.inflate(R.layout.displayprevoustrigger, container, false);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;



    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerviewfence = (RecyclerView)view.findViewById(R.id.recyclerviewfence);

        Tdate = (TextView)view.findViewById(R.id.date);

        loader = (pl.droidsonroids.gif.GifImageView)view.findViewById(R.id.loader);

        textfornodata = (TextView)view.findViewById(R.id.textfornodata);

        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Tdate.setText(sdf.format(myCalendar.getTime()));

                selecteddate = sdf.format(myCalendar.getTime());


                    getHistoryData(selecteddate);


//                updateLabel();
            }

        };

        opendatepicker = (RelativeLayout)view.findViewById(R.id.opendatepicker);
        opendatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        recyclerviewfence.setLayoutManager(new LinearLayoutManager(getActivity()));

        //setData();


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);


            getHistoryData(formattedDate);


    }

    public void setData()
    {
        adapetr = new RecyclerViewAdapterclass(getActivity());
        recyclerviewfence.setAdapter(adapetr);
        Gson gson = new Gson();
        if(new LocalData(getActivity()).getDime().equals("0"))
        {
            Toast.makeText(getActivity(),"No data to display",Toast.LENGTH_LONG).show();
            dismiss();
        }
        else
        {
            String json = new LocalData(getActivity()).getDime();
            Type type = new TypeToken<ArrayList<GeoFenceArraylist>>() {}.getType();
            arraylists =  gson.fromJson(json, type);

            //adapetr.setData(arraylists);
            adapetr.notifyDataSetChanged();
            recyclerviewfence.invalidate();

        }


    }

    public void getHistoryData(String date) {
        historydata.clear();


// To dismiss the dialog

        loader.setVisibility(View.VISIBLE);

        String url = ServerUrl.getHistory;
        HashMap<String, String> params = new HashMap<>();
//        params.put("ec_no",new LocalData(getActivity()).getuserecno());
        params.put("ec_no","100000");
        params.put("date",date);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.wtf("historyresponse", response.toString());

                loader.setVisibility(View.GONE);

                if(response.optString("success").equals("1"))
                {
                    recyclerviewfence.setVisibility(View.VISIBLE);
                    textfornodata.setVisibility(View.GONE);

                    JSONArray jsonArray = response.optJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        HistoryData data = new HistoryData();

                        data.setEvent("");
                        if(jsonObject.optString("in_coordinates").equals("") && jsonObject.optString("out_coordinates").length() > 0)
                        {
                            data.setEvent("Exited");
                            data.setTime(jsonObject.optString("out_punch"));
                            data.setLocation(jsonObject.optString("out_location"));
                        }
                        else if(jsonObject.optString("out_coordinates").equals("") && jsonObject.optString("in_coordinates").length() > 0)
                        {
                            data.setEvent("Entered");
                            data.setTime(jsonObject.optString("in_punch"));
                            data.setLocation(jsonObject.optString("in_location"));
                        }



                        data.setIn_location(jsonObject.optString("in_location"));
                        data.setOut_location(jsonObject.optString("out_location"));
                        data.setIn_coordinates(jsonObject.optString("in_coordinates"));
                        data.setOut_coordinates(jsonObject.optString("out_coordinates"));
                        data.setIn_punch(jsonObject.optString("in_punch"));
                        data.setOut_punch(jsonObject.optString("out_punch"));

                        historydata.add(data);

                        adapetr.setData(historydata);
                        adapetr.notifyDataSetChanged();
                        recyclerviewfence.invalidate();

                    }
                }
                else
                {
                    recyclerviewfence.setVisibility(View.GONE);
                    textfornodata.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"No data found for selected date",Toast.LENGTH_LONG).show();
                }


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
        adapetr = new RecyclerViewAdapterclass(getActivity());
        recyclerviewfence.setAdapter(adapetr);

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        requestQueue.add(jsonObjectRequest);




        }


}
