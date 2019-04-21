package com.example.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by kuldeep on 13/02/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PojoClass> pojoClassArrayList;
    PojoClassCallback pojoClassCallback;

    public ListAdapter(Context context, ArrayList<PojoClass> pojoClassArrayList,PojoClassCallback pojoClassCallback) {
        this.context = context;
        this.pojoClassArrayList = pojoClassArrayList;
        this.pojoClassCallback=pojoClassCallback;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {

        holder.song_name.setText(pojoClassArrayList.get(position).getSong_name());
        holder.singer_name.setText(pojoClassArrayList.get(position).getSinger_name());
        int duration = Integer.valueOf(pojoClassArrayList.get(position).getTime());
        String durition = getMinSecFromMiliseconds(duration);

        holder.time.setText(durition);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pojoClassCallback.getSong(position);
                //Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getMinSecFromMiliseconds(int duration) {
        int secs = duration / 1000;
        int min = secs / 60;
        int sec = secs % 60;
        String min1, sec1;
        if (min < 10) {
            min1 = "0" + String.valueOf(min);
        } else {
            min1 = String.valueOf(min);
        }
        if (sec < 10) {
            sec1 = "0" + String.valueOf(sec);
        } else {
            sec1 = String.valueOf(sec);
        }
        return min1 + ":" + sec1;
    }

    @Override
    public int getItemCount() {
        return pojoClassArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView song_name, singer_name, time;
        LinearLayout ll;

        public ViewHolder(View view) {
            super(view);
            ll = view.findViewById(R.id.LL);
            song_name = view.findViewById(R.id.song_name);
            singer_name = view.findViewById(R.id.singer_name);
            time = view.findViewById(R.id.time);

        }

    }
}
