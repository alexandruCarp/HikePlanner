package com.example.hike_planner_v2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {


    private LayoutInflater layoutInflater;
    private List<Intersectie> data;
    private List<Intersectie> datafull;
    private Context context;
    Adapter(Context context, List<Intersectie> data){
        this.layoutInflater=LayoutInflater.from(context);
        this.data=data;
        datafull= new ArrayList<>(data);
        this.context=context;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.custom_view_recycler,parent,false);
        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Intersectie icurent=data.get(position);
        String titlu=icurent.nume;
        String altitudinetext=String.valueOf(icurent.altitudine);
        holder.titluview.setText(titlu);
        holder.altitudineview.setText("alt." + altitudinetext);
        holder.pozaview.setImageResource(icurent.idimagine_mica);
        if(icurent.accesibil_auto)
            holder.masinaview.setImageResource(R.drawable.ic_masina_verde);
        else holder.masinaview.setImageResource(R.drawable.ic_masina_rosie);
        if(icurent.arerestaurant)
            holder.restaview.setImageResource(R.drawable.ic_restaurant_verde);
        else holder.restaview.setImageResource(R.drawable.ic_restaurant_rosu);
        if(icurent.arecazare)
            holder.hotelview.setImageResource(R.drawable.ic_hotel_verde);
        else holder.hotelview.setImageResource(R.drawable.ic_hotel_rosu);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titluview;
        TextView altitudineview;
        ImageView masinaview;
        ImageView restaview;
        ImageView hotelview;
        ImageView pozaview;
        Button butonstart,butondest,butonharta;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titluview=itemView.findViewById(R.id.titluob);
            altitudineview=itemView.findViewById(R.id.altitudineob);
            masinaview=itemView.findViewById(R.id.imaginemasina);
            restaview=itemView.findViewById(R.id.imaginerestaurant);
            hotelview=itemView.findViewById(R.id.imaginehotel);
            pozaview=itemView.findViewById(R.id.imagineob);
            butondest=itemView.findViewById(R.id.butondest);
            butonstart=itemView.findViewById(R.id.butonstart);
            butonharta=itemView.findViewById(R.id.butonspreharta);
            View.OnClickListener click = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (v.getId()==R.id.butonstart){
                        obiectiveFragmentDirections.ActionObiectiveFragmentToPlanificatorFragment action=obiectiveFragmentDirections.actionObiectiveFragmentToPlanificatorFragment(titluview.getText().toString(),null);
                        Navigation.findNavController((AppCompatActivity)context,R.id.fragment).navigate(action);
                    }
                    else if(v.getId()==R.id.butondest) {
                        obiectiveFragmentDirections.ActionObiectiveFragmentToPlanificatorFragment action=obiectiveFragmentDirections.actionObiectiveFragmentToPlanificatorFragment(null,titluview.getText().toString());
                        Navigation.findNavController((AppCompatActivity)context,R.id.fragment).navigate(action);
                    }
                    else if(v.getId()==R.id.butonspreharta)
                    {
                        obiectiveFragmentDirections.ActionObiectiveFragmentToHartaFragment action=obiectiveFragmentDirections.actionObiectiveFragmentToHartaFragment(data.get(getAdapterPosition()).index,null);
                        Navigation.findNavController((AppCompatActivity)context,R.id.fragment).navigate(action);
                    }
                    else {
                        Intent intent = new Intent(v.getContext(), Detaliiobiectiv.class);
                        intent.putExtra("obiectivindex", data.get(getAdapterPosition()).index);
                        v.getContext().startActivity(intent);
                    }
                }
            };
            butonstart.setOnClickListener(click);
            butondest.setOnClickListener(click);
            butonharta.setOnClickListener(click);
            itemView.setOnClickListener(click);
        }
    }

    @Override
    public Filter getFilter() {
        return filtru;
    }
    private Filter filtru=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Intersectie> datafiltrata=new ArrayList<>();
            if(constraint==null || constraint.length()==0)
                datafiltrata=datafull;
            else
            {
                String cautat=constraint.toString().toLowerCase().trim();
                for(int i=0;i<datafull.size();i++)
                {
                    if(datafull.get(i).nume.toLowerCase().contains(cautat))
                        datafiltrata.add(datafull.get(i));
                }
            }
            FilterResults results=new FilterResults();
            results.values=datafiltrata;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
