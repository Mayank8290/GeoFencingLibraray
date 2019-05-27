package com.google.android.gms.location.sample.geofencing;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.sample.geofencing.LocalData.LocalData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayPrevousTriggers extends DialogFragment {

    RecyclerView recyclerviewfence;
    ArrayList<GeoFenceArraylist> arraylists = new ArrayList<>();
    RecyclerViewAdapterclass adapetr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return inflater.inflate(R.layout.displayprevoustrigger,container,false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerviewfence = (RecyclerView)view.findViewById(R.id.recyclerviewfence);
        recyclerviewfence.setLayoutManager(new LinearLayoutManager(getActivity()));

        setData();

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

            adapetr.setData(arraylists);
            adapetr.notifyDataSetChanged();
            recyclerviewfence.invalidate();

        }


    }
}
