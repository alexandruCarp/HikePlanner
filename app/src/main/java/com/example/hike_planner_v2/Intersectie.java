package com.example.hike_planner_v2;

import java.io.Serializable;
import java.util.ArrayList;

public class Intersectie implements Serializable {
    String nume;
    String tip;
    int altitudine;
    boolean accesibil_auto;
    boolean accesibil_tren;
    boolean accesibil_telecabina;
    boolean arerestaurant;
    boolean arecazare;
    int idimagine_mica;
    ArrayList<Integer>listaImaginiDesc;
    int index;
    public Intersectie(String nume, String tip, int altitudine, boolean accesibil_tren, boolean accesibil_auto, boolean accesibil_telecabina, boolean arerestaurant, boolean arecazare,int id_img_mica,ArrayList<Integer>listaImaginiDesc,int index)
    {
        this.nume=nume;
        this.tip=tip;
        this.altitudine=altitudine;
        this.accesibil_tren=accesibil_tren;
        this.accesibil_auto=accesibil_auto;
        this.accesibil_telecabina=accesibil_telecabina;
        this.arerestaurant=arerestaurant;
        this.arecazare=arecazare;
        this.idimagine_mica=id_img_mica;
        this.listaImaginiDesc=listaImaginiDesc;
        this.index=index;
    }

}
