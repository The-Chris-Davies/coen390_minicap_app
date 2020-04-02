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

import java.text.DateFormat;
import java.util.ArrayList;

public class PositionListAdapter extends RecyclerView.Adapter<PositionListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Position> positions;
    private int selected;
    private GoogleMap gMap;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView position;
        private TextView timestamp;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            position = itemView.findViewById(R.id.value);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    notifyItemChanged(selected);
                    selected = getAdapterPosition();
                    notifyItemChanged(selected);
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(positions.get(selected).getValue().getLatitude(),
                                    positions.get(selected).getValue().getLongitude())));
                }
            });
        }
    }

    public PositionListAdapter(Context context, ArrayList<Position> positions, GoogleMap gMap) {
        this.context = context;
        this.positions = positions;
        this.gMap = gMap;
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
        Position currentPos = positions.get(index);
        holder.position.setText(
            "Lat: " + currentPos.getValue().getLatitude() +
            "\nLon: " + currentPos.getValue().getLongitude());
        holder.timestamp.setText(
            DateFormat.getTimeInstance().format(currentPos.getTimestamp().toDate())
            + "\n" + DateFormat.getDateInstance().format(currentPos.getTimestamp().toDate())
        );
        holder.itemView.setBackground(selected == index ? ContextCompat.getDrawable(context , R.drawable.list_entry_border_selected) : ContextCompat.getDrawable(context , R.drawable.list_entry_border));
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }
}
