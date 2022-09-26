package com.example.hike_planner_v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    private List<Integer>data;
    PagerAdapter(Context context,List<Integer>data)
    {
        this.layoutInflater=LayoutInflater.from(context);
        this.data=data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.galerie_poze_obiectiv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(data!=null)
            holder.imagine.setImageResource(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imagine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagine=itemView.findViewById(R.id.imaginegalerie);
        }
    }
}
