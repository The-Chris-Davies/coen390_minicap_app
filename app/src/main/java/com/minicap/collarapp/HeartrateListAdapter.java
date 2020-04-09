package com.minicap.collarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.GraphView;

import java.text.DateFormat;
import java.util.ArrayList;

public class HeartrateListAdapter extends RecyclerView.Adapter<HeartrateListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Heartrate> heartrates;
    private GraphView graph;
    private int selected;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView heartrate;
        private TextView timestamp;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            heartrate = itemView.findViewById(R.id.value);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    notifyItemChanged(selected);
                    selected = getAdapterPosition();
                    notifyItemChanged(selected);
                    //TODO: update the graph to show the selected value
                    //what the hell does this do
                    //graph.scrollTo((int) heartrates.get(selected).getTimestamp().toDate().getTime(), heartrates.get(selected).getValue().intValue());
                }
            });
        }
    }

    public HeartrateListAdapter(Context context, ArrayList<Heartrate> heartrates, GraphView graph) {
        this.context = context;
        this.heartrates = heartrates;
        this.graph = graph;
        selected = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        Heartrate currentHr = heartrates.get(index);
        holder.heartrate.setText(currentHr.getValue() + " BPM");
        holder.timestamp.setText(DateFormat.getTimeInstance().format(currentHr.getTimestamp().toDate())
                        + "\n" + DateFormat.getDateInstance().format(currentHr.getTimestamp().toDate())
        );
        holder.itemView.setBackground(selected == index ? ContextCompat.getDrawable(context , R.drawable.list_entry_border_selected) : ContextCompat.getDrawable(context , R.drawable.list_entry_border));
    }

    @Override
    public int getItemCount() {
        return heartrates.size();
    }
}
