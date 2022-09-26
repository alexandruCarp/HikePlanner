package com.example.hike_planner_v2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link obiectiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class obiectiveFragment extends Fragment {

    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<Intersectie>obiective;
    public obiectiveFragment() {
        // Required empty public constructor
    }
    public static obiectiveFragment newInstance() {
        obiectiveFragment fragment = new obiectiveFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obiective=((MainActivity)getActivity()).getInterlistRelevante();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view=inflater.inflate(R.layout.fragment_obiective, container, false);
        recyclerView= view.findViewById(R.id.recyclerV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new Adapter(getActivity(),obiective);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menuobiective,menu);
        MenuItem searchitem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)searchitem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}