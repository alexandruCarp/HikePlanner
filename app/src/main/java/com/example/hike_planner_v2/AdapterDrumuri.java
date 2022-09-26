package com.example.hike_planner_v2;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.TreeSet;

public class AdapterDrumuri extends RecyclerView.Adapter<AdapterDrumuri.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Traseu> data;
    private List<Intersectie>intersectieList;
    Context context;
    AdapterDrumuri(Context context, List<Traseu> data, List<Intersectie> intersectieList){
        this.layoutInflater=LayoutInflater.from(context);
        this.data=data;
        this.context=context;
        this.intersectieList=intersectieList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.recycler_drumuri,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Traseu traseuCurent=data.get(position);
        holder.textMare.setText(traseuCurent.textTraseu);
        holder.textViewDurata.setText(traseuCurent.durata+ " h");
        double x= (double) traseuCurent.distanta /10;
        holder.textViewDistanta.setText(x+" km");
        holder.textViewAltPoz.setText(traseuCurent.difNivelPozitiva+" m");
        holder.textViewAltNeg.setText(traseuCurent.difNivelNegativa+" m");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textMare;
        TextView textViewDurata;
        TextView textViewDistanta;
        TextView textViewAltPoz;
        TextView textViewAltNeg;
        Button butonSpreHarta,butonAfisareDet;
        ImageView imageView;
        ConstraintLayout layout;
        TextView[] textViewnou;
        ImageView[] marcaje;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMare=itemView.findViewById(R.id.text_traseu);
            textViewDurata=itemView.findViewById(R.id.textTimp);
            textViewDistanta=itemView.findViewById(R.id.textDistanta);
            textViewAltPoz=itemView.findViewById(R.id.textAltitudineUrcata);
            textViewAltNeg=itemView.findViewById(R.id.textAltitudineCoborata);
            imageView=itemView.findViewById(R.id.imageView);
            butonSpreHarta=itemView.findViewById(R.id.buttonTraseuSpreHarta);
            butonAfisareDet=itemView.findViewById(R.id.buttonAfisareDetaliata);
            layout=itemView.findViewById(R.id.cLayoutTraseu);
            butonSpreHarta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] indecsiDrumuri=new int[50];
                    Traseu traseu=data.get(getAdapterPosition());
                    for(int i=0;i<traseu.drumuriComponente.size();i++)
                        indecsiDrumuri[i]=traseu.drumuriComponente.get(i).first.index;
                    planificatorFragmentDirections.ActionPlanificatorFragmentToHartaFragment action = planificatorFragmentDirections.actionPlanificatorFragmentToHartaFragment(-traseu.drumuriComponente.size(),indecsiDrumuri);
                    Navigation.findNavController((AppCompatActivity)context,R.id.fragment).navigate(action);
                }
            });
            butonAfisareDet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textMare.getVisibility() == View.VISIBLE) {
                        textViewnou= new TextView[50];
                        marcaje= new ImageView[50];
                        Traseu traseu = data.get(getAdapterPosition());
                        for (int i = 0; i < traseu.drumuriComponente.size(); i++) {
                            textViewnou[i] = new TextView(context);
                            marcaje[i] = new ImageView(context);
                            Drum drum1 = traseu.drumuriComponente.get(i).first;
                            String text1;
                            if (traseu.drumuriComponente.get(i).second)
                                text1 = intersectieList.get(drum1.intjos).nume + " - " + intersectieList.get(drum1.intsus).nume + " " + drum1.timpMare+"h";
                            else
                                text1 = intersectieList.get(drum1.intsus).nume + " - " + intersectieList.get(drum1.intjos).nume + " "+ drum1.timpMic+"h";
                            textViewnou[i].setText(text1);
                            textViewnou[i].setId(View.generateViewId());
                            marcaje[i].setId(View.generateViewId());
                            String numeDr="ic_"+drum1.marcaj;
                            int id=context.getResources().getIdentifier(numeDr,"drawable",context.getPackageName());
                            marcaje[i].setImageResource(id);
                            ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (i > 0)
                            {params1.topToBottom = textViewnou[i - 1].getId();
                            params1.topMargin=10;}
                            else {params1.topToBottom = R.id.text_traseu;
                            params1.topMargin = 20;}
                            params1.startToStart= ConstraintLayout.LayoutParams.PARENT_ID;
                            params1.leftMargin=5;
                            layout.addView(marcaje[i], params1);
                            textViewnou[i].setPadding(5,5,25,5);
                            ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            params2.topToTop=marcaje[i].getId();
                            params2.bottomToBottom=marcaje[i].getId();
                            params2.startToEnd=marcaje[i].getId();
                            params2.leftMargin=5;

                            layout.addView(textViewnou[i],params2);
                        }
                        textMare.setVisibility(View.GONE);
                        ConstraintLayout.LayoutParams params= (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                        params.topToBottom = textViewnou[traseu.drumuriComponente.size() - 1].getId();
                        imageView.setLayoutParams(params);
                    }
                    else
                    {
                        textMare.setVisibility(View.VISIBLE);
                        for(int i=0;i<data.get(getAdapterPosition()).drumuriComponente.size(); i++) {
                            textViewnou[i].setVisibility(View.GONE);
                            marcaje[i].setVisibility(View.GONE);
                        }
                        ConstraintLayout.LayoutParams params= (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                        params.topToBottom = textMare.getId();
                        imageView.setLayoutParams(params);
                    }
                }
            });

        }
    }
}
