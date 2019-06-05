package com.google.android.gms.location.sample.geofencing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.sample.geofencing.GetterSetter.GeoFenceArraylist;

import java.util.ArrayList;



public class RecyclerViewAdapterclass extends RecyclerView.Adapter<RecyclerViewAdapterclass.ViewHolder> {


    ArrayList<GeoFenceArraylist> arraylists = new ArrayList<>();
    LayoutInflater layoutInflater;
    Context context;

    public void setData(ArrayList<GeoFenceArraylist> arraylists)
    {
        this.arraylists = arraylists;
        notifyItemRangeChanged(0,arraylists.size());
    }

    public RecyclerViewAdapterclass(Context context)
    {
        this.context =context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerviewlaoyout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText("Name : "+arraylists.get(position).getName());
        holder.time.setText("Time : "+arraylists.get(position).getTime());
        holder.event.setText("Event : "+arraylists.get(position).getEvent());
        holder.provider.setText("Provider : "+arraylists.get(position).getProvider());

    }

    @Override
    public int getItemCount() {
        return arraylists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView name,time,event,provider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            event = itemView.findViewById(R.id.event);
            provider = itemView.findViewById(R.id.provider);

        }
    }

}
