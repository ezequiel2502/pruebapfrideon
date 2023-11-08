package com.example.gps_test;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_test.ui.ActivityBuscarEventosRecycler.EventosCercanosAdapter;
import com.example.gps_test.ui.ActivityBuscarEventosRecycler.ModelEvento;
import com.example.gps_test.ui.map.Curvaturas;
import com.example.gps_test.ui.map.Location_Variables;
import com.example.gps_test.ui.map.Map_Camera_Settings;
import com.example.gps_test.ui.map.Marker_Variables;
import com.example.gps_test.ui.map.Routing_Variables;
import com.example.gps_test.ui.map.Search_Variables;
import com.example.gps_test.ui.map.TupleDouble;
import com.example.gps_test.ui.recyclerView.MyListAdapter;
import com.example.gps_test.ui.recyclerView.MyListData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tomtom.sdk.location.GeoLocation;
import com.tomtom.sdk.location.GeoPoint;
import com.tomtom.sdk.location.LocationProvider;
import com.tomtom.sdk.location.OnLocationUpdateListener;
import com.tomtom.sdk.location.android.AndroidLocationProvider;
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig;
import com.tomtom.sdk.map.display.TomTomMap;
import com.tomtom.sdk.map.display.camera.CameraOptions;
import com.tomtom.sdk.map.display.camera.CameraTrackingMode;
import com.tomtom.sdk.map.display.marker.Marker;
import com.tomtom.sdk.map.display.marker.MarkerClickListener;
import com.tomtom.sdk.map.display.ui.MapFragment;
import com.tomtom.sdk.map.display.ui.MapReadyCallback;
import com.tomtom.sdk.map.display.ui.MapView;
import com.tomtom.sdk.routing.RoutePlanner;
import com.tomtom.sdk.routing.online.OnlineRoutePlanner;
import com.tomtom.sdk.routing.route.Route;
import com.tomtom.sdk.search.Search;
import com.tomtom.sdk.search.online.OnlineSearch;
import com.tomtom.sdk.search.ui.SearchFragment;
import com.tomtom.sdk.search.ui.SearchFragmentListener;
import com.tomtom.sdk.search.ui.model.PlaceDetails;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.Triple;


public class BuscarEventosMapaActivity extends AppCompatActivity {

    private MapView mapView;
    private static TomTomMap tomtomMap;
    private LocationProvider geoPosition;

    private GeoPoint CurrentPosition;
    private GeoPoint previousImmediatePosition;

    private SearchFragment searcher;

    private List<GeoPoint> route_Points = new ArrayList<GeoPoint>();

    private List<GeoPoint> online_Route_Points = new ArrayList<GeoPoint>();

    private Boolean app_Start = true;

    private List<Float> touchPosition = new ArrayList<Float>();

    //Lo usamos cuando queremos presionar un marcador y no queremos que el mapa también tome el click
    private boolean ignoreLongClick = false;
    private static boolean ignoreRouteClick = false;

    private Route route_Summary;
    private String startLocation;
    private String finishLocation;
    private int curvesAmount;

    private Marker selectedMarker;

    private com.tomtom.sdk.map.display.route.Route displayed_Route;
    private static List<List<GeoPoint>> summaryReferences = new ArrayList<>();

    private View route_Instructions;
    private MyListAdapter summaryAdapter;
    private List<GeoPoint> current_Route_Points = new ArrayList<GeoPoint>();
    private static List<Curvaturas> currentApiDrawRoad;
    private static BottomSheetDialog routeSummaryDialog;
    private int APIRequestsCounter = 0;

    private List<Integer> hashListRequestsCounter = new ArrayList<>();
    private final Handler h = new Handler(); //Necesario para controlar la fecha cada segundo
    private static Date currentTime = null;
    private boolean navStart = false;
    private boolean abandono = true;
    private double distanciaRecorrida = 0;
    CardView backgroundCard;
    ImageView instructionsViewer;
    TextView instructionsViewerExtraText;
    TextView instructionsViewerDistance;
    List<Curvaturas> instructions;
    TupleDouble meta;
    FloatingActionButton ComenzarEvento;
    FloatingActionButton TerminarEvento;
    DecimalFormat df;
    Date horaInicio;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference refRoutes = database.getReference().child("Route");
    DatabaseReference refPublicRoutes = database.getReference().child("PublicRoute");
    DatabaseReference refPublicEvents = database.getReference().child("Eventos").child("Eventos Publicos");




    @SuppressLint({"UnsafeOptInUsageError", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.buscar_eventos_mapa);
        ComenzarEvento = findViewById(R.id.startEventParticipation);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(com.example.gps_test.R.id.map_fragment);

        mapFragment.getMapAsync(mapReady);

        Chronometer cronometro = findViewById(R.id.idCMmeter);
        //cronometro.setVisibility(View.INVISIBLE);
        df = new DecimalFormat("#.##");
        instructionsViewer =  findViewById(com.example.gps_test.R.id.imageViewIndications);
        instructionsViewerExtraText =  findViewById(R.id.textViewIndicationsExtra);
        instructionsViewerDistance = findViewById(R.id.textViewIndicationsDistance);
        Intent intent = getIntent();
        instructions = (List<Curvaturas>) intent.getSerializableExtra("List_Navigation");
        Intent intent1 = getIntent();
        List<TupleDouble> listaPuntos = (List<TupleDouble>) intent1.getSerializableExtra("List_Of_Points");
        TerminarEvento = findViewById(R.id.finishEventParticipation);
        meta = listaPuntos.get(listaPuntos.size()-1);
        backgroundCard = findViewById(R.id.backgroundCard);

        FloatingActionButton findEvents = findViewById(R.id.findCloseEvents);
        findEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked();
            }
        });
        findEvents.setVisibility(View.GONE);


        ComenzarEvento.setEnabled(false);
        ComenzarEvento.setImageAlpha(75);
        ComenzarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComenzarEvento.setEnabled(false);
                ComenzarEvento.setImageAlpha(75);
                TerminarEvento.setEnabled(true);
                TerminarEvento.setImageAlpha(255);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss",
                        Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
                cronometro.setBase(SystemClock.elapsedRealtime());
                cronometro.start();
                horaInicio = Calendar.getInstance().getTime();
                String comienzo = dateFormat.format(Calendar.getInstance().getTime());
                DatosParticipacionEvento datos = new DatosParticipacionEvento(comienzo, "pending", "pending");
                Intent intent = getIntent();
                String EventoId = intent.getStringExtra("Evento");
                database.getReference().child("Events_Data").child(userId).child(EventoId).setValue(datos);
                navStart = true;
                new Map_Camera_Settings().setTracking(tomtomMap);
            }
        });



        Dialog dialog = new Dialog(BuscarEventosMapaActivity.this);

        TerminarEvento.setEnabled(false);
        TerminarEvento.setImageAlpha(75);
        TerminarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navStart = false;
                new Map_Camera_Settings().disableTracking(tomtomMap);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss",
                        Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
                cronometro.stop();
                String finalizacion = dateFormat.format(Calendar.getInstance().getTime());
                String distancia = String.valueOf(distanciaRecorrida);
                Intent intent = getIntent();
                String EventoId = intent.getStringExtra("Evento");
                database.getReference().child("Events_Data").child(userId).child(EventoId).child("finalizacion").setValue(finalizacion);
                database.getReference().child("Events_Data").child(userId).child(EventoId).child("distanciaCubierta").setValue(distancia);
                if (!abandono)
                {
                    database.getReference().child("Events_Data").child(userId).child(EventoId).child("abandono").setValue("No");
                    showFinishPopUp(Calendar.getInstance().getTime(), distancia, "Terminado");
                }
                else
                {
                    database.getReference().child("Events_Data").child(userId).child(EventoId).child("abandono").setValue("Si");
                    showFinishPopUp(Calendar.getInstance().getTime(), distancia, "Abandonó");
                }
                abandono = true;
            }

        });

        FloatingActionButton OpenBottomSheet = findViewById(R.id.openRouteSummary);
        OpenBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        BuscarEventosMapaActivity.this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(BuscarEventosMapaActivity.this)
                        .inflate(R.layout.bottom_sheet_layout,
                                (LinearLayout)findViewById(R.id.modalBottomSheetContainer));
                route_Instructions = bottomSheetView;
                RecyclerView recyclerView = (RecyclerView) route_Instructions.findViewById(R.id.routeSummary2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(BuscarEventosMapaActivity.this));
                recyclerView.setAdapter(summaryAdapter);
                routeSummaryDialog = bottomSheetDialog;

                //Para evitar que el cuadro haga scroll con la lista en paralelo
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        /*v.getParent().requestDisallowInterceptTouchEvent(true);
                        v.onTouchEvent(event);*/
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                //v.getParent().requestDisallowInterceptTouchEvent(true);
                                bottomSheetDialog.getBehavior().setDraggable(false);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                //v.getParent().requestDisallowInterceptTouchEvent(false);
                                bottomSheetDialog.getBehavior().setDraggable(true);
                                break;
                            default:
                                break;
                        }
                        v.onTouchEvent(event);
                        return true;
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });





    }

    public void showFinishPopUp(Date finalizacion, String distancia, String abandono)
    {
        Dialog dialogEvents = new Dialog(BuscarEventosMapaActivity.this);
        dialogEvents.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEvents.setContentView(R.layout.stats_event_finished);
        dialogEvents.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogEvents.setCancelable(true);
        dialogEvents.getWindow().getAttributes().windowAnimations = com.example.gps_test.R.style.animation;

        TextView tiempo, estado, finalizado, distancia_recorrida, okay_text, cancel_text;
        okay_text = dialogEvents.findViewById(R.id.okay_text);
        cancel_text = dialogEvents.findViewById(R.id.cancel_text);
        tiempo = dialogEvents.findViewById(R.id.tv_tiempo);
        estado = dialogEvents.findViewById(R.id.tv_Estado);
        finalizado = dialogEvents.findViewById(R.id.tv_Finalizado);
        distancia_recorrida = dialogEvents.findViewById(R.id.tv_Distancia);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss",
                Locale.getDefault());
        SimpleDateFormat shortHourFormat = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
        long crono = finalizacion.getTime() - horaInicio.getTime();
        tiempo.setText(String.valueOf(shortHourFormat.format(crono)));
        estado.setText(abandono);
        finalizado.setText(dateFormat.format(finalizacion));
        distancia_recorrida.setText(distancia);

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si queremos retornar resultados
                Intent data = new Intent();
                data.putExtra("Result","Calificar");
                Intent intent = getIntent();
                String EventoId = intent.getStringExtra("Evento");
                data.putExtra("EventID", EventoId);
                setResult(RESULT_OK, data);
                dialogEvents.dismiss();
                BuscarEventosMapaActivity.this.finish();
            }
        });
        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si queremos retornar resultados
                Intent data = new Intent();
                data.putExtra("Result","Salir");
                setResult(RESULT_OK, data);
                dialogEvents.dismiss();
                BuscarEventosMapaActivity.this.finish();
            }
        });
        dialogEvents.show();
    }

    public void showAlertDialogButtonClicked() {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduzca la distancia en metros");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.input_distance, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("OK", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            EditText editText = customLayout.findViewById(R.id.editDistance);
            showEventsDialog(editText.getText().toString());
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showEventsDialog(String expectedDistance)
    {
        List<String> availableEvents = new ArrayList<>();
        Dialog dialogEvents = new Dialog(BuscarEventosMapaActivity.this);
        dialogEvents.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEvents.setContentView(R.layout.list_events_dialog);
        dialogEvents.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogEvents.setCancelable(true);
        dialogEvents.getWindow().getAttributes().windowAnimations = com.example.gps_test.R.style.animation;
        RecyclerView recyclerView = (RecyclerView) dialogEvents.findViewById(R.id.eventsList);
        recyclerView.setHasFixedSize(true);

        ArrayList<ModelEvento> data = new ArrayList<>();
        EventosCercanosAdapter adapter=new EventosCercanosAdapter(data, BuscarEventosMapaActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BuscarEventosMapaActivity.this));



        refPublicEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);
                    try {
                        double d = Double.parseDouble(evento.getRuta());
                        refRoutes.child(evento.getRuta()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Ruta ruta_actual = snapshot.getValue(Ruta.class);
                                TupleDouble inicio = ruta_actual.routePoints.get(0);
                                TupleDouble meta = ruta_actual.routePoints.get(ruta_actual.routePoints.size() - 1);
                                if (distance(CurrentPosition.getLatitude(), inicio.getLatitude(),
                                        CurrentPosition.getLongitude(), inicio.getLongitude()) <= Double.parseDouble(expectedDistance) )
                                {
                                    data.add(evento);
                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (NumberFormatException nfe) {

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialogEvents.show();
    }

    //Esto va a habilitar el botón de inicio en el momento y distancia esperados
    public void enableStartButton (ImageButton DeletePoints)
    {
        Date eventTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss",
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));

        Intent intent = getIntent();
        String fecha = intent.getStringExtra("Fecha_Hora");
        TupleDouble partida = (TupleDouble) intent.getSerializableExtra("Start_Point");
        if (fecha != null)
        {
           String data = intent.getStringExtra("Fecha_Hora");
           try{
           eventTime.setTime( dateFormat.parse(data).getTime());}
           catch (java.text.ParseException error){}
        }
        h.post(new Runnable() {
            @Override
            public void run() {
                currentTime = Calendar.getInstance().getTime();
                if (currentTime.getTime() >= eventTime.getTime() && distance(CurrentPosition.getLatitude(), partida.getLatitude(),
                        CurrentPosition.getLongitude(), partida.getLongitude()) <= 300)
                {
                    DeletePoints.setEnabled(true);
                    DeletePoints.setImageAlpha(255);
                }
                h.postDelayed(this, 1000);
            }
        });
    }


    //Este evento nos permite obtener las coordenadas de pantalla que toca el usuario, no es relevante para el mapa o los marcadores, si para donde queremos que aparezcan controles como menús secundarios
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            touchPosition.add(ev.getX());
            touchPosition.add(ev.getY());
        }
        return super.dispatchTouchEvent(ev);
    }

    //Esta interfaz se implementa para usar el mapa  cuando esta listo de manera asincrona (de manera local sería)
    MapReadyCallback mapReady = new MapReadyCallback() {
        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public void onMapReady(@NonNull TomTomMap tomTomMap) {
            Location_Variables a = new Location_Variables();
            AndroidLocationProviderConfig androidLocationProviderConfig = a.getAndroidLocationProviderConfig();


            LocationProvider locationProvider = new AndroidLocationProvider(
                    getApplicationContext(),
                    androidLocationProviderConfig
            );

            locationProvider.enable();
            tomTomMap.setLocationProvider(locationProvider);

            tomTomMap.enableLocationMarker(a.getLocationMarker());


            //Acá lo único que hago es asignar las variables para que sean accesibles al resto del formulario, tomtomMap es el mapa, geoPosition es una clase que maneja la localización con android.
            tomtomMap = tomTomMap;

            geoPosition = locationProvider;
            geoPosition.addOnLocationUpdateListener(locationUpdateListener);

            Intent intent = getIntent();
            List<TupleDouble> points = (List<TupleDouble>)intent.getSerializableExtra("List_Of_Points");
            if (points != null)
            {
                loadRouteOnMap((List<TupleDouble>) intent.getSerializableExtra("List_Of_Points"),
                        (List<com.example.gps_test.ui.recyclerView.MyListData>) intent.getSerializableExtra("Resume_Data"));
            }

        }
    };

    public List<GeoPoint> listPairTolistGeo(List<TupleDouble> ev) {

        List<GeoPoint> a = new ArrayList<>();

        for (int i = 0; i < ev.size(); i++) {
            a.add(new GeoPoint(ev.get(i).getLatitude(), ev.get(i).getLongitude()));
        }
        return a;
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void loadRouteOnMap(List<TupleDouble> points, List<com.example.gps_test.ui.recyclerView.MyListData> resume) {
        Routing_Variables helper = new Routing_Variables();
        RoutePlanner routePlanner = OnlineRoutePlanner.create(getApplicationContext(), BuildConfig.CREDENTIALS_KEY, null);
        List<GeoPoint> geometry = listPairTolistGeo(points);
        current_Route_Points = geometry;
        displayed_Route = tomtomMap.addRoute(helper.setRoute(geometry));
        //tomtomMap.setLocationProvider(helper.simulateRoute(route_Summary));
        Marker_Variables m = new Marker_Variables();
        tomtomMap.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void onMarkerClicked(@NonNull Marker marker) {
                if(marker.isSelected())
                {
                    marker.deselect();
                }
                else
                {
                    marker.select();
                }
            }
        });

        //MyListData[] myListData2Array = new MyListData[myListData.size()];
        //myListData.toArray(myListData2Array);
        summaryAdapter = new MyListAdapter(resume.toArray(new MyListData[]{}));
        summaryAdapter.notifyDataSetChanged();
        Search_Variables searchVar = new Search_Variables();
        tomtomMap.moveCamera(searchVar.setCamera(geometry.get(0), 20, 0, 0));

    }



    //Esta interfaz básicamente actualiza la localización en el mapa cuando cambia la posición del usuario y setea la cámara al inicio basado en la localización
    OnLocationUpdateListener locationUpdateListener = new OnLocationUpdateListener() {
        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public void onLocationUpdate(GeoLocation geoLocation) {
            CurrentPosition = geoLocation.getPosition();
            Search_Variables searchVar = new Search_Variables();
            if (app_Start == true) {
                tomtomMap.moveCamera(searchVar.setCamera(geoPosition.getLastKnownLocation().getPosition(), 15, 0, 0));
                //searcher = searchBar(geoPosition.getLastKnownLocation().getPosition());
                //searcher.getView().bringToFront();
                //Esto que parece no tener sentido es para que no se abrá solo el teclado en pantalla cuando carga la aplicación, porque tenemos el buscador que hace un trigger automático al inicio
                //InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(findViewById(com.example.gps_test.R.id.search_fragment_container).getWindowToken(), 0);
                enableStartButton(ComenzarEvento); //Lo pongo acá porque en el onCreate todavía no conocemos la posición del gps y da error por nulo.
                app_Start = false;
            }
            if(navStart == true){
                int currentIndex = 0;
                int currentSegment = 0;
                TupleDouble currentClosePosition = null;
                Double closestDistanceToGps = 9999999999999.0;
                for (Curvaturas segment: instructions)
                {

                    /*for (TupleDouble pair: segment.getSegmentPoints())
                    {
                        if (closestDistanceToGps == 9999999999999.0)
                        {
                            closestDistanceToGps = distance(CurrentPosition.getLatitude(), pair.getLatitude(), CurrentPosition.getLongitude(), pair.getLongitude());
                            currentSegment =  currentIndex;
                            currentClosePosition = pair;
                        }
                        else
                        {
                            Double compareDistance = distance(CurrentPosition.getLatitude(), pair.getLatitude(), CurrentPosition.getLongitude(), pair.getLongitude());
                            if (closestDistanceToGps > compareDistance)
                            {
                                closestDistanceToGps = compareDistance;
                                currentSegment =  currentIndex;
                                currentClosePosition = pair;
                            }
                        }
                    }*/
                    if (closestDistanceToGps == 9999999999999.0)
                    {
                        closestDistanceToGps = distance(CurrentPosition.getLatitude(), segment.getSegmentPoints().get(0).getLatitude(), CurrentPosition.getLongitude(), segment.getSegmentPoints().get(0).getLongitude());
                        currentSegment =  currentIndex;
                        currentClosePosition = segment.getSegmentPoints().get(0);
                    }
                    /*else
                    {
                        Double compareDistance = distance(CurrentPosition.getLatitude(), segment.getSegmentPoints().get(0).getLatitude(), CurrentPosition.getLongitude(), segment.getSegmentPoints().get(0).getLongitude());
                        if (closestDistanceToGps > compareDistance)
                        {
                            closestDistanceToGps = compareDistance;
                            currentSegment =  currentIndex;
                            currentClosePosition = segment.getSegmentPoints().get(0);
                        }
                    }*/
                    else
                    {
                        Double compareDistance = distance(CurrentPosition.getLatitude(), segment.getSegmentPoints().get(0).getLatitude(), CurrentPosition.getLongitude(), segment.getSegmentPoints().get(0).getLongitude());
                        if (closestDistanceToGps > compareDistance || compareDistance < 20)
                        {
                            closestDistanceToGps = compareDistance;
                            currentSegment =  currentIndex;
                            currentClosePosition = segment.getSegmentPoints().get(0);
                        }
                    }
                    currentIndex ++;
                }

                if (distanciaRecorrida == 0 && distance(CurrentPosition.getLatitude(), instructions.get(0).getSegmentPoints().get(0).getLatitude(), CurrentPosition.getLongitude(), instructions.get(0).getSegmentPoints().get(0).getLongitude()) < 10)
                {
                    distanciaRecorrida = 1;
                    instructionsViewerDistance.setText(String.valueOf(distanciaRecorrida)+"m");
                }
                if (distanciaRecorrida > 0)
                {
                    distanciaRecorrida = distanciaRecorrida + distance(previousImmediatePosition.getLatitude(),CurrentPosition.getLatitude(),previousImmediatePosition.getLongitude(),CurrentPosition.getLongitude());
                    instructionsViewerDistance.setText(df.format(distanciaRecorrida)+"m");
                }
                previousImmediatePosition = CurrentPosition;

                if (instructions.get(currentSegment).getTurnDirection() == 1.0) {
                    if (instructions.get(currentSegment).getCurvatureAngle() < 50) {
                        //Glide.with(BuscarEventosMapaActivity.this).load(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_closed_left_24)).into(instructionsViewer);

                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_closed_left_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_closed_left_24));
                        }
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#fc9403"));
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                    }
                    else if (instructions.get(currentSegment).getCurvatureAngle() < 110) {
                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_left_black_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_left_black_24));
                        }
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#fce303"));
                    } else {
                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_slight_left_black_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_slight_left_black_24));
                        }
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#7bfc03"));
                    }
                } else if (instructions.get(currentSegment).getTurnDirection() == 2.0) {
                    if (instructions.get(currentSegment).getCurvatureAngle() < 50) {
                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_closed_right_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_closed_right_24));
                        }
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#fc9403"));
                    }
                    else if (instructions.get(currentSegment).getCurvatureAngle() < 110) {
                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_right_black_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_right_black_24));
                        }
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#fce303"));
                    } else {
                        if (instructionsViewer.getDrawable() != null) {
                            TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_slight_right_black_24)});
                            instructionsViewer.setImageDrawable(a);
                            a.setCrossFadeEnabled(true);
                            a.startTransition(1000);
                        }
                        else {
                            instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_turn_slight_right_black_24));
                        }
                        instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureAngle())+"°");
                        backgroundCard.setCardBackgroundColor(Color.parseColor("#7bfc03"));
                    }
                } else {
                    if (instructionsViewer.getDrawable() != null) {
                        TransitionDrawable a = new TransitionDrawable(new Drawable[]{instructionsViewer.getDrawable(), ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_north_black_24)});
                        instructionsViewer.setImageDrawable(a);
                        a.setCrossFadeEnabled(true);
                        a.startTransition(1000);
                    }
                    else {
                        instructionsViewer.setImageDrawable(ContextCompat.getDrawable(BuscarEventosMapaActivity.this, R.drawable.outline_north_black_24));
                    }
                    instructionsViewerExtraText.setText(df.format(instructions.get(currentSegment).getCurvatureLength())+"m");
                    backgroundCard.setCardBackgroundColor(Color.parseColor("#07fc03"));
                }


                if (distance(CurrentPosition.getLatitude(), meta.getLatitude(), CurrentPosition.getLongitude(), meta.getLongitude()) < 30)
                {
                    abandono = false;
                    TerminarEvento.performClick();
                }
            }
        }
    };

    @SuppressLint("UnsafeOptInUsageError")
    public static void focusCamera(int index)
    {
        Search_Variables searchVar = new Search_Variables();
        List<GeoPoint> a = new TupleDouble().listTupleTolistGeo(currentApiDrawRoad.get(index).getSegmentPoints());
        Routing_Variables route = new Routing_Variables();
        tomtomMap.moveCamera(searchVar.setCamera(a.get(a.size()/2), 20, 0, 0));
        com.tomtom.sdk.map.display.route.Route reference = tomtomMap.addRoute(route.setRouteAlt(a));
        summaryReferences.add(reference.getGeometry());
        routeSummaryDialog.dismiss();
    }



    //Acá se implementa lo relacionado con la barra de direcciones y sus resultados
    private SearchFragment searchBar(GeoPoint position) {
        Search_Variables searchVar = new Search_Variables();
        @SuppressLint("UnsafeOptInUsageError") SearchFragment searchFragment = SearchFragment.newInstance(searchVar.setSearchProperties(position, Locale.US, 10));
        searchFragment.setMenuVisibility(false);
        getSupportFragmentManager().beginTransaction().replace(com.example.gps_test.R.id.search_fragment_container, searchFragment).commitNow();
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(com.example.gps_test.R.id.search_fragment_container).getWindowToken(), 0);
        searchFragment.getView().clearFocus();

        SearchFragmentListener listener = new SearchFragmentListener() {
            @Override
            public void onSearchBackButtonClick() {

            }

            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onSearchResultClick(@NonNull PlaceDetails placeDetails) {
                tomtomMap.moveCamera(searchVar.setCamera(placeDetails.getPosition(), 15, 0, 0));
                searchFragment.clear();
                //Esto que parece no tener sentido es para que no se abrá solo el teclado en pantalla cuando carga la aplicación, porque tenemos el buscador que hace un trigger automático al inicio
                InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(com.example.gps_test.R.id.search_fragment_container).getWindowToken(), 0);
            }

            @Override
            public void onSearchError(@NonNull Throwable throwable) {

            }


            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onSearchQueryChanged(@NonNull String s) {

            }


            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCommandInsert(@NonNull String s) {

            }
        };
        Search searchApi = OnlineSearch.create(getApplicationContext(), com.example.gps_test.BuildConfig.CREDENTIALS_KEY, null, null);
        searchFragment.setSearchApi(searchApi);
        searchFragment.setFragmentListener(listener);
        return searchFragment;
    }



    //Calcular punto entre dos puntos expresados en longitud y latitud
    public static Pair<Double, Double> intermediatePoints(double lat, double lon, double bearing, double distance)
    {
        double R = 6371000;

        // convert to radians
        double lat1 = Math.toRadians(lat);
        double lon1 = Math.toRadians(lon);
        double angleBearing = Math.toRadians(360 - bearing);

        double latDest = Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(angleBearing));

        double lonDest = lon1 + Math.atan2(Math.sin(angleBearing) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(latDest));

        return Pair.create(Math.toDegrees(latDest), Math.toDegrees(lonDest));
    }


    //Calcular puntos (X,Y) en base a longitud y latitud
    public static Triple<Double, Double, Double> coordinates(double lat1, double lon1) {
        lon1 = Math.toRadians(lon1);
        lat1 = Math.toRadians(lat1);
        //radio de la tierra en metros
        double R = 6371000;
        Triple<Double, Double, Double> points;
        double x = R * Math.cos(lat1) * Math.cos(lon1);
        double y = R * Math.cos(lat1) * Math.sin(lon1);
        double z = R * Math.sin(lat1);

        points = new Triple<>(x, y, z);
        return points;
    }

    //Calcular triangulo y sus angulos (para punto de giro)
    public static Triple<Double, Double, Double> triangleAngles(List<Pair<Double, Double>> coordinates, Double radius)
    {
        Double x1 = coordinates.get(0).first;
        Double y1 = coordinates.get(0).second;
        Double x2 = coordinates.get(1).first;
        Double y2 = coordinates.get(1).second;
        Double x3 = coordinates.get(2).first;
        Double y3 = coordinates.get(2).second;



        //Sides length
        double a = Math.sqrt((x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3)); //Vector P2 a P3
        double b = Math.sqrt((x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3));//No nos hace falta a menos que busquemos triangulos no equilateros
        double c = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); //Vector P1 a P2

        // Compute three angles
        double A = Math.toDegrees(Math.acos((a * a - b * b - c * c) / (-2 * b * c)));//Primer angulo (entre P1 y P3)
        double B = Math.toDegrees(Math.acos((b * b - a * a - c * c) / (-2 * a * c)));//Segundo angulo (entre P2 y P3)
        double C = Math.toDegrees(Math.acos((c * c - b * b - a * a) / (-2 * a * b)));//Tercer angulo (entre P3 y P1)

        return new Triple<>(A, B, C);
    }

    //Calcular la distancia en metros entre dos puntos mediante latitud y longitud
    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        //double r = 6371;
        //Lo uso en metros
        double r = 6371000;

        // calculate the result
        return(c * r);
    }




    @Override
    public void onBackPressed() {
        finish();
    }

    //Esto debería ser para el usuario, cuando se abré por primera vez la app que solicite todos los permisos necesarios o se cierre pero es solo un place holder no implemente eso todavía
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }




}