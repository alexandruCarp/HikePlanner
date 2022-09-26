package com.example.hike_planner_v2;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Vector;

public class planificatorFragment extends Fragment {

    final int NMAX = 50001;
    final int oo = (1 << 30);
    ConstraintLayout scrollView;
    MenuItem butonSchimbaCautarea;
    TextView acces1,acces2,textfiltruplec,textfiltrudest,textdurata;
    RecyclerView recyclerView;
    ToggleButton toggleSezon;
    Button butoncauta,butonInvers1,butonInvers2;
    AutoCompleteTextView locplecare,locdest;
    CheckBox plecarevar,autop,telep,trenp,restp,cazarep,destvar,autod,teled,trendest,restd,cazaredest;
    ArrayList<Intersectie>listainter;
    ArrayList<String> numeintersectii;
    ArrayList<Drum>drumurilista;
    RangeSlider sliderdurata;
    String numestart,numedest;
    HashMap<String,Integer> A=new HashMap<>();
    int[][] indexulDrumului=new int[100][100];
    ArrayList<Traseu>  trasee = new ArrayList<>();
    String nodstart,nodfinish;
    Double costcurent=0.00;
    float timpmin,timpmax;
    int[]  tata=new int[200];
    double[] D=new double[NMAX];
    boolean[] viz=new boolean[200];
    boolean[] vdfs=new boolean[200];
    boolean eIarna;
    Vector<Integer> drumcurent;
    Vector<Vector<Pair<Integer, Double>>>GVara;
    Vector<Vector<Pair<Integer, Double>>>GIarna;
    Vector<Vector<Pair<Integer, Double>>>G;
    PriorityQueue<Noddij> coada;
    TreeMap<Double,Pair<ArrayList<Pair<Drum,Boolean>>,String> >drumurix= new TreeMap<>();
    public planificatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planificator, container, false);
        plecarevar = view.findViewById(R.id.checkBoxplecare);
        destvar = view.findViewById(R.id.checkBoxdestinatie);
        textfiltruplec = view.findViewById(R.id.textfiltruplecare);
        textfiltrudest = view.findViewById(R.id.textfiltrudestinatie);
        acces1 = view.findViewById(R.id.textacces);
        acces2 = view.findViewById(R.id.acces2);
        autop = view.findViewById(R.id.checkBoxautop);
        telep = view.findViewById(R.id.checkBoxtelep);
        trenp = view.findViewById(R.id.checkBoxtrenp);
        restp = view.findViewById(R.id.checkBoxrestp);
        cazarep = view.findViewById(R.id.checkBoxcazarep);
        autod = view.findViewById(R.id.checkBoxautod);
        teled = view.findViewById(R.id.checkBoxtelecabinad);
        trendest = view.findViewById(R.id.checkBoxtrend);
        restd = view.findViewById(R.id.checkBoxrestd);
        cazaredest = view.findViewById(R.id.checkBoxcazared);
        locplecare = view.findViewById(R.id.introducerestart);
        locdest = view.findViewById(R.id.introducerefinish);
        sliderdurata = view.findViewById(R.id.sliderdur);
        textdurata = view.findViewById(R.id.textdurata);
        butoncauta=view.findViewById(R.id.buttoncauta);
        recyclerView=view.findViewById(R.id.recyclerDrumuri);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        scrollView=view.findViewById(R.id.frameLayout3);
        butonInvers1=view.findViewById(R.id.butonInverseaza1);
        butonInvers2=view.findViewById(R.id.butonInverseaza2);
        toggleSezon=view.findViewById(R.id.toggleButtonsezon);
        planificatorFragmentArgs args = null;
        if (getArguments() != null && !getArguments().isEmpty())
            args = planificatorFragmentArgs.fromBundle(getArguments());
        if (args != null) {
            numestart = args.getStartul();
            numedest = args.getDestinatia();
        }
        if (numestart != null)
            locplecare.setText(numestart);
        if (numedest != null)
            locdest.setText(numedest);
        plecarevar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textfiltruplec.setVisibility(View.VISIBLE);
                    acces1.setVisibility(View.VISIBLE);
                    autop.setVisibility(View.VISIBLE);
                    telep.setVisibility(View.VISIBLE);
                    trenp.setVisibility(View.VISIBLE);
                    restp.setVisibility(View.VISIBLE);
                    cazarep.setVisibility(View.VISIBLE);
                    locplecare.setText("Oriunde");
                    locplecare.setEnabled(false);
                    if(destvar.isChecked())
                        destvar.setChecked(false);
                } else {
                    textfiltruplec.setVisibility(View.GONE);
                    acces1.setVisibility(View.GONE);
                    autop.setVisibility(View.GONE);
                    telep.setVisibility(View.GONE);
                    trenp.setVisibility(View.GONE);
                    restp.setVisibility(View.GONE);
                    cazarep.setVisibility(View.GONE);
                    locplecare.setEnabled(true);
                    locplecare.setText("");
                }
            }
        });
        destvar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textfiltrudest.setVisibility(View.VISIBLE);
                    acces2.setVisibility(View.VISIBLE);
                    autod.setVisibility(View.VISIBLE);
                    teled.setVisibility(View.VISIBLE);
                    trendest.setVisibility(View.VISIBLE);
                    restd.setVisibility(View.VISIBLE);
                    cazaredest.setVisibility(View.VISIBLE);
                    locdest.setText("Oriunde");
                    locdest.setEnabled(false);
                    if(plecarevar.isChecked())
                        plecarevar.setChecked(false);
                } else {
                    textfiltrudest.setVisibility(View.GONE);
                    acces2.setVisibility(View.GONE);
                    autod.setVisibility(View.GONE);
                    teled.setVisibility(View.GONE);
                    trendest.setVisibility(View.GONE);
                    restd.setVisibility(View.GONE);
                    cazaredest.setVisibility(View.GONE);
                    locdest.setEnabled(true);
                    locdest.setText("");
                }
            }
        });
        View.OnClickListener inversListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locplecare.isEnabled() && locdest.isEnabled())
                {
                    String aux=locplecare.getText().toString();
                    locplecare.setText(locdest.getText().toString());
                    locdest.setText(aux);
                }
                else
                    if(locplecare.isEnabled())
                    {
                        String aux=locplecare.getText().toString();
                        plecarevar.setChecked(true);
                        destvar.setChecked(false);
                        locdest.setText(aux);
                    }
                    else
                    {
                        String aux=locdest.getText().toString();
                        destvar.setChecked(true);
                        plecarevar.setChecked(false);
                        locplecare.setText(aux);
                    }
            }
        };
        butonInvers1.setOnClickListener(inversListener);
        butonInvers2.setOnClickListener(inversListener);
        listainter = ((MainActivity) getActivity()).getLista();
        numeintersectii = new ArrayList<>();
        for (int i = 0; i < listainter.size(); i++)
            numeintersectii.add(listainter.get(i).nume);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, numeintersectii);
        locplecare.setAdapter(adapter);
        locdest.setAdapter(adapter);
        sliderdurata.setLabelBehavior(LabelFormatter.LABEL_GONE);
        float step = (float) 0.5;
        sliderdurata.setStepSize(step);
        sliderdurata.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> valori;
                valori = slider.getValues();
                String duratatext = "Durata :" + valori.get(0).toString() + "h - " + valori.get(1).toString() + "h";
                if (valori.get(1) == slider.getValueTo())
                    duratatext = duratatext + " +";
                timpmin=valori.get(0);
                timpmax=valori.get(1);
                textdurata.setText(duratatext);
            }
        });
        for(int i=0 ; i<listainter.size();i++)
        {
            A.put(listainter.get(i).nume,i);
        }
        drumurilista = ((MainActivity) getActivity()).getDrumlist();
        GVara = new Vector<>();
        GIarna = new Vector<>();
        drumcurent= new Vector<>();

        for(int i=0;i<listainter.size();i++)
        {
            GVara.add(i,new Vector<Pair<Integer, Double>>());
            GIarna.add(i,new Vector<Pair<Integer, Double>>());
        }
        for(int i=0;i<drumurilista.size();i++)
        {
            indexulDrumului[drumurilista.get(i).intjos][drumurilista.get(i).intsus]=i;
            indexulDrumului[drumurilista.get(i).intsus][drumurilista.get(i).intjos]=i;
            GVara.get(drumurilista.get(i).intjos).add(new Pair<>(drumurilista.get(i).intsus, drumurilista.get(i).timpMare));
            GVara.get(drumurilista.get(i).intsus).add(new Pair<>(drumurilista.get(i).intjos, drumurilista.get(i).timpMic));
            if(drumurilista.get(i).deIarna)
            {
                GIarna.get(drumurilista.get(i).intjos).add(new Pair<>(drumurilista.get(i).intsus, drumurilista.get(i).timpMare+drumurilista.get(i).timpMare/2));
                GIarna.get(drumurilista.get(i).intsus).add(new Pair<>(drumurilista.get(i).intjos, drumurilista.get(i).timpMic+drumurilista.get(i).timpMic/2));
            }
        }
        butoncauta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Float>valori;
                valori=sliderdurata.getValues();
                timpmin=valori.get(0);
                timpmax=valori.get(1);
                eIarna=toggleSezon.isChecked();
                if(eIarna)
                    G=GIarna;
                else G=GVara;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if(locplecare.isEnabled() && !locdest.isEnabled()) {
                    nodstart=locplecare.getText().toString();
                    if(A.containsKey(nodstart))
                    {DIJKSTRA(A.get(nodstart));afisare1();}
                else Toast.makeText(getActivity(),"Loc de plecare invalid.Alegeti unul din lista!",Toast.LENGTH_LONG).show();

                }
                if(locdest.isEnabled() && !locplecare.isEnabled()) {
                    nodfinish=locdest.getText().toString();
                    if(A.containsKey(nodfinish))
                    {DIJKSTRA2(A.get(nodfinish));afisare2();}
                    else Toast.makeText(getActivity(),"Destinatie invalida.Alegeti una din lista!",Toast.LENGTH_LONG).show();

                }
                if(locdest.isEnabled() && locplecare.isEnabled()){
                    nodstart=locplecare.getText().toString();nodfinish=locdest.getText().toString();
                    if(A.containsKey(nodfinish) && A.containsKey(nodstart))
                    {dfs(A.get(nodstart),A.get(nodfinish));afisaredfs();}
                else Toast.makeText(getActivity(),"Loc de plecare sau destinatie invalida.Alegeti din lista!",Toast.LENGTH_LONG).show();
                }


            }
        });
        return view;
    }

    public void afisare1() {
        trasee.clear();
        ArrayList<Pair<Drum, Boolean>>drumComp;
        Vector<String> Revert= new Vector<>();
        String out;
        int x1,x2;
        boolean sens;
        for (int i = 0; i < listainter.size(); i++) {
            if (D[i] != oo && D[i] != 0&&(D[i]>=timpmin && D[i]<=timpmax)&& ((autod.isChecked() && listainter.get(i).accesibil_auto) || (!autod.isChecked())) && ((trendest.isChecked() && listainter.get(i).accesibil_tren) || (!trendest.isChecked()))&& ((teled.isChecked() && listainter.get(i).accesibil_telecabina) || (!teled.isChecked())) && ((restd.isChecked() && listainter.get(i).arerestaurant) || (!restd.isChecked())) && ((cazaredest.isChecked() && listainter.get(i).arecazare) || (!cazaredest.isChecked()))) {
                out = nodstart+"-";
                x1=A.get(nodstart);
                drumComp=new ArrayList<>();
                int copiei = i;
                String copie2= listainter.get(i).nume;
                while (tata[copiei] != A.get(nodstart)) {
                    Revert.add(listainter.get(tata[copiei]).nume);
                    copiei = tata[copiei];
                }
                for(int j=Revert.size()-1;j>-1;j--)
                {out += Revert.get(j) + "-";
                x2=A.get(Revert.get(j));
                sens=(x1==drumurilista.get(indexulDrumului[x1][x2]).intjos);
                drumComp.add(new Pair<>(drumurilista.get(indexulDrumului[x1][x2]),sens));
                x1=x2;
                }
                out+=copie2;
                x2=A.get(copie2);
                sens=(x1==drumurilista.get(indexulDrumului[x1][x2]).intjos);
                drumComp.add(new Pair<Drum, Boolean>(drumurilista.get(indexulDrumului[x1][x2]),sens));
                Revert.clear();
                Traseu traseu=new Traseu(out,D[i],0,0,0,drumComp);
                traseu.setDifNivel();
                traseu.setDistanta();
                trasee.add(traseu);
            }
        }
        if(trasee.isEmpty())
        {
            Toast.makeText(getActivity(),"Nu a fost gasit niciun traseu pentru datele introduse!",Toast.LENGTH_LONG).show();
            return;
        }
        AdapterDrumuri adapterDrumuri=new AdapterDrumuri(getActivity(),trasee,listainter);
        recyclerView.setAdapter(adapterDrumuri);
        scrollView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        butonSchimbaCautarea.setVisible(true);
        ((MainActivity) getActivity()).seteazaTitluActionBar("Trasee de la "+nodstart);
    }

    public void afisare2()
    {
        trasee.clear();
        String out;
        ArrayList<Pair<Drum,Boolean>>drumComp;
        int x1,x2;
        boolean sens;
        for(int i=0;i<listainter.size();i++)
        {if(D[i]!=oo && D[i]!=0 &&(D[i]>=timpmin && D[i]<=timpmax) && ((autop.isChecked() && listainter.get(i).accesibil_auto) || (!autop.isChecked())) && ((trenp.isChecked() && listainter.get(i).accesibil_tren) || (!trenp.isChecked()))&& ((telep.isChecked() && listainter.get(i).accesibil_telecabina) || (!telep.isChecked())) && ((restp.isChecked() && listainter.get(i).arerestaurant) || (!restp.isChecked())) && ((cazarep.isChecked() && listainter.get(i).arecazare) || (!cazarep.isChecked())))
        {out=listainter.get(i).nume+"-";
        x1=i;drumComp=new ArrayList<>();
            int copiei=i;
            while(tata[copiei]!=A.get(nodfinish))
            {
                out+=listainter.get(tata[copiei]).nume+"-";
                x2=tata[copiei];
                sens=(x1==drumurilista.get(indexulDrumului[x1][x2]).intjos);
                drumComp.add(new Pair<>(drumurilista.get(indexulDrumului[x1][x2]), sens));
                x1=x2;
                copiei=tata[copiei];
            }
            out+=nodfinish;
            x2=A.get(nodfinish);
            sens=(x1==drumurilista.get(indexulDrumului[x1][x2]).intjos);
            drumComp.add(new Pair<>(drumurilista.get(indexulDrumului[x1][x2]), sens));
            Traseu traseu=new Traseu(out,D[i],0,0,0,drumComp);
            traseu.setDifNivel();
            traseu.setDistanta();
            trasee.add(traseu);
        }
        }
        if(trasee.isEmpty())
        {
            Toast.makeText(getActivity(),"Nu a fost gasit niciun traseu pentru datele introduse!",Toast.LENGTH_LONG).show();
            return;
        }
        AdapterDrumuri adapterDrumuri=new AdapterDrumuri(getActivity(),trasee,listainter);
        recyclerView.setAdapter(adapterDrumuri);
        scrollView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        butonSchimbaCautarea.setVisible(true);
        ((MainActivity) getActivity()).seteazaTitluActionBar("Trasee pana la "+nodfinish);
    }

    public void DIJKSTRA(int nod)
    {
        for(int i=0;i<listainter.size();i++)
        {
            D[i]=oo;
            viz[i]=false;
        }
        D[nod]=0;
        Noddij nodx=new Noddij(nod,0);
        coada=new PriorityQueue<>();
        coada.add(nodx);
        viz[nod]=true;
        while(!coada.isEmpty())
        {

            int nodcurent=coada.poll().node;
            viz[nodcurent]=false;
            for(int i=0;i<G.get(nodcurent).size();i++)
            {
                int vecin=G.get(nodcurent).get(i).first;
                double cost=G.get(nodcurent).get(i).second;
                if(D[vecin]>D[nodcurent]+cost)
                {
                    D[vecin]=D[nodcurent]+cost;
                    tata[vecin]=nodcurent;
                    if(!viz[vecin])
                    {
                        viz[vecin]=true;
                        coada.add(new Noddij(vecin,D[vecin]));
                    }
                }
            }
        }
    }

    public void DIJKSTRA2(int nod)
    {
        for(int i=0;i<listainter.size();i++)
        {
            D[i]=oo;
            viz[i]=false;
        }
        D[nod]=0;
        Noddij nodx=new Noddij(nod,0);
        coada=new PriorityQueue<>();
        coada.add(nodx);
        viz[nod]=true;
        while(!coada.isEmpty())
        {

            int nodcurent=coada.poll().node;
            viz[nodcurent]=false;
            for(int i=0;i<G.get(nodcurent).size();i++)
            {
                int vecin=G.get(nodcurent).get(i).first;
                double cost=0;
                for(int j=0;j<G.get(vecin).size();j++)
                {if(G.get(vecin).get(j).first==nodcurent) cost=G.get(vecin).get(j).second;}

                if(D[vecin]>D[nodcurent]+cost)
                {
                    D[vecin]=D[nodcurent]+cost;
                    tata[vecin]=nodcurent;
                    if(!viz[vecin])
                    {
                        viz[vecin]=true;
                        coada.add(new Noddij(vecin,D[vecin]));
                    }
                }
            }
        }
    }

    public void dfs(int inceput, int sfarsit)
    {
        vdfs[inceput]=true;
        if(inceput==sfarsit)
        {
            int x1,x2;
            boolean sens;
            String s;
            s=nodstart;
            x1=A.get(nodstart);
            ArrayList<Pair<Drum,Boolean>>drumnou= new ArrayList<>();
            for(int i=0;i<drumcurent.size();i++) {
                x2=drumcurent.get(i);
                sens=(x1==drumurilista.get(indexulDrumului[x1][x2]).intjos);
                drumnou.add(new Pair<>(drumurilista.get(indexulDrumului[x1][x2]),sens));
                s+="-";
                s+=listainter.get(drumcurent.get(i)).nume;
                x1=x2;
            }
            drumurix.put(costcurent, new Pair<>(drumnou, s));

        }
        for(int i=0;i<G.get(inceput).size();i++)
        {
            int vecin=G.get(inceput).get(i).first;
            double cost=G.get(inceput).get(i).second;
            if(!vdfs[vecin])
            {costcurent+=cost;
                vdfs[vecin]=true;
                drumcurent.add(vecin);
                dfs(vecin,sfarsit);
                costcurent-=cost;
                drumcurent.remove(drumcurent.size()-1);
            }
        }
        vdfs[inceput]=false;}

    public void afisaredfs()
    {
        trasee.clear();
        for(double i: drumurix.keySet())
        {
            if(i>=timpmin && i<=timpmax) {
                Traseu traseu=new Traseu(drumurix.get(i).second, i, 0, 0, 0, drumurix.get(i).first);
                traseu.setDistanta();
                traseu.setDifNivel();
                trasee.add(traseu);
            }
        }
        if(trasee.isEmpty())
        {
            Toast.makeText(getActivity(),"Nu a fost gasit niciun traseu pentru datele introduse!",Toast.LENGTH_LONG).show();
            return;
        }
        AdapterDrumuri adapterDrumuri=new AdapterDrumuri(getActivity(),trasee,listainter);
        recyclerView.setAdapter(adapterDrumuri);
        scrollView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        butonSchimbaCautarea.setVisible(true);
        ((MainActivity) getActivity()).seteazaTitluActionBar(nodstart+"-"+nodfinish);
        drumurix.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menuplanificator,menu);
        butonSchimbaCautarea=menu.findItem(R.id.butonSchimbaCautarea);
        butonSchimbaCautarea.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                recyclerView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                butonSchimbaCautarea.setVisible(false);
                ((MainActivity) getActivity()).seteazaTitluActionBar("Planificator");
                return true;
            }
        });
    }

    public class Noddij implements Comparable<Noddij> {

        public int node;
        public double cost;

        public Noddij(int node, double cost) {
            this.node = node;
            this.cost = cost;
        }
        public int compareTo(Noddij o)
        {
            if (this.cost < o.cost)
                return -1;
            if (this.cost > o.cost)
                return 1;
            return 0;
        }
    }
}