package com.minicap.collarapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;

public class DogListAdapter extends RecyclerView.Adapter<DogListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Dog> dogs = new ArrayList();

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.dogName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int selected = getAdapterPosition();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("dogID", dogs.get(selected).getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    public DogListAdapter(Context context, ArrayList<Dog> dogs) {
        this.context = context;
        this.dogs = dogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        holder.name.setText(dogs.get(index).getName());
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }
}
