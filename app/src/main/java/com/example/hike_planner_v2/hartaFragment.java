package com.example.hike_planner_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.mapbox.geojson.Point.fromJson;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;


public class hartaFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;
    hartaFragmentArgs args;
    SearchView searchView;
    CardView cardView,cardViewDrum;
    Button butonStart,butonFinish,butonDetalii;
    ListView listView;
    MapboxMap harta;
    boolean butondescarcare;
    SharedPreferences sharedPreferences;
    Intersectie obselectat=null;
    ArrayList<Intersectie>listainter;
    ArrayList<Drum>listadrum;
    com.mapbox.mapboxsdk.geometry.LatLngBounds latLngBounds = new LatLngBounds.Builder().include(new com.mapbox.mapboxsdk.geometry.LatLng(45.497,25.557)).include(new LatLng(45.274,25.3318)).build();
    private MapView mapView;
    OfflineManager offlineManager;
    MenuItem butondesc;
    FeatureCollection featureCollectionDrumuri,featureCollectionInter;
    GeoJsonSource sourceInter,sourceDrumuri;
    public hartaFragment() {
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
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        View view=inflater.inflate(R.layout.fragment_harta, container, false);
        searchView=view.findViewById(R.id.search_harta);
        cardView=view.findViewById(R.id.card_element_harta);
        cardViewDrum=view.findViewById(R.id.card_drumH);
        listView=view.findViewById(R.id.lista_cautare);
        listainter=((MainActivity)getActivity()).getLista();
        listadrum=((MainActivity)getActivity()).getDrumlist();
        offlineManager=OfflineManager.getInstance(getActivity());
        sharedPreferences=getActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        args=null;
        if(getArguments()!=null && !getArguments().isEmpty())
            args=hartaFragmentArgs.fromBundle(getArguments());
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                harta=mapboxMap;
                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/alexandruc/ckmqds9br10e117s2xpkk7tzp"), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);

                        String geoJson1=loadGeoJsonFromAsset(getActivity(),"intersectii_harta.geojson");
                        String geoJson2=loadGeoJsonFromAsset(getActivity(),"drumuri_harta.geojson");
                        featureCollectionInter=FeatureCollection.fromJson(geoJson1);
                        featureCollectionDrumuri=FeatureCollection.fromJson(geoJson2);
                        sourceInter=new GeoJsonSource("sursa-intersectii",featureCollectionInter);
                        sourceDrumuri=new GeoJsonSource("sursa-drumuri",featureCollectionDrumuri);
                        style.addSource(sourceInter);
                        style.addSource(sourceDrumuri);

                        SymbolLayer layerinter=new SymbolLayer("layer-intersectii","sursa-intersectii");
                        layerinter.withProperties(
                                PropertyFactory.textField(Expression.step(Expression.zoom(), literal(""),Expression.stop(literal(13f),get("nume")))),
                                PropertyFactory.iconImage("{tip}"),
                                PropertyFactory.iconOpacity(0.7f),
                                PropertyFactory.iconSize(Expression.switchCase(
                                        Expression.eq(get("selectat"),"da"), literal(1.5f),
                                        literal(1)
                                )),
                                PropertyFactory.textAnchor(Property.TEXT_ANCHOR_TOP),
                                PropertyFactory.textOffset(new Float[] {0f,0.5f})
                                              );
                        style.addLayer(layerinter);
                        LineLayer layerdrumuri=new LineLayer("layer-drumuri","sursa-drumuri");
                        layerdrumuri.withProperties(
                                PropertyFactory.lineWidth(Expression.switchCase(
                                        Expression.eq(get("selectat"),"da"), literal(6),
                                        literal(4)
                                )),
                                PropertyFactory.lineColor(Expression.switchCase(
                                        Expression.eq(get("selectat"),"da"),Expression.color(Color.BLUE),
                                        Expression.color(Color.GRAY)
                                ))
                        );
                        style.addLayerBelow(layerdrumuri,"layer-intersectii");
                        SymbolLayer layerMarcaje=new SymbolLayer("layer-marcaje","sursa-drumuri");
                        layerMarcaje.withProperties(
                          PropertyFactory.iconImage("{marcaj}"),
                                PropertyFactory.iconSize(0.75f),
                                PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE),
                                PropertyFactory.symbolSpacing(75f),
                                PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_VIEWPORT)
                        );
                        style.addLayerAbove(layerMarcaje,"layer-drumuri");
                        if (args != null) {
                            if (args.getIndexObSelectat()>=0)
                                obselectat=listainter.get(args.getIndexObSelectat());
                            else selecteazaTraseul(-args.getIndexObSelectat(),args.getIndecsiDrumuriSelectate());

                        }
                        if(obselectat!=null)
                            mutaCameraLaObiectvu(obselectat.index,15,false);

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                for (Feature feature:featureCollectionDrumuri.features())
                                    feature.properties().addProperty("selectat","nu");
                                sourceDrumuri.setGeoJson(featureCollectionDrumuri);
                                for(Feature feature:featureCollectionInter.features())
                                    feature.properties().addProperty("selectat","nu");
                                sourceInter.setGeoJson(featureCollectionInter);
                                cardView.setVisibility(View.GONE);
                                cardViewDrum.setVisibility(View.GONE);
                                boolean ok;
                                ok=selecteazaDrumul(mapboxMap.getProjection().toScreenLocation(point));
                                if(!ok)
                                ok=selecteazaIntersectia(mapboxMap.getProjection().toScreenLocation(point));
                                    if (searchView.getVisibility() == View.VISIBLE) {
                                        searchView.setVisibility(View.GONE);
                                        searchView.setQuery("", false);
                                    } else if(!ok)searchView.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                return true;
                            }
                        });
                        
                    }
                });
            }
        });
        init_cautare();
        if(!sharedPreferences.getBoolean("descarcata",false)) {
            butondescarcare=true;
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    for(int i=0;i<offlineRegions.length;i++)
                    {
                        offlineRegions[i].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
        if(!internetIsConnected())
            butondescarcare=false;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchView.setQuery(listView.getItemAtPosition(position).toString(),true);
                listView.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for(int i=0;i<listainter.size();i++)
                    if(listainter.get(i).nume.toLowerCase().equals(query.toLowerCase()))
                    {
                        mutaCameraLaObiectvu(i,15,true);
                        listView.setVisibility(View.GONE);
                    }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(listView.getVisibility()!=View.VISIBLE && !newText.equals(""))
                    listView.setVisibility(View.VISIBLE);
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==searchView.getId())
                    listView.setVisibility(View.VISIBLE);
                else listView.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                cardViewDrum.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {listView.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
                cardViewDrum.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
    public void descarcaHarta()
    {
        OfflineTilePyramidRegionDefinition definition= new OfflineTilePyramidRegionDefinition("mapbox://styles/alexandruc/ckmqds9br10e117s2xpkk7tzp",latLngBounds,10,15,getActivity().getResources().getDisplayMetrics().density);
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nume_regiune","BUCEGI");
            String json=jsonObject.toString();
            metadata=json.getBytes();

        }catch (Exception exception)
        {
            Log.e("TAG","Failed to encode metadata: " + exception.getMessage());
            metadata=null;
        }
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                final AlertDialog.Builder alertprogres=new AlertDialog.Builder(getActivity());
                LayoutInflater inflater= getActivity().getLayoutInflater();
                View view=inflater.inflate(R.layout.descdialog,null);
                final ProgressBar progressBar=view.findViewById(R.id.progressBar);
                progressBar.setMax(100);
                alertprogres.setView(view);
                alertprogres.setCancelable(false);
                final AlertDialog dialog=alertprogres.create();
                dialog.show();
                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                    @Override
                    public void onStatusChanged(OfflineRegionStatus status) {
                        int percentage = (int) (100 * status.getCompletedResourceCount() / status.getRequiredResourceCount());
                        progressBar.setProgress(percentage);
                        if (status.isComplete()) {
                            butondesc.setVisible(false);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("descarcata",true);
                            editor.apply();
                            Toast.makeText(getActivity(),"Harta descarcata cu succes!",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {
                    }

                    @Override
                    public void mapboxTileCountLimitExceeded(long limit) {
                    }
                });
            }

            @Override
            public void onError(String error) {
            }
        });
    }
    public boolean selecteazaDrumul(PointF pixel)
    {
        List<Feature>features=harta.queryRenderedFeatures(pixel,"layer-drumuri");
        if(features.size()>0)
        {
            Feature featureselectat=features.get(0);
            for(Feature feature : featureCollectionDrumuri.features())
            {
                if(feature.getStringProperty("index").equals(featureselectat.getStringProperty("index")))
                {feature.properties().addProperty("selectat","da");
                    sourceDrumuri.setGeoJson(featureCollectionDrumuri);
                    int indexDrumSel=Integer.parseInt(featureselectat.getStringProperty("index"))-1;
                    deschideCardulDrumului(listadrum.get(indexDrumSel));
                    return true;
                }
            }
        }
        return false;
    }
    public boolean selecteazaIntersectia(PointF pixel)
    {
        List<Feature>features=harta.queryRenderedFeatures(pixel,"layer-intersectii");
        if(features.size()>0)
        {
            Feature featureselectat=features.get(0);
            for(Feature feature : featureCollectionInter.features())
            {
                if(feature.getStringProperty("index").equals(featureselectat.getStringProperty("index"))) {
                    feature.properties().addProperty("selectat", "da");
                    sourceInter.setGeoJson(featureCollectionInter);
                    int indexObSel=Integer.parseInt(featureselectat.getStringProperty("index"));
                    deschideCardulObiectivului(listainter.get(indexObSel));
                    mutaCameraLaObiectvu(indexObSel,-1,true);
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menuharta,menu);
        butondesc=menu.findItem(R.id.butondownload_harta);
        butondesc.setVisible(butondescarcare);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.butondownload_harta)
            {
                AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
                alert.setTitle("Descarcare harta");
                alert.setMessage("Doriti sa descarcati harta (~20MB) pentru a o putea folosi si cand sunteti offline?");
                alert.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        descarcaHarta();
                    }
                });
                alert.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setCancelable(true);
                alert.create().show();
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
    static String loadGeoJsonFromAsset(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, Charset.forName("UTF-8"));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    public void init_cautare()
    {
        ArrayList<String>numeintersectii;
        numeintersectii = new ArrayList<>();
        for (int i = 0; i < listainter.size(); i++)
            numeintersectii.add(listainter.get(i).nume);
        arrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,numeintersectii);
        listView.setAdapter(arrayAdapter);
    }
    public void mutaCameraLaObiectvu(final int indexObiectivSelectat, final int zoom,boolean animat)
    {
        Feature featuresel = null;
        for(Feature feature : featureCollectionInter.features())
        {
            if(feature.getStringProperty("index").equals(String.valueOf(indexObiectivSelectat))) {
                featuresel=feature;
                break;
            }
        }
        Point point=fromJson(featuresel.geometry().toJson());
        final LatLng coord=new LatLng(point.latitude(),point.longitude());
        CameraUpdate cameraUpdate=new CameraUpdate() {
            @Nullable
            @Override
            public CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap) {
                if(zoom!=-1)
                    return new CameraPosition.Builder().target(coord).zoom(zoom).build();
                else return new CameraPosition.Builder().target(coord).build();
            }
        };
        if(animat)
            harta.easeCamera(cameraUpdate,1000);
        else harta.moveCamera(cameraUpdate);
    }
    public void deschideCardulObiectivului(final Intersectie obiectivSelectat)
    {
        TextView titluView = cardView.findViewById(R.id.titluobH);
        TextView altiView = cardView.findViewById(R.id.altitudineobH);
        ImageView masinaView=cardView.findViewById(R.id.imaginemasinaH);
        ImageView restaView=cardView.findViewById(R.id.imaginerestaurantH);
        ImageView hotelView=cardView.findViewById(R.id.imaginehotelH);
        ImageView obImgView=cardView.findViewById(R.id.imagineobH);
        butonStart=cardView.findViewById(R.id.butonstartH);
        butonFinish=cardView.findViewById(R.id.butondestH);
        butonDetalii=cardView.findViewById(R.id.butonspreDetalii);

        titluView.setText(obiectivSelectat.nume);
        altiView.setText("alt."+ obiectivSelectat.altitudine);
        if(obiectivSelectat.idimagine_mica!=0)
        {obImgView.setImageResource(obiectivSelectat.idimagine_mica);
        obImgView.setVisibility(View.VISIBLE);
        butonDetalii.setVisibility(View.VISIBLE);
        }
        else {obImgView.setVisibility(View.GONE);butonDetalii.setVisibility(View.GONE);}
        if(obiectivSelectat.accesibil_auto)
            masinaView.setImageResource(R.drawable.ic_masina_verde);
        else masinaView.setImageResource(R.drawable.ic_masina_rosie);
        if(obiectivSelectat.arerestaurant)
            restaView.setImageResource(R.drawable.ic_restaurant_verde);
        else restaView.setImageResource(R.drawable.ic_restaurant_rosu);
        if(obiectivSelectat.arecazare)
            hotelView.setImageResource(R.drawable.ic_hotel_verde);
        else hotelView.setImageResource(R.drawable.ic_hotel_rosu);
        View.OnClickListener click_butoane=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId()==butonStart.getId())
                {
                    hartaFragmentDirections.ActionHartaFragmentToPlanificatorFragment action=hartaFragmentDirections.actionHartaFragmentToPlanificatorFragment(obiectivSelectat.nume,null);
                    Navigation.findNavController(getActivity(),R.id.fragment).navigate(action);
                }
                else
                    if (v.getId()==butonFinish.getId())
                    {
                        hartaFragmentDirections.ActionHartaFragmentToPlanificatorFragment action=hartaFragmentDirections.actionHartaFragmentToPlanificatorFragment(null,obiectivSelectat.nume);
                        Navigation.findNavController(getActivity(),R.id.fragment).navigate(action);
                    }
                    else {
                        Intent intent=new Intent(v.getContext(),Detaliiobiectiv.class);
                        intent.putExtra("obiectivindex",obiectivSelectat.index);
                        v.getContext().startActivity(intent);
                    }
            }
        };
        butonStart.setOnClickListener(click_butoane);
        butonFinish.setOnClickListener(click_butoane);
        butonDetalii.setOnClickListener(click_butoane);
        cardView.setVisibility(View.VISIBLE);
    }
    public void deschideCardulDrumului(final Drum drum)
    {
        TextView titluDrum=cardViewDrum.findViewById(R.id.text_traseuH);
        TextView timpDrum=cardViewDrum.findViewById(R.id.textTimpH);
        TextView distDrum=cardViewDrum.findViewById(R.id.textDistantaH);
        ImageView imgMarcaj=cardViewDrum.findViewById(R.id.imageMarcaj);
        titluDrum.setText(listainter.get(drum.intjos).nume +" - "+listainter.get(drum.intsus).nume);
        timpDrum.setText(drum.timpMare+"h");
        double x=(double) drum.distanta/10;
        distDrum.setText(x+"km");
        String numeDr="ic_"+drum.marcaj;
        int id=getActivity().getResources().getIdentifier(numeDr,"drawable",getActivity().getPackageName());
        imgMarcaj.setImageResource(id);
        cardViewDrum.setVisibility(View.VISIBLE);

    }
    public void selecteazaTraseul(int n,int[] drumuri)
    {
        for(Feature feature : featureCollectionDrumuri.features())
            for (int i=0;i<n;i++)
                if(feature.getStringProperty("index").equals(String.valueOf(drumuri[i]+1)))
                    feature.properties().addProperty("selectat","da");
                sourceDrumuri.setGeoJson(featureCollectionDrumuri);
    }
}