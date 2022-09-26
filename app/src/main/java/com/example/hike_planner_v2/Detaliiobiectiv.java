package com.example.hike_planner_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Detaliiobiectiv extends AppCompatActivity {
    LinearLayout linearLayout;
    Intersectie obiectiv;
    String descriere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaliiobiectiv);
        Intent intent=getIntent();
        int index_ob=intent.getIntExtra("obiectivindex",-1);
        obiectiv=citireIntersectie(index_ob);
        linearLayout= findViewById(R.id.SliderDots);
        ArrayList<Integer>lista_imagini= obiectiv.listaImaginiDesc;
        ViewPager2 viewPager=findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter=new PagerAdapter(this,lista_imagini);
        viewPager.setAdapter(pagerAdapter);
        final int nrpuncte = pagerAdapter.getItemCount();
        final ImageView[] puncte = new ImageView[nrpuncte];
        for(int i=0;i<nrpuncte;i++)
        {
            puncte[i]=new ImageView(this);
            puncte[i].setImageResource(R.drawable.punctulet_inactiv);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            linearLayout.addView(puncte[i],params);
        }
        puncte[0].setImageResource(R.drawable.punctulet_activ);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<nrpuncte;i++)
                    puncte[i].setImageResource(R.drawable.punctulet_inactiv);
                puncte[position].setImageResource(R.drawable.punctulet_activ);
            }
        });
        TextView titlu=findViewById(R.id.titlu);
        TextView descr=findViewById(R.id.descriere);
        if (obiectiv != null) {
            titlu.setText(obiectiv.nume);
        }
        if (obiectiv != null) {
            getSupportActionBar().setTitle(obiectiv.nume);
        }
        descr.setText(descriere);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public Intersectie citireIntersectie(int index)
    {
        String linie= "";
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            inputStreamReader=new InputStreamReader(getAssets().open("intersectii_data.csv"));
            bufferedReader=new BufferedReader(inputStreamReader);
            for(int i=0;i<=index;i++)
                linie=bufferedReader.readLine();

                String[] data=linie.split(",");
                ArrayList<Integer> listaImg=new ArrayList<>();
                for(int i=1;i<=Integer.parseInt(data[9]);i++)
                {
                    String numefisier=data[8]+i;
                    listaImg.add(getResources().getIdentifier(numefisier,"drawable",getPackageName()));
                }
            bufferedReader.close();
            citireDescriere(data[8]+"_descriere.txt");
                return new Intersectie(data[0],data[1],Integer.parseInt(data[2]),Boolean.parseBoolean(data[3]),Boolean.parseBoolean(data[4]),Boolean.parseBoolean(data[5]),Boolean.parseBoolean(data[6]), Boolean.parseBoolean(data[7]),getResources().getIdentifier(data[8]+"_mica","drawable",getPackageName()),listaImg,index);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void citireDescriere(String numeFisier)
    {
        String linie;
        descriere="";
        try {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(getAssets().open(numeFisier)));
            while((linie=bufferedReader.readLine())!=null)
            {
                descriere=descriere+linie;
                descriere=descriere+System.lineSeparator();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"EROARE >>>>>>",Toast.LENGTH_LONG).show();
        }
    }
}