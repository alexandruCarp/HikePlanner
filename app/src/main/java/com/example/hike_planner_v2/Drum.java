package com.example.hike_planner_v2;

public class Drum {
    int intjos, intsus,distanta;
    boolean deIarna;
    double timpMare,timpMic;
    int difnivel;
    String marcaj;
    int index;
    public Drum(int start, int finish,int distanta, double timpMare,double timpMic,String marcaj,boolean deIarna,int index) {
        this.intjos = start;
        this.intsus = finish;
        this.distanta = distanta;
        this.timpMare = timpMare;
        this.timpMic = timpMic;
        this.marcaj=marcaj;
        this.deIarna=deIarna;
        this.index=index;
    }

    public void setDifnivel(int altstart, int altfinish) {
        this.difnivel = altfinish-altstart;
    }
}
