package com.example.hike_planner_v2;

import android.util.Pair;

import java.util.ArrayList;

public class Traseu {
    String textTraseu;
    ArrayList<Pair<Drum,Boolean>>drumuriComponente;
    double durata;
    int distanta;
    int difNivelPozitiva,difNivelNegativa;
    public Traseu(String textTraseu,double durata,int distanta,int difNivelPozitiva,int difNivelNegativa,ArrayList<Pair<Drum,Boolean>>drumuriComponente)
    {
        this.textTraseu=textTraseu;
        this.durata=durata;
        this.distanta=distanta;
        this.difNivelPozitiva=difNivelPozitiva;
        this.difNivelNegativa=difNivelNegativa;
        this.drumuriComponente=drumuriComponente;
    }

    public void setDistanta() {
        for(int i=0;i<drumuriComponente.size();i++)
            distanta=distanta+drumuriComponente.get(i).first.distanta;
    }

    public void setDifNivel() {
        for (int i=0;i<drumuriComponente.size();i++) {
            if (drumuriComponente.get(i).second)
                difNivelPozitiva = difNivelPozitiva + drumuriComponente.get(i).first.difnivel;
            else difNivelNegativa=difNivelNegativa + drumuriComponente.get(i).first.difnivel;
        }
    }
}
