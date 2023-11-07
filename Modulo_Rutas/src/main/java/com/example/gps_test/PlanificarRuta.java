package com.example.gps_test;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gps_test.ui.ImageUploaders.ImageUploader;
import com.example.gps_test.ui.map.Curvaturas;
import com.example.gps_test.ui.map.Routing_Variables;
import com.example.gps_test.ui.map.TupleDouble;
import com.example.gps_test.ui.recyclerView.MyListAdapter;
import com.example.gps_test.ui.recyclerView.MyListData;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;
import com.tomtom.sdk.location.GeoLocation;
import com.tomtom.sdk.location.GeoPoint;
import com.tomtom.sdk.location.LocationProvider;

import com.tomtom.sdk.location.OnLocationUpdateListener;
import com.tomtom.sdk.location.android.AndroidLocationProvider;
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig;
import com.tomtom.sdk.map.display.gesture.MapLongClickListener;
import com.tomtom.sdk.map.display.image.ImageFactory;
import com.tomtom.sdk.map.display.marker.Marker;
import com.tomtom.sdk.map.display.marker.MarkerClickListener;
import com.tomtom.sdk.map.display.marker.MarkerLongClickListener;
import com.tomtom.sdk.map.display.route.Instruction;
import com.tomtom.sdk.map.display.route.RouteClickListener;
import com.tomtom.sdk.map.display.TomTomMap;
import com.tomtom.sdk.map.display.ui.MapFragment;
import com.tomtom.sdk.map.display.ui.MapReadyCallback;
import com.tomtom.sdk.map.display.ui.MapView;
import com.example.gps_test.ui.map.Location_Variables;
import com.example.gps_test.ui.map.Search_Variables;
import com.example.gps_test.ui.map.Marker_Variables;
import com.tomtom.sdk.routing.RoutePlanner;
import com.tomtom.sdk.routing.RoutePlanningCallback;
import com.tomtom.sdk.routing.RoutePlanningResponse;
import com.tomtom.sdk.routing.RoutingFailure;
import com.tomtom.sdk.routing.online.OnlineRoutePlanner;
import com.tomtom.sdk.routing.route.Route;
import com.tomtom.sdk.routing.route.RouteLeg;
import com.tomtom.sdk.routing.route.instruction.roundabout.ExitRoundaboutInstruction;
import com.tomtom.sdk.routing.route.instruction.roundabout.RoundaboutInstruction;
import com.tomtom.sdk.routing.route.instruction.turn.TurnInstruction;
import com.tomtom.sdk.routing.route.section.Sections;
import com.tomtom.sdk.search.*;
import com.tomtom.sdk.search.online.OnlineSearch;
import com.tomtom.sdk.search.ui.SearchFragment;
import com.tomtom.sdk.search.ui.SearchFragmentListener;
import com.tomtom.sdk.search.ui.model.PlaceDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kotlin.Triple;


public class PlanificarRuta extends AppCompatActivity {

    private MapView mapView;
    private static TomTomMap tomtomMap;
    private LocationProvider geoPosition;

    private GeoPoint CurrentPosition;

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

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refRoutes = database.getReference().child("Route");
    DatabaseReference refPublicRoutes = database.getReference().child("PublicRoute");
    private Thread secondary_Thread_Reverse_Geocoding;
    private boolean stopThread = false;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private Bitmap screenshot;
    private ImageButton button;
    private ImageButton button2;




    @SuppressLint({"UnsafeOptInUsageError", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.gps_test.R.layout.planificar_ruta_main);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        mapFragment.getMapAsync(mapReady);

        //place holder button, must be changed later to context menu
        button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeOptInUsageError")
            public void onClick(View v) {
                Routing_Variables route = new Routing_Variables();
                if (route_Points.size() >= 2) {
                    tomtomMap.addRoute(route.setRoute(route_Points));
                    tomtomMap.removeMarkers("Free Marker");
                    button.setEnabled(false);
                    button.setImageAlpha(75);
                    button2.setEnabled(false);
                    button2.setImageAlpha(75);
                }
            }
        });

        button2 = (ImageButton) findViewById(R.id.imageButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeOptInUsageError")
            public void onClick(View v) {
                Routing_Variables route = new Routing_Variables();
                if (online_Route_Points.size() >= 2 && tomtomMap.getRoutes().size() == 0 ) {
                    stopThread = false;
                    hashListRequestsCounter = new ArrayList<>();
                    routePlanner(online_Route_Points);
                    tomtomMap.removeMarkers("Route Marker");
                    button.setEnabled(false);
                    button.setImageAlpha(75);
                    button2.setEnabled(false);
                    button2.setImageAlpha(75);
                }
            }
        });

        FloatingActionButton OpenBottomSheet = findViewById(R.id.openRouteSummary);
        OpenBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        PlanificarRuta.this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
                View bottomSheetView = LayoutInflater.from(PlanificarRuta.this)
                        .inflate(R.layout.bottom_sheet_layout,
                                (LinearLayout)findViewById(R.id.modalBottomSheetContainer));
                route_Instructions = bottomSheetView;
                RecyclerView recyclerView = (RecyclerView) route_Instructions.findViewById(R.id.routeSummary2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(PlanificarRuta.this));
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

        FloatingActionButton DeletePoints = findViewById(R.id.deleteRoutePoints);
        DeletePoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondary_Thread_Reverse_Geocoding.interrupt();
                stopThread = true;
                hashListRequestsCounter.clear();
                route_Points.clear();
                online_Route_Points.clear();
                tomtomMap.removeRoutes();
                tomtomMap.clear();
                route_Summary = null;
                summaryReferences.clear();
                summaryAdapter.clearData();
                tomtomMap.removeMarkers("Route Marker");
                tomtomMap.removeMarkers("Free Marker");
                button.setEnabled(true);
                button.setImageAlpha(255);
                button2.setEnabled(true);
                button2.setImageAlpha(255);
            }
        });


        FloatingActionButton SavePoints = findViewById(R.id.saveRoutePoints);
        Dialog dialog = new Dialog(PlanificarRuta.this);
        refRoutes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ruta post = dataSnapshot.getValue(Ruta.class);
                System.out.println(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        SavePoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.input_name_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                TextView okay_text, cancel_text;
                okay_text = dialog.findViewById(R.id.okay_text);
                cancel_text = dialog.findViewById(R.id.cancel_text);

                okay_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditText name = (EditText)dialog.findViewById(R.id.editTextText);
                        if (!name.getText().toString().equals("") & current_Route_Points.size() > 0)
                        {
                            Intent intent = getIntent();
                            String close = intent.getStringExtra("Close_On_Enter");
                            String id = intent.getStringExtra("User_ID");


                            if (close.equals("False")) {
                                Ruta ruta = new Ruta(id, name.getText().toString(), 0,
                                        listGeoTolistPair(current_Route_Points),
                                        route_Summary.getSummary().getLength().inMeters(), curvesAmount,
                                        startLocation, finishLocation, "Imagén Pendiente",
                                        Arrays.asList(summaryAdapter.getCurrentList()), currentApiDrawRoad);

                                String baseID = String.valueOf(current_Route_Points.hashCode() + java.util.Calendar.getInstance().getTime().hashCode());
                                while (baseID.equals("Infinity") || baseID.equals("Inf") || baseID.equals("-Infinity") || baseID.equals("-Inf") || baseID.equals("NaN"))
                                {
                                    baseID = String.valueOf(current_Route_Points.hashCode() + java.util.Calendar.getInstance().getTime().hashCode());
                                }
                                refRoutes.child(baseID).setValue(ruta);
                                refPublicRoutes.child(baseID).setValue(ruta);

                                mapFragment.getScaleView().setVisible(false);
                                mapFragment.getZoomControlsView().setVisible(false);
                                String finalBaseID = baseID;
                                Instacapture.INSTANCE.capture(PlanificarRuta.this, new SimpleScreenCapturingListener() {
                                    @Override
                                    public void onCaptureComplete(Bitmap bitmap) {
                                        ImageUploader uploadMapScreenShot = new ImageUploader();
                                        uploadMapScreenShot.cargar_bytes(id, bitmap, finalBaseID);
                                        mapFragment.getScaleView().setVisible(true);
                                        mapFragment.getZoomControlsView().setVisible(true);
                                    }
                                },button,button2,SavePoints,DeletePoints,OpenBottomSheet, searcher.getView());

                                Instacapture.INSTANCE.enableLogging(true);
                                //ImageUploader uploadMapScreenShot = new ImageUploader();

                                // Initialize the MediaProjectionManager
                                // mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                                // Start a screen capture session
                                //Intent captureIntent = mProjectionManager.createScreenCaptureIntent();
                                //captureIntent.putExtra("User_ID", id);
                                //captureIntent.putExtra("Route_ID", baseID);
                                //takeScreenshotLauncher.launch(captureIntent);
                                //uploadMapScreenShot.cargar_bytes(id, captureScreen(),baseID);
                                //uploadMapScreenShot.cargar_bytes(id, takeScreenshot(findViewById(R.id.map_fragment)),baseID);
                            }
                            else
                            {
                                Ruta ruta = new Ruta(id, name.getText().toString(), 0,
                                        listGeoTolistPair(current_Route_Points),
                                        route_Summary.getSummary().getLength().inMeters(), curvesAmount,
                                        startLocation, finishLocation, "Imagén Pendiente",
                                        Arrays.asList(summaryAdapter.getCurrentList()), currentApiDrawRoad);

                                String baseID = String.valueOf(current_Route_Points.hashCode() + java.util.Calendar.getInstance().getTime().hashCode());
                                refRoutes.child(baseID).setValue(ruta);
                                refPublicRoutes.child(baseID).setValue(ruta);

                                mapFragment.getScaleView().setVisible(false);
                                mapFragment.getZoomControlsView().setVisible(false);
                                Instacapture.INSTANCE.capture(PlanificarRuta.this, new SimpleScreenCapturingListener() {
                                    @Override
                                    public void onCaptureComplete(Bitmap bitmap) {
                                        ImageUploader uploadMapScreenShot = new ImageUploader();
                                        uploadMapScreenShot.cargar_bytes(id, bitmap,baseID);
                                        mapFragment.getScaleView().setVisible(true);
                                        mapFragment.getZoomControlsView().setVisible(true);
                                    }
                                },button,button2,SavePoints,DeletePoints,OpenBottomSheet, searcher.getView());


                                Instacapture.INSTANCE.enableLogging(true);

                                //ImageUploader uploadMapScreenShot = new ImageUploader();
                                // Initialize the MediaProjectionManager
                                //mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                                // Start a screen capture session
                                //Intent captureIntent = mProjectionManager.createScreenCaptureIntent();
                                //captureIntent.putExtra("User_ID", id);
                                //captureIntent.putExtra("Route_ID", baseID);
                                //takeScreenshotLauncher.launch(captureIntent);
                                //uploadMapScreenShot.cargar_bytes(id, takeScreenshotLauncher.launch(captureIntent),baseID);
                                //uploadMapScreenShot.cargar_bytes(id, takeScreenshot(findViewById(R.id.map_fragment)),baseID);

                                //Si queremos retornar resultados
                                //Intent data = new Intent();
                                //data.putExtra("key","any_value");
                                //setResult(RESULT_OK,data);
                                finish();
                            }

                        }
                    }
                });

                cancel_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        });



        Intent intent = getIntent();
        String removeControls = intent.getStringExtra("Invalidate_Controls");
        if (removeControls != null && removeControls.equals("True"))
        {
            button.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            SavePoints.setVisibility(View.GONE);
            DeletePoints.setVisibility(View.GONE);
        }


    }




    private Bitmap imageToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(
                image.getWidth() + rowPadding / pixelStride,
                image.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }


    public Bitmap takeScreenshot(View view)
    {
        view.setDrawingCacheEnabled(true);
        view.setBackgroundColor(Color.TRANSPARENT);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        view.draw(canvas);

        //mapFragment.getScaleView().setVisible(false);
        //mapFragment.getZoomControlsView().setVisible(false);
        //findViewById(R.id.search_fragment_container).setVisibility(View.GONE);

        return view.getDrawingCache();
    }

    public Bitmap takeScreen()
    {
        // Get device dimmensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get root view
        View view = findViewById(R.id.map_fragment);
        // Create the bitmap to use to draw the screenshot
        final Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);

        // Get current theme to know which background to use
        final Activity activity = PlanificarRuta.this;
        final Resources.Theme theme = activity.getTheme();
        final TypedArray ta = theme
                .obtainStyledAttributes(new int[] { android.R.attr.windowBackground });
        final int res = ta.getResourceId(0, 0);
        final Drawable background = activity.getResources().getDrawable(res);

        // Draw background
        background.draw(canvas);

        // Draw views
        view.draw(canvas);

        return bitmap;

    }
    public List<GeoPoint> listPairTolistGeo(List<TupleDouble> ev) {

        List<GeoPoint> a = new ArrayList<>();

        for (int i = 0; i < ev.size(); i++) {
            a.add(new GeoPoint(ev.get(i).getLatitude(), ev.get(i).getLongitude()));
        }
        return a;
    }

    public List<TupleDouble> listGeoTolistPair(List<GeoPoint> ev) {

        List<TupleDouble> a = new ArrayList<>();

        for (int i = 0; i < ev.size(); i++) {
            a.add(new TupleDouble(ev.get(i).getLatitude(), ev.get(i).getLongitude()));
        }
        return a;
    }



    //Este evento nos permite obtener las coordenadas de pantalla que toca el usuario, no es relevante para el mapa o los marcadores, si para donde queremos que aparezcan controles como menús secundarios
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            touchPosition.add(ev.getX());
            touchPosition.add(ev.getY());
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
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
            tomTomMap.addMapLongClickListener(onMapLongClick);
            tomTomMap.addMarkerLongClickListener(onMarkerLongClick);

            //Acá lo único que hago es asignar las variables para que sean accesibles al resto del formulario, tomtomMap es el mapa, geoPosition es una clase que maneja la localización con android.
            tomtomMap = tomTomMap;
            tomtomMap.addRouteClickListener(routeListener);
            geoPosition = locationProvider;
            geoPosition.addOnLocationUpdateListener(locationUpdateListener);

            Intent intent = getIntent();
            String removeControls = intent.getStringExtra("Invalidate_Controls");
            if (removeControls != null && removeControls.equals("True"))
            {
                loadRouteOnMap((List<TupleDouble>) intent.getSerializableExtra("List_Of_Points"));
            }
        }
    };

    private void routePlanner(List<GeoPoint> position) {
        Routing_Variables helper = new Routing_Variables();
        RoutePlanner routePlanner = OnlineRoutePlanner.create(getApplicationContext(), BuildConfig.CREDENTIALS_KEY, null);
        routePlanner.planRoute(helper.setOnlineRoute(position), new RoutePlanningCallback() {
            @Override
            public void onRoutePlanned(Route route) {
                route_Summary = route;
            }

            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onSuccess(RoutePlanningResponse routePlanningResponse) {
                Sections k = routePlanningResponse.getRoutes().get(0).getSections();
                current_Route_Points = routePlanningResponse.getRoutes().get(0).getGeometry();
                displayed_Route = tomtomMap.addRoute(helper.setRoute(routePlanningResponse.getRoutes().get(0).getGeometry()));
                setRouteInstructions(route_Summary, displayed_Route, 10, helper);
                //tomtomMap.setLocationProvider(helper.simulateRoute(route_Summary));
//                Marker_Variables m = new Marker_Variables();
//                for (int i = 0; i < routePlanningResponse.getRoutes().get(0).getGeometry().size(); i++) {
//                    tomtomMap.addMarker(m.setMarkerOptions(routePlanningResponse.getRoutes().get(0).getGeometry().get(i), ImageFactory.INSTANCE.fromResource(R.drawable.outline_add_location_alt_black_18), "Free Marker"));
//                }
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
                List<Curvaturas> data = curvatureTrim(curvature(routePlanningResponse.getRoutes().get(0).getGeometry()));
                summaryAdapter = loadList2(data);
                APIRequestsCounter = 0;

                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        markerLocationDataAPI2(data);
                    }
                });
                secondary_Thread_Reverse_Geocoding = t1;
                t1.start();


                List<RouteLeg> instructions = routePlanningResponse.getRoutes().get(0).getLegs();
                for (int i = 0; i < instructions.size(); i++) {
                    for (int j = 0; j < instructions.get(i).getInstructions().size(); j++) {
                        String name = instructions.get(i).getInstructions().get(j).getClass().getSimpleName();
                        switch (name) {
                            case "TurnInstruction":
                                TurnInstruction t = (TurnInstruction) instructions.get(i).getInstructions().get(j);
                                System.out.println(t.getClass().getSimpleName()
                                        + " " + t.getManeuverPoint().toString() + " " + t.getRouteOffset()
                                        + " Calle Actual:" + t.getNextSignificantRoad() + " Giro en grados:" + t.getTurnAngle()
                                );
                                break;

                            case "RoundaboutInstruction":
                                RoundaboutInstruction r = (RoundaboutInstruction) instructions.get(i).getInstructions().get(j);
                                System.out.println(r.getClass().getSimpleName()
                                        + " " + r.getManeuverPoint().toString() + " " + r.getRouteOffset()
                                        + " Calle Actual:" + r.getNextSignificantRoad() + " Giro en grados:" + r.getTurnAngle()
                                );
                                break;

                            case "ExitRoundaboutInstruction":
                                ExitRoundaboutInstruction e = (ExitRoundaboutInstruction) instructions.get(i).getInstructions().get(j);
                                System.out.println(e.getClass().getSimpleName()
                                        + " " + e.getManeuverPoint().toString() + " " + e.getRouteOffset()
                                        + " Calle Actual:" + e.getNextSignificantRoad() + " Giro en grados:" + e.getTurnAngle()
                                );
                                break;

                            default:
                                System.out.println(instructions.get(i).getInstructions().get(j).getClass().getSimpleName()
                                        + " " + instructions.get(i).getInstructions().get(j).getManeuverPoint().toString() + " " + instructions.get(i).getInstructions().get(j).getRouteOffset()
                                        + " Calle Actual:" + instructions.get(i).getInstructions().get(j).getNextSignificantRoad()
                                );
                        }

                    }
                }

            }

            @Override
            public void onFailure(RoutingFailure routingFailure) {
                String a = routingFailure.getMessage();
                System.out.println(a);
            }
        });
    }

    private void setRouteInstructions(Route route, com.tomtom.sdk.map.display.route.Route display, int instructions_Number, Routing_Variables helper) {
        double distance = route.getSummary().getLength().inMeters();
        double instructions_Per_Distance = distance / instructions_Number;
        List<Instruction> instructions = new ArrayList<Instruction>();

        for (int i = 0; i < instructions_Number; i++) {
            instructions.add(helper.setInstruction(instructions_Per_Distance * (i + 1), 10.0));
        }
        display.setInstructions(instructions);

    }

    //Acá se agrega funcionalidad que queremos que ocurra cuando se hace un click largo sobre el mapa
    MapLongClickListener onMapLongClick = new MapLongClickListener() {
        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public boolean onMapLongClicked(@NonNull GeoPoint geoPoint) {
            if (tomtomMap.getRoutes().size() == 0) {
                if (ignoreLongClick == false) {
                    Marker_Variables m = new Marker_Variables();
                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenuOverlapAnchor);
                    //PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.map_fragment), Gravity.NO_GRAVITY, androidx.appcompat.R.attr.actionOverflowMenuStyle, 0);
                    //PopupMenu popup = new PopupMenu(contextThemeWrapper, findViewById(R.id.map_fragment), Gravity.END);

                    showPopup(PlanificarRuta.this, touchPosition.get(0), touchPosition.get(1), m, geoPoint);
                    touchPosition.clear();
                } else {
                    ignoreLongClick = false;
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Ya existe una ruta en el mapa, borre la misma para agregar una nueva", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    };


    //Este procedimiento permite mostrar un popup cerca de la región de la pantalla donde se hizo click
    public void showPopup(Activity activity, float x, float y, Marker_Variables m, GeoPoint geoPoint) {
        final ViewGroup root = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        final View view = new View(getApplicationContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        view.setBackgroundColor(Color.TRANSPARENT);

        root.addView(view);

        view.setX((float) (x - x * 0.12));
        view.setY((float) (y - y * 0.12));

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, view, Gravity.CENTER);
        popupMenu.getMenuInflater().inflate(R.menu.poupup_menu, popupMenu.getMenu());
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                root.removeView(view);
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals("Free Marker")) {
                    tomtomMap.addMarker(m.setMarkerOptions(geoPoint, ImageFactory.INSTANCE.fromResource(R.drawable.location_on_black_48dp), "Free Marker"));
                    route_Points.add(geoPoint);
                } else {
                    tomtomMap.addMarker(m.setMarkerOptions(geoPoint, ImageFactory.INSTANCE.fromResource(R.drawable.navigation_black_48dp), "Route Marker"));
                    online_Route_Points.add(geoPoint);
                }
                return true;
            }
        });

        popupMenu.show();
    }

    //Interfaz que es llamada cuando se produce un click largo sobre un marcador del mapa
    MarkerLongClickListener onMarkerLongClick = new MarkerLongClickListener() {
        @Override
        public void onMarkerLongClicked(@NonNull Marker marker) {
            if (marker.getTag().equals("Free Marker")) {
                route_Points.remove(marker.getCoordinate());
                marker.remove();
            } else {
                online_Route_Points.remove(marker.getCoordinate());
                marker.remove();
            }
            //Esto sirve para que el mapa debajo no haga ninguna acción ya que también recibe el click
            ignoreLongClick = true;
        }
    };

    //Esta interfaz básicamente actualiza la localización en el mapa cuando cambia la posición del usuario y setea la cámara al inicio basado en la localización
    OnLocationUpdateListener locationUpdateListener = new OnLocationUpdateListener() {
        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public void onLocationUpdate(GeoLocation geoLocation) {
            CurrentPosition = geoLocation.getPosition();
            Search_Variables searchVar = new Search_Variables();
            if (app_Start = true) {
                tomtomMap.moveCamera(searchVar.setCamera(geoPosition.getLastKnownLocation().getPosition(), 15, 0, 0));
                searcher = searchBar(geoPosition.getLastKnownLocation().getPosition());
                //searcher.getView().bringToFront();
                //Esto que parece no tener sentido es para que no se abrá solo el teclado en pantalla cuando carga la aplicación, porque tenemos el buscador que hace un trigger automático al inicio
                //InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(findViewById(R.id.search_fragment_container).getWindowToken(), 0);
                app_Start = false;
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

    @SuppressLint("UnsafeOptInUsageError")
    public void loadRouteOnMap(List<TupleDouble> points) {
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
        List<Curvaturas> data = curvatureTrim(curvature(geometry));
        summaryAdapter = loadList2(data);
        APIRequestsCounter = 0;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                markerLocationDataAPI2(data);
            }
        });
        t1.start();

        Search_Variables searchVar = new Search_Variables();
        tomtomMap.moveCamera(searchVar.setCamera(geometry.get(0), 20, 0, 0));


    }
    static RouteClickListener routeListener = new RouteClickListener() {
        @Override
        public void onRouteClick(@NonNull com.tomtom.sdk.map.display.route.Route route) {
            if(summaryReferences.contains(route.getGeometry())) {
                summaryReferences.remove(route.getGeometry());
                route.remove();
            }
        }
    };


    //Acá se implementa lo relacionado con la barra de direcciones y sus resultados
    private SearchFragment searchBar(GeoPoint position) {
        Search_Variables searchVar = new Search_Variables();
        @SuppressLint("UnsafeOptInUsageError") SearchFragment searchFragment = SearchFragment.newInstance(searchVar.setSearchProperties(position, Locale.US, 10));
        getSupportFragmentManager().beginTransaction().replace(R.id.search_fragment_container, searchFragment).commitNow();
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
                imm.hideSoftInputFromWindow(findViewById(R.id.search_fragment_container).getWindowToken(), 0);
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
        Search searchApi = OnlineSearch.create(getApplicationContext(), BuildConfig.CREDENTIALS_KEY, null, null);
        searchFragment.setSearchApi(searchApi);
        searchFragment.setFragmentListener(listener);
        return searchFragment;
    }

    //Determinar la curvatura en radios (se mide en metros)
    public static List<Curvaturas> curvature(List<GeoPoint> route) {
        double sideA = 0;
        double sideB = 0;
        double sideC = 0;
        double angle1 = 0;
        double angle2 = 0;
        double angle3 = 0;
        double radius = 0;
        Pair<Double, Double> newValues1;
        Pair<Double, Double> newValues21 = Pair.create(0.0,0.0);
        Pair<Double, Double> newValues22 = Pair.create(0.0,0.0);
        List<Pair<Double, Double>> j = new ArrayList<>();
        List<Curvaturas> finalList = new ArrayList<>();
        boolean enteredNewValue21 = false;

        for (int i = 0; i < route.size(); i++) {
            if ((route.size() > 2) && (route.get(i) != route.get(route.size() - 2))) {
                //Acá calculamos el radio de cada punto intermedio
                if (newValues21.first == 0.0) {
                    sideA = distance(route.get(i).getLatitude(), route.get(i + 1).getLatitude(), route.get(i).getLongitude(), route.get(i + 1).getLongitude());
                    sideB = distance(route.get(i + 1).getLatitude(), route.get(i + 2).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLongitude());
                    sideC = distance(route.get(i).getLatitude(), route.get(i + 2).getLatitude(), route.get(i).getLongitude(), route.get(i + 2).getLongitude());

                    radius = (sideA * sideB * sideC) / Math.sqrt((sideA + sideB + sideC) * (sideB + sideC - sideA) * (sideC + sideA - sideB) * (sideA + sideB - sideC));

                    //Acá calculamos hacia que lado va la curvatura del segmento
                    angle1 = angleFromCoordinate(route.get(i).getLatitude(), route.get(i).getLongitude(), route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude());
                    angle2 = angleFromCoordinate(route.get(i).getLatitude(), route.get(i).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                    angle3 = angleFromCoordinate(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                }
                else
                {
                    sideA = distance(newValues21.first, route.get(i + 1).getLatitude(), newValues21.second, route.get(i + 1).getLongitude());
                    sideB = distance(route.get(i + 1).getLatitude(), route.get(i + 2).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLongitude());
                    sideC = distance(newValues21.first, route.get(i + 2).getLatitude(), newValues21.second, route.get(i + 2).getLongitude());

                    radius = (sideA * sideB * sideC) / Math.sqrt((sideA + sideB + sideC) * (sideB + sideC - sideA) * (sideC + sideA - sideB) * (sideA + sideB - sideC));

                    angle1 = angleFromCoordinate(newValues21.first, newValues21.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude());
                    angle2 = angleFromCoordinate(newValues21.first, newValues21.second, route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                    angle3 = angleFromCoordinate(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                    enteredNewValue21 = true;
                    newValues21 = Pair.create(0.0, 0.0);
                }
                double finalAngle = 0;
                int side = 0;

                if (radius < 175) //&& max * 0.95 > min
                    {
                    if (angle1 > angle2) {
                        side = 1;
                    } else {
                        side = 2;
                    }

                    if(sideA > 25 || sideB > 25)
                    {
                        if (sideA > 25 && sideB > 25) {
                            double angleX = angleFromCoordinate360(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i).getLatitude(), route.get(i).getLongitude());
                            angle3 = angleFromCoordinate360(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                            newValues1 = intermediatePoints(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), angleX, 20);
                            newValues21 = intermediatePoints(route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), angle3, 20);
                            newValues22 = intermediatePoints(route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), angle3, 20);
                            if (i > 0) {
                                finalList.set(finalList.size() - 1, recalculateValues(route.get(i - 1).getLatitude(), route.get(i - 1).getLongitude(), route.get(i).getLatitude(), route.get(i).getLongitude(), newValues1.first, newValues1.second));
                                finalList.add(recalculateValues(newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), newValues21.first, newValues21.second));
                                continue;
                            } else {
                                finalList.add(recalculateValues(route.get(i).getLatitude(), route.get(i).getLongitude(), newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()));
                                finalList.add(recalculateValues(newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), newValues21.first, newValues21.second));
                                continue;
                            }
                        }
                        if (sideA > 25) {
                            double angleX = angleFromCoordinate360(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i).getLatitude(), route.get(i).getLongitude());
                            newValues1 = intermediatePoints(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), angleX, 20);
                            if (i > 0) {
                                finalList.set(finalList.size() - 1, recalculateValues(route.get(i - 1).getLatitude(), route.get(i - 1).getLongitude(), route.get(i).getLatitude(), route.get(i).getLongitude(), newValues1.first, newValues1.second));
                                finalList.add(recalculateValues(newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()));
                                continue;
                            } else {
                                finalList.add(recalculateValues(route.get(i).getLatitude(), route.get(i).getLongitude(), newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()));
                                finalList.add(recalculateValues(newValues1.first, newValues1.second, route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()));
                                continue;
                            }
                        }
                        else
                        {
                            angle3 = angleFromCoordinate360(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude());
                            newValues21 = intermediatePoints(route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), angle3, 20);
                            newValues22 = intermediatePoints(route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), angle3, 20);
                            finalList.add(recalculateValues(route.get(i).getLatitude(), route.get(i).getLongitude(), route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude(), newValues21.first, newValues21.second));
                            continue;
                        }
                    }

                    Pair<Double, Double> one = Pair.create(coordinates(route.get(i).getLatitude(), route.get(i).getLongitude()).getFirst(), coordinates(route.get(i).getLatitude(), route.get(i).getLongitude()).getThird());
                    Pair<Double, Double> two = Pair.create(coordinates(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()).getFirst(), coordinates(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()).getThird());
                    Pair<Double, Double> three = Pair.create(coordinates(route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()).getFirst(), coordinates(route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()).getThird());
                    j.addAll(Arrays.asList(one, two, three));
                    Triple<Double, Double, Double> g = triangleAngles(j, radius);
                    finalAngle = g.getSecond();
                    j.clear();



                }
                String tipo = "";

                if(finalAngle == 0) {
                    tipo = "Línea Recta";
                } else if (finalAngle < 25) {
                    tipo = "Giro muy Cerrado";
                } else if (finalAngle < 50) {
                    tipo = "Giro Cerrado";
                } else if (finalAngle < 110) {
                    tipo = "Giro Regular";
                } else {
                    tipo = "Giro Suave";
                }

                if (enteredNewValue21 == true) {
                    finalList.add(new Curvaturas(radius, finalAngle, new ArrayList<TupleDouble>(Arrays.asList(new TupleDouble(newValues22.first, newValues22.second), new TupleDouble(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()), new TupleDouble(route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()))), side, tipo, sideA + sideB));
                    enteredNewValue21 = false;
                }
                else
                {
                    finalList.add(new Curvaturas(radius, finalAngle, new ArrayList<TupleDouble>(Arrays.asList(new TupleDouble(route.get(i).getLatitude(), route.get(i).getLongitude()), new TupleDouble(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()), new TupleDouble(route.get(i + 2).getLatitude(), route.get(i + 2).getLongitude()))), side, tipo, sideA + sideB));
                }
            } else
            {
                if (finalList.get(finalList.size()-1).getSegmentPoints().get(finalList.get(finalList.size()-1).getSegmentPoints().size()-1).equals(route.get(route.size()-1)))
                {
                    break;
                }
                else
                {
                    TupleDouble start = finalList.get(finalList.size()-1).getSegmentPoints().get(finalList.get(finalList.size()-1).getSegmentPoints().size()-1);
                    GeoPoint finish = route.get(route.size()-1);
                    double newDistance = distance(start.getLatitude(), finish.getLatitude(), start.getLongitude(), finish.getLongitude());
                    double angleX = angleFromCoordinate360(start.getLatitude(), start.getLongitude(), finish.getLatitude(), finish.getLongitude());
                    Pair<Double, Double> intermediatePoint = intermediatePoints(start.getLatitude(), start.getLongitude(), angleX, newDistance/2);
                    finalList.add(new Curvaturas(0, 0, new ArrayList<TupleDouble>(Arrays.asList(finalList.get(finalList.size()-1).getSegmentPoints().get(finalList.get(finalList.size()-1).getSegmentPoints().size()-1), new TupleDouble(route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()), new TupleDouble(route.get(route.size()-1).getLatitude(), route.get(route.size()-1).getLongitude()))), 0, "Línea Recta", newDistance));
                    break;
                }
            }
        }

        return finalList;
    }

    public static Curvaturas recalculateValues(double lat1, double lon1, double lat2, double lon2, double lat3, double lon3)
    {
        List<Pair<Double, Double>> j = new ArrayList<>();

        double sideA = distance(lat1, lat2, lon1, lon2);
        double sideB = distance(lat2, lat3, lon2, lon3);
        double sideC = distance(lat1, lat3, lon1, lon3);

        double radius = (sideA * sideB * sideC) / Math.sqrt((sideA + sideB + sideC) * (sideB + sideC - sideA) * (sideC + sideA - sideB) * (sideA + sideB - sideC));

        double angle1 = angleFromCoordinate(lat1, lon1, lat2, lon2);
        double angle2 = angleFromCoordinate(lat1, lon1, lat3, lon3);
        double angle3 = angleFromCoordinate(lat2, lon2, lat3, lon3);

        double finalAngle = 0;
        int side = 0;

        if (radius < 175) //&& max * 0.95 > min
        {
            if (angle1 > angle2) {
                side = 1;
            } else {
                side = 2;
            }

            Pair<Double, Double> one = Pair.create(coordinates(lat1, lon1).getFirst(), coordinates(lat1, lon1).getThird());
            Pair<Double, Double> two = Pair.create(coordinates(lat2, lon2).getFirst(), coordinates(lat2, lon2).getThird());
            Pair<Double, Double> three = Pair.create(coordinates(lat3, lon3).getFirst(), coordinates(lat3, lon3).getThird());
            j.addAll(Arrays.asList(one, two, three));
            Triple<Double, Double, Double> g = triangleAngles(j, radius);
            finalAngle = g.getSecond();

        }
        String tipo = "";

        if(finalAngle == 0) {
            tipo = "Línea Recta";
        } else if (finalAngle < 25) {
            tipo = "Giro muy Cerrado";
        } else if (finalAngle < 50) {
            tipo = "Giro Cerrado";
        } else if (finalAngle < 110) {
            tipo = "Giro Regular";
        } else {
            tipo = "Giro Suave";
        }


        return new Curvaturas(radius, finalAngle, new ArrayList<TupleDouble>(Arrays.asList(new TupleDouble(lat1, lon1), new TupleDouble(lat2, lon2), new TupleDouble(lat3, lon3))), side, tipo, sideA + sideB);

    }

    public static List<Curvaturas> curvatureTrim(List<Curvaturas> list)
    {
        List<Curvaturas> trimmed = new ArrayList<>();
        Boolean previousIsStraight = false;
        for (int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i).getCurvatureAngle() == 0 && previousIsStraight.equals(true))
            {
                Curvaturas seccionAnterior = trimmed.get(trimmed.size()-1);
                Curvaturas seccionActual = list.get(i);

                trimmed.add(new Curvaturas(seccionAnterior.getCurvatureRadians()+seccionActual.getCurvatureRadians(),
                seccionAnterior.getCurvatureAngle()+seccionActual.getCurvatureAngle(),
                        Stream.concat(seccionAnterior.getSegmentPoints().stream(), seccionActual.getSegmentPoints().stream()).distinct().collect(Collectors.toList()),
                        seccionActual.getTurnDirection(), seccionActual.getCurvatureType(),
                        distance(seccionAnterior.getSegmentPoints().get(0).getLatitude(), seccionActual.getSegmentPoints().get(2).getLatitude(),
                                seccionAnterior.getSegmentPoints().get(0).getLongitude(), seccionActual.getSegmentPoints().get(2).getLongitude())
                        ));
                trimmed.remove(seccionAnterior);
            }
            else if(list.get(i).getCurvatureAngle() == 0)
            {
                trimmed.add(list.get(i));
                previousIsStraight = true;
            }
            else
            {
                trimmed.add(list.get(i));
                previousIsStraight = false;
            }

        }
        return trimmed;
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

    //Obtener hacía donde nos estamos desplazando en el plano del mapa (Punto de inicio para la maniobra de giro)
    public static Pair<Double, Double> coordinateRelation(double side, double max_angle, double min_angle)
    {
        //Referencias:
        //Primer término(eje vertical) 1: Arriba, 2: Abajo, 3: Izquierda, 4: Derecha
        //Segundo término(dirección de giro) 1: Izquierda, 2: Derecha

        Pair<Double, Double> finalOrientation = null;
        //Para el giro a la derecha
        if (side == 2 && 0 < min_angle && min_angle < 90)
        {
            finalOrientation = Pair.create(3.0, side);
        } else if (side == 2 && 90 < min_angle && min_angle < 180)
        {
            finalOrientation = Pair.create(2.0, side);
        } else if (side == 2 && 180 < min_angle && min_angle < 270)
        {
            finalOrientation = Pair.create(4.0, side);
        } else if (side == 2 && 270 < min_angle && min_angle < 360)
        {
            finalOrientation = Pair.create(1.0, side);
        }
        //Para el giro a la izquierda
        else if (side == 1 && 0 < max_angle && max_angle < 90)
        {
            finalOrientation = Pair.create(1.0, side);
        } else if (side == 1 && 90 < max_angle && max_angle < 180)
        {
            finalOrientation = Pair.create(3.0, side);
        } else if (side == 1 && 180 < max_angle && max_angle < 270)
        {
            finalOrientation = Pair.create(2.0, side);
        } else if (side == 1 && 270 < max_angle && max_angle < 360)
        {
            finalOrientation = Pair.create(4.0, side);
        }
        return finalOrientation;
    }
    //Nos sirve para determinar si la curva va a hacia la derecha o izquierda
    public static double angleFromCoordinate(double lat1, double long1, double lat2, double long2)
    {
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);

        return brng;
    }

    public static double angleFromCoordinate360(double lat1, double long1, double lat2, double long2)
    {
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }



    private MyListAdapter loadList2(List<Curvaturas> list)
    {
        List<MyListData> myListData = new ArrayList<>();
        curvesAmount = list.size();
        for (int i = 0; i < list.size(); i++)
        {
            String sideData = "";
            String streetData = "";

            //markerLocationData(list.get(i).getSegmentPoints().get(1)).toString();
            //String streetData = new GeocodeResponse().setTest(list.get(i).getSegmentPoints().get(1), MainActivity.this).toString();

            switch (String.valueOf(list.get(i).getTurnDirection())) {
                case "1.0":
                    sideData = "Orientación de curva: Izquierda " + "| " + "Ángulo de curva: " + String.valueOf(Math.round(list.get(i).getCurvatureAngle())
                            + " " + "Datos de calle: " + streetData);
                    break;

                case "2.0":
                    sideData = "Orientación de curva: Derecha " + "| " + "Ángulo de curva: " + String.valueOf(Math.round(list.get(i).getCurvatureAngle())
                            + " " + "Datos de calle: " + streetData);

            }

            if (list.get(i).getTurnDirection() == 1.0) {
                if (list.get(i).getCurvatureAngle() < 50) {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_closed_left_24"));
                }
                else if (list.get(i).getCurvatureAngle() < 110) {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_left_black_24"));
                } else {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_slight_left_black_24"));
                }
            } else if (list.get(i).getTurnDirection() == 2.0) {
                if (list.get(i).getCurvatureAngle() < 50) {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_closed_right_24"));
                }
                else if (list.get(i).getCurvatureAngle() < 110) {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_right_black_24"));
                } else {
                    myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", sideData, "outline_turn_slight_right_black_24"));
                }
            } else {
                myListData.add(new MyListData(list.get(i).getCurvatureType() + " " + Math.round(list.get(i).getCurvatureLength())+"mts", "Datos de calle: " + streetData, "outline_north_black_24"));
                curvesAmount --;
            }

        }
        currentApiDrawRoad = list;
        MyListData[] myListData2Array = new MyListData[myListData.size()];
        myListData.toArray(myListData2Array);
        MyListAdapter adapter = new MyListAdapter(myListData2Array);
        return adapter;
    }



    private void markerLocationDataAPI2 (List<Curvaturas> locaciones)
    {
        String url = "";
        for (int i = 0; i < locaciones.size(); i++)
        {
            if (APIRequestsCounter == 5)
            {
                try {
                    Thread.sleep(2100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                APIRequestsCounter = 0;
            }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        if (locaciones.get(i).getCurvatureType().equals("Línea Recta")) {
            url = "https://api.tomtom.com/search/2/reverseGeocode/" + locaciones.get(i).getSegmentPoints().get(locaciones.get(i).getSegmentPoints().size()-1).getLatitude() + "%2C" + locaciones.get(i).getSegmentPoints().get(locaciones.get(i).getSegmentPoints().size()-1).getLongitude() + ".json?returnSpeedLimit=false&radius=100&returnRoadUse=false&callback=cb&allowFreeformNewLine=false&returnMatchType=false&view=Unified&key=" + BuildConfig.CREDENTIALS_KEY;
        }
        else
        {
            url = "https://api.tomtom.com/search/2/reverseGeocode/" + locaciones.get(i).getSegmentPoints().get(0).getLatitude() + "%2C" + locaciones.get(i).getSegmentPoints().get(0).getLongitude() + ".json?returnSpeedLimit=false&radius=100&returnRoadUse=false&callback=cb&allowFreeformNewLine=false&returnMatchType=false&view=Unified&key=" + BuildConfig.CREDENTIALS_KEY;
        }
        // Request a string response from the provided URL.
            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    postHandler(response, this.hashCode());
                }
            };
            hashListRequestsCounter.add(response.hashCode());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 1, 1f));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        APIRequestsCounter ++;
        }
    }

    private void postHandler(String response, int hash)
    {

        if (stopThread == false) {
            response = response.replace("cb(", "");
            response = response.replace(")", "");
            response = response.replace("Ã³", "ó");

            try {
                JSONObject jo = new JSONObject(response);
                JSONArray jo_addresses = (JSONArray) jo.get("addresses");
                JSONObject jo_first_address = jo_addresses.getJSONObject(0);
                JSONObject jo_first_address_values = jo_first_address.getJSONObject("address");
                System.out.println(jo_first_address_values.toString());
        //            System.out.println(jo_first_address_values.getString("streetNameAndNumber").toString());
        //            System.out.println(jo_first_address_values.getString("municipality"));
        //            System.out.println(jo_first_address_values.getString("countrySubdivision"));
        //            System.out.println(jo_first_address_values.getString("country"));

                String old = summaryAdapter.getCurrentList()[hashListRequestsCounter.indexOf(hash)].getDescription2();
                summaryAdapter.getCurrentList()[hashListRequestsCounter.indexOf(hash)].setDescription2(old + jo_first_address_values.getString("streetName"));
                summaryAdapter.notifyItemChanged(hashListRequestsCounter.indexOf(hash));

                if (hashListRequestsCounter.indexOf(hash) == 0) {
                    startLocation = jo_first_address_values.getString("country") + " - " + jo_first_address_values.getString("countrySubdivision") + " - " + jo_first_address_values.getString("municipality") + " - " + jo_first_address_values.getString("streetName");
                }

                if (hashListRequestsCounter.indexOf(hash) == (hashListRequestsCounter.size() - 1)) {
                    finishLocation = jo_first_address_values.getString("country") + " - " + jo_first_address_values.getString("countrySubdivision") + " - " + jo_first_address_values.getString("municipality") + " - " + jo_first_address_values.getString("streetName");
                }


            } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                //Thread current = Thread.currentThread();
                //current.interrupt();
                throw new RuntimeException(e);
            }
        }
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