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

public class TemperatureListAdapter extends RecyclerView.Adapter<TemperatureListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Temperature> temperatures;
    private GraphView graph;
    private int selected;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView temperature;
        private TextView timestamp;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            temperature = itemView.findViewById(R.id.value);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    notifyItemChanged(selected);
                    selected = getAdapterPosition();
                    notifyItemChanged(selected);
                    //TODO: update the graph to show the selected value
                    //what the hell does this do
                    //graph.scrollTo((int) temperatures.get(selected).getTimestamp().toDate().getTime(), temperatures.get(selected).getValue().intValue());
                }
            });
        }
    }

    public TemperatureListAdapter(Context context, ArrayList<Temperature> temperatures, GraphView graph) {
        this.context = context;
        this.temperatures = temperatures;
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
        Temperature currentTemp = temperatures.get(index);
        holder.temperature.setText(currentTemp.getValue() + "°C");
        holder.timestamp.setText(DateFormat.getTimeInstance().format(currentTemp.getTimestamp().toDate())
                + "\n" + DateFormat.getDateInstance().format(currentTemp.getTimestamp().toDate())
        );
        holder.itemView.setBackground(selected == index ? ContextCompat.getDrawable(context , R.drawable.list_entry_border_selected) : ContextCompat.getDrawable(context , R.drawable.list_entry_border));
    }

    @Override
    public int getItemCount() {
        return temperatures.size();
    }
}
