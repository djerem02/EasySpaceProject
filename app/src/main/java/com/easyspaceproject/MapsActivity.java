package com.easyspaceproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

import android.graphics.Color;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener,LocationListener,/*GoogleMap.OnCameraChangeListener,*/ GeoQueryEventListener {

    private LocationManager locationManager;
    private Location location;
    private String provider;

    private double latitude = 49.84732; //dur
    private double longitude = 3.289103; //dur
    private double malatitude = 1;
    private double malongitude;
    private float accuracy;

    private GeoLocation CENTRE = new GeoLocation(malatitude, malongitude);
    private static final LatLng INSSET = new LatLng(49.8495161, 3.2874817);
    private static final LatLng CAMPUS = new LatLng(49.8374935, 3.3000117);
    private static final LatLng FRANCE = new LatLng(46.2157467, 2.2088257);
    private static final int ZOOM = 15;  //éloigné<<près
    private static final double RADIUS = 0.7;
    private static final String BASE = "https://easyspaceproject.firebaseio.com/EasySpace/geofire";


    public GoogleMap mMap;
    private Circle maZone;
    private GeoFire geoFire;
    private Firebase firebase;
    private GeoQuery geoQuery;
    private Geocoder geo;


    private HashMap<String, Marker> markers;
    private Marker markersdispos;
    private Marker mySpace;
    private Marker monMarker;


    //private TextView latitudeField;
    //private TextView longitudeField;
    //private TextView rechercheField;
    //private Button ajouterBouton;
    private Spinner tarifBouton;
    private ToggleButton ajouterToggle;

    private String nbrPlaces = "nbrPlaces";
    private String type = "type";
    private String etat = "etat";
    private String rue = "rue";
    private String cp = "cp";
    private String ville = "ville";
    private String label = "Parking "+ rue;

    private TextView text ;
    private IconGenerator iconGenerator;


    /*
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Loading
        /*ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Chargement...");
        progress.setMessage("Veuillez patienter, chargement en cours...");
        progress.show();*/
        geo = new Geocoder(MapsActivity.this);

        // POUR CLASSE
        //MonLocationListener monLocationListener = new MonLocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //a remettre pos
        //LES SOURCES
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);// aremettre pos
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);


        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);
            /*if (location != null) {
                latitudeField.setText("Dispo");
                longitudeField.setText("Dispo");
                System.out.print("Source " + source + " a été connectée.");
                onLocationChanged(location);
            }*/
        //}

        //latitudeField = (TextView) findViewById(R.id.idvaleurlatitude);
        //longitudeField = (TextView) findViewById(R.id.idvaleurlongitude);

        //rechercheField = (TextView) findViewById(R.id.idrecherche);
        //rechercheField.setHint("Rechercher");

        //ajouterBouton = (Button) findViewById(R.id.idboutonajouter);
        //ajouterBouton.setOnClickListener(this);
        ajouterToggle = (ToggleButton) findViewById(R.id.togglemoi);
        ajouterToggle.setOnClickListener(this);

        tarifBouton = (Spinner) findViewById(R.id.spinnerTypeParking);
        MonSpinnerPerso adapter = new MonSpinnerPerso(this, new Integer[]{R.drawable.all, R.drawable.parkgratuit, R.drawable.limit, R.drawable.phandicap});
        tarifBouton.setAdapter(adapter);
        //tarifBouton.setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    public void onMyLocationChange(Location location){
        LatLng latLng = new LatLng(malatitude,malongitude);
        if(maZone == null || monMarker  == null){
            drawCircle(latLng);
        }else{
            updateCircle(latLng);
        }
    }

    private void updateCircle(LatLng position){
        maZone.setCenter(position);
        monMarker.setPosition(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
    }
    private void drawCircle(LatLng position){
        int strokeColor = 0xff00ffff; // outline
        int shadeColor = 0x4400ffff;//  fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(RADIUS*1000).fillColor(shadeColor).strokeColor(strokeColor);
        maZone = mMap.addCircle(circleOptions);

        //Marker ma position avec getlat et getlong
        MarkerOptions markerOptions = new MarkerOptions().position(position);
        monMarker = mMap.addMarker(markerOptions);
        monMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        monMarker.setVisible(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));


    }
    protected boolean isRouteDisplayed() {
        return false;
    }

    /*Quand la position de l'utilisateur change */

    public void onLocationChanged(Location location) {


        //malatitude = location.getLatitude();
        //malongitude = location.getLongitude();
        malatitude = 49.8368987; //dur
        malongitude = 3.2995772; //dur

        accuracy = location.getAccuracy();
        CENTRE = new GeoLocation(malatitude, malongitude);
        /*if (malatitude != 1) {
            CENTRE = new GeoLocation(malatitude, malongitude);
            //LatLng position = new LatLng(CENTRE.latitude, CENTRE.longitude);


        } else {
            CENTRE = new GeoLocation(latitude, longitude);
            LatLng position = new LatLng(CENTRE.latitude, CENTRE.longitude);
        }*/

        TextView tvAdresse = new TextView(this);

        try {
            List<Address> adresses = geo.getFromLocation(CENTRE.latitude, CENTRE.longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);
                String rue = adresse.getThoroughfare();
                String cp = adresse.getPostalCode();
                String ville = adresse.getLocality();
                tvAdresse = (TextView) findViewById(R.id.adresse);
                tvAdresse.setText(String.valueOf(rue + " " + cp + " " + ville));


            } else {
                ((TextView) findViewById(R.id.adresse)).setText("Adresse indéterminée");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ((TextView) findViewById(R.id.adresse)).setText("Adresse indéterminée");
        }

        //latitudeField.setText(String.valueOf("Lat: " + latitude));
        //longitudeField.setText(String.valueOf("Long: " + longitude));

        onMyLocationChange(location);


        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null);
        //Ajoute les markers (utils)
        //IconGenerator icon = new IconGenerator(this);
        //BitmapDescriptor icon =BitmapDescriptorFactory.fromResource(R.drawable.all);
        //addIcon(icon, "Default", new LatLng(malatitude,malongitude));
        /*LatLng position = new LatLng(malatitude, malongitude);
        monMarker = mMap.addMarker(new MarkerOptions()
                .title("Vous êtes ici !")
                .rotation(90)
                .position(position));
        monMarker.setAlpha((float) 0.7);
        //monMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.markerauto));
        monMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        /*this.maZone = this.mMap.addCircle(new CircleOptions().center(position).radius(RADIUS*1000));
        this.maZone.setFillColor(Color.argb(1, 0, 255, 255));   //Interieur
        this.maZone.setStrokeColor(Color.argb(20, 0, 0, 255));    //Limite
        */


        //marker ma position avec setMyLocation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

    }


    /*Quand le statut de la source change*/

    public void onStatusChanged(String source, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "Indisponible";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "Temporairement indisponible";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "Disponible";
                break;
        }
        String msg = String.format(getResources().getString(R.string.source_statut), source, newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*Quand une source est activée*/

    public void onProviderEnabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_active), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*Quand une source est desactivée*/

    public void onProviderDisabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_desactive), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //Méthode déclencher au clique sur un bouton
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.idboutonajouter:
                occuperSpace();
                break;*/
            case R.id.togglemoi:
                if (ajouterToggle.isChecked()) {
                    occuperSpace(monMarker);
                } else {
                    quitterSpace();
                }
            default:
                break;
        }
    }

    private void occuperSpace(Marker monMarker ) {
        monMarker.setVisible(false);
        //J'occupe la place
        //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        etat = "Occupé";

        //ALERTBOX
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.alertdialogperso, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        TextView maPosition = new TextView(this);

        adb.setView(alertDialogView);
        adb.setTitle("Ajouter une place de parking");
        adb.setIcon(R.drawable.addmarker);

        try {
            List<Address> adresses = geo.getFromLocation(CENTRE.latitude, CENTRE.longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);
                rue = adresse.getThoroughfare();
                cp = adresse.getPostalCode();
                ville = adresse.getLocality();


                maPosition = (TextView) alertDialogView.findViewById(R.id.idmaposition);
                maPosition.setText(String.valueOf("Ma position actuelle :\n Coordonées GPS : (" + CENTRE.latitude + ";" + CENTRE.longitude + ")\n Adresse :\n" + rue + " " + cp + " " + ville));

                /*String maPosition = "Ma position : " + rue +" "+ cp +" "+ville;
                ((TextView) findViewById(R.id.adresse)).setText(maPosition);*/
            } else {
                ((TextView) findViewById(R.id.idmaposition)).setText("Adresse indéterminée");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ((TextView) findViewById(R.id.idmaposition)).setText("Adresse indéterminée");
        }
        label = "Parking " + rue;
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //ToggleButton
                final ToggleButton toggle = (ToggleButton) alertDialogView.findViewById(R.id.toggleButton);
                type = (String) toggle.getText();


                //Spinner
                Spinner tailleParkingSpinner = (Spinner) alertDialogView.findViewById(R.id.spinnerTailleParking);
                tailleParkingSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
                nbrPlaces = (String) tailleParkingSpinner.getSelectedItem();

                ajouterSpace();

                String msg = " Place ajoutée au Park !";
                Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();

            }//Fin Onclick
        }).setNegativeButton("Non merci", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ajouterSpace();
            }
        });



        adb.show();

        //FIN DIALOGBOX

    } //FIN OCCUPERSPACE()

    private void ajouterSpace(){
        Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
        /*Firebase*/
        Parking parking = new Parking(label, rue, cp, ville, malatitude,malongitude, nbrPlaces, type, etat);
        firebase.child("firebase").child(label).setValue(parking);

        /*Geofire*/
        GeoFire geoFire = new GeoFire(firebase.child("geofire"));
        geoFire.setLocation(label, new GeoLocation(malatitude, malongitude));

        LatLng position = new LatLng(malatitude,malongitude);
        mySpace = mMap.addMarker(new MarkerOptions()
                .title(label).snippet("Vous êtes garé ici ! ")
                .rotation(20)
                .position(position));
        mySpace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mySpace.setVisible(true);

    }

    private void quitterSpace() {

        //Faire disparaitre marker rouge
        mySpace.remove();
        //mySpace.setVisible(false);
        etat = "Libre";
        label="Parking "+rue;
        Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
        Parking parking = new Parking(label, rue, cp, ville, CENTRE.latitude, CENTRE.longitude, nbrPlaces, type, etat);
        firebase.child("firebase").child(label).setValue(parking);
        String message = "Place quittée";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        /* OU SI MAPOSITION=MARKER couleur => rouge
        if(malatitude== && malongitude==){
         marker.addoptionscolor = rouge }*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAMPUS, ZOOM));
        //this.mMap.setOnCameraChangeListener(this);

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //Type Satellite (trop energivore)
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Zoom sur ...
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FRANCE, 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6), 4000, null); //13 normal 15 Hybrid 5 France*/

        //Marqueurs
        //final Marker insset = mMap.addMarker(new MarkerOptions().position(INSSET).title("INSSET").snippet("Aurevoir le centre ville !"));
        //final Marker campus = mMap.addMarker(new MarkerOptions().position(CAMPUS).title("IUT").snippet("Notre nouveau campus !"));


    }


    //Localisation

    @Override
    /*Mise à jour des coordonnées*/
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 3, (LocationListener) this);

        }


    }

    //Pas visible sur l'écran
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates((LocationListener) this);

        }
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //remove all event listeners to stop updating in the background
        /*geoQuery.removeAllListeners();
        for (Marker markersdispos : markers.values()) {
            markersdispos.remove();
        }
        markers.clear();*/


    // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.easyspaceproject/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);
        //setup GeoFire
        this.geoFire = new GeoFire(new Firebase(BASE));
        //affiche les markers autour
        CENTRE= new GeoLocation(malatitude,malongitude);
        this.geoQuery = this.geoFire.queryAtLocation(CENTRE,RADIUS);
        //add an event listener to start updating locations again
        //geoQuery.addGeoQueryEventListener(this); //utilisé pour recup key
        //setup markers
        this.markers = new HashMap<String, Marker>();

        this.geoQuery.addGeoQueryEventListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.easyspaceproject/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

    }


    //Animation
    /*private void animateMarkerTo(final Marker marker, final double lat, final double lng){
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable(){
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis()- start;
                float t = elapsed/DURATION_MS;
                float v =  interpolator.getInterpolation(t);

                double currentLat =(lat -startPosition.latitude)* v + startPosition.latitude;
                double currentLng =(lng-startPosition.longitude)*v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat,currentLng));

                //if animation is not finished yet , repeat
                if(t<1){
                    handler.postDelayed(this,16);
                }
            }
        });
    }*/


    /*private double zoomLevelToRadius (double zoomLevel){
        //Approximation to fit circle into view
        return 16384000/Math.pow(2,zoomLevel);
    }*/

    /*@Override
    public void onCameraChange(CameraPosition cameraPosition){
        //Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
        this.zone.setCenter(center);
        this.zone.setRadius(radius);
        //this.geoQuery.setCenter(CENTRE); ERROR
        // radius en km
        //this.geoQuery.setRadius(radius/1000); ERROR
    }*/

    /*Affichage des parkings*/

    final Set<String> parkingZone = new HashSet<String>();
        @Override
        public void onKeyEntered(final String key, final GeoLocation location){

           final Firebase data = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace/firebase");

            //Affiche données seulement si etat==Libre ( attention marker affiché tout de même)
            //Query query= data.orderByChild("etat").startAt("Libre").endAt("Libre");

            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //marker.setSnippet(String.valueOf(snapshot.getValue())); //donne la liste
                    //marker.setSnippet("Il y a  " + snapshot.getChildrenCount() + " catégories."); //nombre de catégories
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String quellabel = (String) snapshot.child(key).getKey();
                        String queletat = (String) snapshot.child(key).child("etat").getValue();
                        String quelnbrplaces = (String) snapshot.child(key).child("nbrPlaces").getValue();
                        String queltype = (String) snapshot.child(key).child("type").getValue();

                        /*String quellelatitude = (String) snapshot.child(key).child("latitude").getValue();
                        String lat = (String) snapshot.child(key).child("l").child("0").getValue();
                        String lon = (String) snapshot.child(key).child("l").child("1").getValue();
                        String quellelongitude = (String) snapshot.child(key).child("longitude").getValue();
                        Integer l=Integer.valueOf(lat);
                        Integer L=Integer.valueOf(lon);
                        Integer qlatitude = Integer.valueOf(quellelatitude);
                        Integer qlongitude = Integer.valueOf(quellelongitude);*/

                        List<LatLng> latLngList = new ArrayList<LatLng>();
                        //latLngList.add(new LatLng(qlatitude,qlongitude));
                        latLngList.add(new LatLng(location.latitude,location.longitude));

                        Map<Double,Double> hashmap = new HashMap<Double,Double>();
                        //map.add(location.latitude,location.longitude);
                        //double tableauLatLong[][]= {{location.latitude},{location.longitude}};
                        markers.put(key, markersdispos);
                        hashmap.put(location.latitude,location.longitude);
                        for (HashMap.Entry<Double,Double> entry: hashmap.entrySet()) {
                                Double la= entry.getKey();
                                Double lo= entry.getValue();
                                                        //LatLng latilongi = new LatLng(markers[i].get)
                        /*for( int i=0;i<tableauLatLong.length;i++){
                            for (int j = 0;j<tableauLatLong.length;j++){
                                markersdispos=mMap.addMarker((new MarkerOptions().position(tableauLatLong[i][j])));
                            }
                        }*/
                            //while (x <= latLngList.size()) {

                            //if(queletat==){
                            LatLng poste = new LatLng(la,lo);
                            markersdispos = mMap.addMarker(new MarkerOptions().position(new LatLng(la,lo)));
                            markersdispos.setVisible(true);
                            markersdispos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            //markersdispos.setSnippet(String.valueOf(la)+" -"+String.valueOf(lo));
                            markersdispos.setSnippet(quelnbrplaces + " places - " + queltype + " - " + queletat);
                            markersdispos.setTitle(quellabel);

                            /*LatLng latLng = new LatLng(location.latitude, location.longitude);

                                text.setText(String.valueOf(la)+" - "+String.valueOf(lo));
                                iconGenerator.setBackground(getDrawable(R.drawable.bubble_mask));
                                iconGenerator.setContentView(text);
                                Bitmap icon = iconGenerator.makeIcon();
                                MarkerOptions tp = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(icon));
                                mMap.addMarker(tp);
                            */


                            //} else{
                            //markersdispos.setVisible(false);
                            //    markersdispos= mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)));
                            //    markersdispos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            //    markersdispos.setTitle(quellabel);
                            //    markersdspos.setSnippet(String.valueOf(la) + " -" + String.valueOf(lo));
                            //}

                        }


                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
            /*
            data.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previous) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String quellabel = (String) snapshot.getKey();
                        String queletat = (String) snapshot.child("etat").getValue();
                        String quelnbrplaces = (String) snapshot.child("nbrPlaces").getValue();
                        String queltype = (String) snapshot.child("type").getValue();


                        markersdispos = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));



                        //if(queletat=="Libre"){

                        markersdispos.setVisible(true);
                        markersdispos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markersdispos.setSnippet(quelnbrplaces + " places - " + queltype + " - " + queletat);
                        markersdispos.setTitle(quellabel);
                        //}else{
                        //markersdispos.setVisible(false);
                        //    markersdispos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        //}

                    }
                }
                @Override
                public void onChildChanged(DataSnapshot snapshot, String s) {
                    //Problème mise à jour
                    String typemaj = (String) snapshot.child(key).child("type").getValue();
                    String nbrPlacesmaj = (String) snapshot.child(key).child("nbrPlaces").getValue();
                    String etatmaj = (String) snapshot.child(key).child("etat").getValue();
                    markersdispos.setSnippet(nbrPlacesmaj + " maj- " + typemaj + " - " + etatmaj);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //String typedel = (String) dataSnapshot.child("type").getValue();
                    //String nbrPlacesdel = (String) dataSnapshot.child("nbrPlaces").getValue();
                    //String etatdel = (String) dataSnapshot.child("etat").getValue();
                    //markersdispos.setSnippet(nbrPlacesdel + " - " + typedel + " - " + etatdel);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/


        }

    //Sortie de la zone geoquery
    /*Pour faire disparaitre les markers hors-zone*/
    @Override
    public void onKeyExited(String key) {
            //parkingZone.remove(label);
            // Remove any old info_window_perso
            Marker marker = this.markers.get(key);
            if (marker != null){
                marker.remove();
                this.markers.remove(key);
            }
        }
    //Deplacement dans la zone geoquery
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        //Move the marker
        Marker marker = markers.get(key);
        if(marker != null){
            //this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady() {
    }

    @Override
    public void onGeoQueryError(FirebaseError error) {
        new AlertDialog.Builder(this)
                .setTitle("Erreur")
                .setMessage("Il y a une erreur: " + error.getMessage() )
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void addIcon(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }



}