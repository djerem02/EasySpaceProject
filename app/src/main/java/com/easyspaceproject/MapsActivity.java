package com.easyspaceproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener,LocationListener,/*GoogleMap.OnCameraChangeListener,*/ GeoQueryEventListener {
    /*class MonInfoWindow implements GoogleMap.InfoWindowAdapter{
        private View monContenu;


        MonInfoWindow(){
            monContenu = getLayoutInflater().inflate(R.layout.info_window_perso,null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = (TextView) monContenu.findViewById(R.id.title);
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = (TextView) monContenu.findViewById(R.id.snippet);
            tvSnippet.setText(marker.getSnippet());
            return monContenu;
        }
    }*/
    private LocationManager locationManager;
    private Location location;
    private String provider;

    private double latitude = 49.84732; //dur
    private double longitude = 3.289103; //dur
    private double malatitude = 1;
    private double malongitude;
    private float accuracy;

    private GeoLocation CENTRE = new GeoLocation(latitude, longitude); // à changer selon
    private static final LatLng INSSET = new LatLng(49.8495161, 3.2874817);
    private static final LatLng CAMPUS = new LatLng(49.8374935, 3.3000117);
    private static final LatLng FRANCE = new LatLng(46.2157467, 2.2088257);
    private static final int ZOOM = 15;
    private static final double RADIUS = 0.8;
    private static final String BASE = "https://easyspaceproject.firebaseio.com/EasySpace/geofire";


    public GoogleMap mMap;
    private Circle maZone;
    private GeoFire geoFire;
    private Firebase firebase;
    private GeoQuery geoQuery;
    private Geocoder geo;


    private Map<String, Marker> markers;
    private Marker markersdispos;
    private Marker newplace;
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
    private String label = "Parking ";


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
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);

          /*
        Criteria criteria = new Criteria();
        source = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(source);
            if (location != null) {
                latitudeField.setText("Dispo");
                longitudeField.setText("Dispo");
                System.out.print("Source " + source + " a été connectée.");
                onLocationChanged(location);
            }
        }*/

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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(maZone == null || monMarker == null){
            drawCircle(latLng);
        }else{
            updateCircle(latLng);
        }
    }

    private void updateCircle(LatLng position){
        maZone.setCenter(position);
        monMarker.setPosition(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
    private void drawCircle(LatLng position){
        int strokeColor = 0xff00ffff; //red outline
        int shadeColor = 0x4400ffff;//0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(RADIUS*1000).fillColor(shadeColor).strokeColor(strokeColor);
        maZone = mMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        monMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
    protected boolean isRouteDisplayed() {
        return false;
    }

    /*Quand la position de l'utilisateur change */

    public void onLocationChanged(Location location) {


        malatitude = location.getLatitude();
        malongitude = location.getLongitude();

        accuracy = location.getAccuracy();
        if (malatitude != 1) {
            CENTRE = new GeoLocation(malatitude, malongitude);
            //LatLng position = new LatLng(CENTRE.latitude, CENTRE.longitude);


        } else {
            CENTRE = new GeoLocation(latitude, longitude);
            LatLng position = new LatLng(CENTRE.latitude, CENTRE.longitude);
        }

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
        /*READ*/
        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);
        //setup GeoFire
        this.geoFire = new GeoFire(new Firebase(BASE));
        //query around current user location with radius
        GeoQuery geoQuery = geoFire.queryAtLocation(CENTRE,RADIUS);
        //add an event listener to start updating locations again
        geoQuery.addGeoQueryEventListener(this); //utilisé pour recup key
        //setup markers
        this.markers = new HashMap<String, Marker>();

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
                ajouterSpace();
                break;*/
            case R.id.togglemoi:
                if (ajouterToggle.isChecked()) {
                    ajouterSpace(monMarker);
                } else {
                    quitterSpace();
                }
            default:
                break;
        }
    }


    private void ajouterSpace(Marker monMarker /*Marker marker*/) {
        monMarker.setVisible(false);
        //J'occupe la place
        //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        etat = "Occupé";
        //marker.setVisible(false);

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

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //ToggleButton
                final ToggleButton toggle = (ToggleButton) alertDialogView.findViewById(R.id.toggleButton);
                type = (String) toggle.getText();


                //Spinner
                Spinner tailleParkingSpinner = (Spinner) alertDialogView.findViewById(R.id.spinnerTailleParking);
                tailleParkingSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
                nbrPlaces = (String) tailleParkingSpinner.getSelectedItem();


                label = "Parking " + rue;

                /*WRITE*/
                    /*FIREBASE*/


                Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
                Parking parking = new Parking(label, rue, cp, ville, malatitude,malongitude, nbrPlaces, type, etat);
                firebase.child("firebase").child(label).setValue(parking);

                    /*GEOFIRE*/
                GeoFire geoFire = new GeoFire(firebase.child("geofire"));
                geoFire.setLocation(label, new GeoLocation(malatitude, malongitude));


                /*READ*/
                /*geoFire.getLocation("park", new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        if (location != null) {
                            System.out.println(String.format("Les coordonnées pour la clef %s is [%f,%f]", key, location.latitude, location.longitude));


                            // creates a new query  with a radius of 0.6 kilometers
                            GeoFire geoFire = new GeoFire(new Firebase(BASE));
                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude,location.longitude), 1);

                        } else {
                            System.out.println(String.format("Il n'y a pas de lieu pour la clef %s dans GeoFire", key));
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.err.println("Il y a une erreur dans la localisation GeoFire: " + firebaseError);
                    }
                });

                */
                LatLng position = new LatLng(malatitude,malongitude);
                newplace = mMap.addMarker(new MarkerOptions()
                        .title(label).snippet("Vous êtes garé ici ! ")
                        .position(position));
                newplace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                newplace.setVisible(true);

                String msg = " Place ajoutée au Park !";
                Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();

            }//Fin Onclick
        }).setNegativeButton("Non merci", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
                LatLng position = new LatLng(malatitude,malongitude);
                Map<String, Object> etat = new HashMap<String, Object>();
                etat.put("/etat", "Occupé");
                firebase.child("firebase").child(label).updateChildren(etat);


                newplace = mMap.addMarker(new MarkerOptions()
                        .title(label).snippet("Vous êtes garé ici ! ")
                        .position(position));
                newplace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                newplace.setVisible(true);
            }
        });



        adb.show();

        //FIN DIALOGBOX

    } //FIN AJOUTERSPACE()

    private void quitterSpace() {
        //Faire disparaitre marker rouge
        newplace.setVisible(false);
        etat = "Libre";
        Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
        Parking parking = new Parking(label, rue, cp, ville, CENTRE.latitude, CENTRE.longitude, nbrPlaces, type, etat);
        firebase.child("firebase").child(label).setValue(parking);
        //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        String message = "Place quittée";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a info_window_perso near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

        //mMap.setInfoWindowAdapter(new MonInfoWindow());

    }


    //Localisation

    @Override
    /*Mise à jour des coordonnées*/
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);

        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates((LocationListener) this);

        }

    }

    @Override
    public void onStart() {
        super.onStart();


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


    @Override
    public void onStop() {
        super.onStop();
        //remove all event listeners to stop updating in the background
        /*this.geoQuery.removeAllListeners();
        for (Marker marker : this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();*/


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

    //Animation
    /*private void animateMarkerTo(final Marker info_window_perso, final double lat, final double lng){
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = info_window_perso.getPosition();
        handler.post(new Runnable(){
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis()- start;
                float t = elapsed/DURATION_MS;
                float v =  interpolator.getInterpolation(t);

                double currentLat =(lat -startPosition.latitude)* v + startPosition.latitude;
                double currentLng =(lng-startPosition.longitude)*v + startPosition.longitude;
                info_window_perso.setPosition(new LatLng(currentLat,currentLng));

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

    /*Display*/

    final Set<String> parkingZone = new HashSet<String>();

        public void onKeyEntered(final String label, final GeoLocation location) {
            Firebase data = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace/firebase");
            /*String key=label ;
            parkingZone.add(key);

            /*firebase.child("EasySpace/firebase").child(key).once("value",function(dataSnapshot){


            });*/






            //Ajoute les markers (utils)
            //IconGenerator icon = new IconGenerator(this);
            //addIcon(icon, "Default", new LatLng(location.latitude, location.longitude));


            /*data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //marker.setSnippet(String.valueOf(snapshot.getValue())); //donne la liste
                    //marker.setSnippet("Il y a  " + snapshot.getChildrenCount() + " catégories."); //nombre de catégories dans la table.
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Parking parking = postSnapshot.getValue(Parking.class);
                        marker.setSnippet(parking.getNbrPlaces()+ " places - " + parking.getType());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
            */
            //Query query = data.orderByChild(label);
            //Query query = data.orderByChild("nbrPlaces").limitToFirst(2);
            /*data.limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot datasnapshot) {
                    //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Parking parking = postSnapshot.getValue(Parking.class);

                        //marker.setSnippet(parking.getNbrPlaces() + " - " + parking.getType());

                    //}

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/
            //Query query= data.orderByChild("etat").equalTo("Libre").limitToFirst(3);
            //Query query = data.orderByChild("nbrPlaces").limitToFirst(2);

                markersdispos= mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
                markersdispos.setTitle(label);




            /*query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previous) {
                    Parking parking = dataSnapshot.getValue(Parking.class);
                    //Ajoute les markers
                    markersdispos = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
                    markersdispos.setTitle(parking.getLabel());
                    markersdispos.setSnippet(parking.getNbrPlaces() + " - " + parking.getType() + " - " + parking.getEtat());

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //Problème mise à jour
                    String typemaj = (String) dataSnapshot.child("type").getValue();
                    String nbrPlacesmaj = (String) dataSnapshot.child("nbrPlaces").getValue();
                    String etatmaj = (String) dataSnapshot.child("etat").getValue();
                    markersdispos.setSnippet(nbrPlacesmaj + " - " + typemaj + " - " + etatmaj);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String typedel = (String) dataSnapshot.child("type").getValue();
                    String nbrPlacesdel = (String) dataSnapshot.child("nbrPlaces").getValue();
                    String etatdel = (String) dataSnapshot.child("etat").getValue();
                    markersdispos.setSnippet(nbrPlacesdel + " - " + typedel + " - " + etatdel);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/

            //Ajoute la couleur verte aux markers disponibles
            markersdispos.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //Affiche infowindow
            //marker.showInfoWindow();
            markers.put(label,markersdispos);

            }


    @Override
    public void onKeyExited(String label) {
            //parkingZone.remove(label);
            // Remove any old info_window_perso
            Marker marker = markers.get(label);
            if (marker != null){
                marker.remove();
                markers.remove(label);
            }

        }

        @Override
        public void onKeyMoved(String label, GeoLocation location) {
            //Move the info_window_perso
            Marker marker = markers.get(label);
            if(marker != null){
                //this.animateMarkerTo(info_window_perso, location.latitude, location.longitude);
            }
        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(FirebaseError error) {
                /*new AlertDialog.Builder(this)
                        .setTitle("Erreur")
                        .setMessage("Il y a une erreur: " + error.getMessage() )
                        .setPositiveButton(android.R.string.ok,null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }*/
        };

    private void addIcon(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }



}