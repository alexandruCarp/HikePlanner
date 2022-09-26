package com.example.hike_planner_v2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<Intersectie>interlist;
    ArrayList<Intersectie>interlistRelevante;
    ArrayList<Drum>drumlist;
    HashMap<String,Integer>indexinter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navi= findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(navi, navController);
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(R.id.hartaFragment,R.id.planificatorFragment,R.id.obiectiveFragment).build();
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfig);

        interlist=new ArrayList<>();
        interlistRelevante=new ArrayList<>();
        indexinter=new HashMap<>();
        interlist=citireIntersectii();
        drumlist=citireDrumuri();
    }
    public ArrayList<Intersectie> getLista()
    {
        return interlist;
    }
    public ArrayList<Intersectie> getInterlistRelevante() {return interlistRelevante;}
    public ArrayList<Drum> getDrumlist(){return drumlist;}
    public void seteazaTitluActionBar(String titlu)
    {
        getSupportActionBar().setTitle(titlu);
    }
    public ArrayList<Intersectie> citireIntersectii()
    {
        ArrayList<Intersectie>lista=new ArrayList<>();
        String linie;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            inputStreamReader=new InputStreamReader(getAssets().open("intersectii_data.csv"));
            bufferedReader=new BufferedReader(inputStreamReader);
            int ind=0;
            while((linie=bufferedReader.readLine())!=null)
            {
                String[] data=linie.split(",");
                ArrayList<Integer> listaImg=new ArrayList<>();
                for(int i=1;i<=Integer.parseInt(data[9]);i++)
                {
                    String numefisier=data[8]+i;
                    listaImg.add(getResources().getIdentifier(numefisier,"drawable",getPackageName()));
                }
                Intersectie inte= new Intersectie(data[0],data[1],Integer.parseInt(data[2]),Boolean.parseBoolean(data[3]),Boolean.parseBoolean(data[4]),Boolean.parseBoolean(data[5]),Boolean.parseBoolean(data[6]), Boolean.parseBoolean(data[7]),getResources().getIdentifier(data[8]+"_mica","drawable",getPackageName()),listaImg,ind);
                lista.add(inte);
                if(Boolean.parseBoolean(data[10]))
                    interlistRelevante.add(inte);
                indexinter.put(inte.nume.toLowerCase(),inte.index);
                ind++;
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public ArrayList<Drum> citireDrumuri()
    {
        ArrayList<Drum>lista=new ArrayList<>();
        String linie;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            inputStreamReader=new InputStreamReader(getAssets().open("drumuri_data.csv"));
            bufferedReader=new BufferedReader(inputStreamReader);
            int ind=0;
            while((linie=bufferedReader.readLine())!=null)
            {
                String[] data=linie.split(",");
                int altjos=interlist.get(indexinter.get(data[0].toLowerCase())).altitudine;
                int altsus=interlist.get(indexinter.get(data[1].toLowerCase())).altitudine;
                if(altsus<altjos)
                {
                    String aux=data[0];
                    data[0]=data[1];
                    data[1]=aux;
                }
                Drum drum=new Drum(indexinter.get(data[0].toLowerCase()),indexinter.get(data[1].toLowerCase()),Integer.parseInt(data[2]),Double.parseDouble(data[3]),Double.parseDouble(data[4]),data[5],Boolean.parseBoolean(data[6]),ind);
                ind++;
                drum.setDifnivel(interlist.get(drum.intjos).altitudine,interlist.get(drum.intsus).altitudine);
                lista.add(drum);
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}