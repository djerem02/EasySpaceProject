package com.easyspaceproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener,GoogleMap.OnCameraChangeListener, GeoQueryEventListener {
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
    private double latitude = 49.8479; // var a mettre dans geolocation qui sera getLocation
    private double longitude = 3.284366; // var a mettre dans geolocation qui sera getLocation
    private float accuracy;

    private static final LatLng INSSET = new LatLng(49.8495161, 3.2874817);
    private static final LatLng CAMPUS = new LatLng(49.8374935, 3.3000117);
    private static final LatLng FRANCE = new LatLng(46.2157467, 2.2088257);
    private final GeoLocation CENTRE = new GeoLocation(latitude,longitude);
    private static final int ZOOM = 17;
    private static final float RADIUS = (float) 1;
    private static final String BASE = "https://easyspaceproject.firebaseio.com/EasySpace/geofire";
    //private static final String TEST = "https://glaring-inferno-9753.firebaseio.com/EasySpace/parking";


    public GoogleMap mMap;
    private Circle zone;
    private GeoFire geoFire;
    private Firebase firebase;
    private GeoQuery geoQuery;
    private Geocoder geo;



    private Map<String,Marker> markers;
    private Marker newplace ;

    private LocationManager locationManager;
    private Location location;
    private String source;


    private TextView latitudeField;
    private TextView longitudeField;
    //private TextView rechercheField;
    private Button ajouterBouton;
    private Spinner tarifBouton;

    private ToggleButton ajouterToggle;

    private String nbrPlaces = "100";
    private String type = "?";

    /**
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

        geo = new Geocoder(MapsActivity.this);

        //Loading
        /*ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Chargement...");
        progress.setMessage("Veuillez patienter, chargement en cours...");
        progress.show();*/


        // POUR CLASSE
        // MonLocationListener locationListener = new MonLocationListener();

        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //a remettre pos
        //LES SOURCES
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);// aremettre pos
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);

        //latitudeField = (TextView) findViewById(R.id.idvaleurlatitude);
        //longitudeField = (TextView) findViewById(R.id.idvaleurlongitude);

        //rechercheField = (TextView) findViewById(R.id.idrecherche);
        //rechercheField.setHint("Rechercher");

        //ajouterBouton = (Button) findViewById(R.id.idboutonajouter);
        //ajouterBouton.setOnClickListener(this);
        ajouterToggle= (ToggleButton) findViewById(R.id.togglemoi);
        ajouterToggle.setOnClickListener(this);

        tarifBouton = (Spinner) findViewById(R.id.spinnerTypeParking);
        //tarifBouton.setOnClickListener(this);
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
            } else {
                latitudeField.setText("Indispo");
                longitudeField.setText("Indispo");
            }

        }



        */

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
         /*FONCTIONS*/

        TextView tvAdresse = new TextView(this);
        try {
            List<Address> adresses = geo.getFromLocation(latitude, longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);
                String rue = adresse.getThoroughfare();
                String cp = adresse.getPostalCode();
                String ville = adresse.getLocality();
                tvAdresse = (TextView)findViewById(R.id.adresse);
                tvAdresse.setText(String.valueOf(rue+" " + cp + " " + ville));
            } else {
                ((TextView) findViewById(R.id.adresse)).setText("Adresse indéterminée");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ((TextView) findViewById(R.id.adresse)).setText("Adresse indéterminée");
        }
    }

    protected boolean isRouteDisplayed() {
        return false;
    }

    //Méthode déclencher au clique sur un bouton
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.idboutonajouter:
                ajouterSpace();
                break;*/
            case R.id.togglemoi:
                if (ajouterToggle.isChecked()){
                    quitterSpace(newplace);
                }else{ajouterSpace();}
            default:
                break;
        }
    }


    private void ajouterSpace() {
        //J'occupe la place
        //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


        //ALERTBOX
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.alertdialogperso, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        TextView maPosition = new TextView(this);

        adb.setView(alertDialogView);
        adb.setTitle("Ajouter une place de parking");
        adb.setIcon(R.drawable.addmarker);

        try {
            List<Address> adresses = geo.getFromLocation(latitude, longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);
                String rue = adresse.getThoroughfare();
                String cp = adresse.getPostalCode();
                String ville = adresse.getLocality();


                maPosition = (TextView) alertDialogView.findViewById(R.id.idmaposition);
                maPosition.setText(String.valueOf("Ma position actuelle :\n Coordonées GPS : (" + latitude + ";" + longitude + ")\n Adresse :\n" + rue + " " + cp + " " + ville));

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
                List<Address> adresses = null;
                try {
                    adresses = geo.getFromLocation(latitude, longitude, 1);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address adresse = adresses.get(0);
                String rue = adresse.getThoroughfare();
                String cp = adresse.getPostalCode();
                String ville = adresse.getLocality();
                String label = "Parking "+rue;

                String etat = "Occupé";

                //ToggleButton
                final ToggleButton toggle = (ToggleButton)alertDialogView.findViewById(R.id.toggleButton);
                type= (String) toggle.getText();


                //Spinner
                Spinner tailleParkingSpinner = (Spinner) alertDialogView.findViewById(R.id.spinnerTailleParking);
                tailleParkingSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
                nbrPlaces = (String) tailleParkingSpinner.getSelectedItem();


                final LatLng position = new LatLng(latitude, longitude);

                /*WRITE*/
                    /*FIREBASE*/


                Firebase firebase = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace");
                Parking parking = new Parking(label, rue, cp, ville, latitude, longitude, nbrPlaces, type, etat);
                firebase.child("firebase").child(label).setValue(parking);

                    /*GEOFIRE*/
                GeoFire geoFire = new GeoFire(firebase.child("geofire"));
                geoFire.setLocation(label, new GeoLocation(latitude, longitude));


                //Suppression
                /*geoFire.removeLocation("firebase-hq");*/

                /*READ*/
                /*geoFire.getLocation("park", new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        if (location != null) {
                            System.out.println(String.format("Les coordonnées pour la clef %s is [%f,%f]", key, location.latitude, location.longitude));

                            final LatLng pos = new LatLng(location.latitude, location.longitude);
                            final Marker newplace = mMap.addMarker(new MarkerOptions()
                                    .title("Parking ajouté !").snippet("(" + latitude + " ; " + longitude + ")")
                                    .position(pos));
                            newplace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

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
                /*final Marker*/newplace = mMap.addMarker(new MarkerOptions()
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
            }
        });

        adb.show();

        //FIN DIALOGBOX

    } //FIN AJOUTERSPACE()
    private void quitterSpace(Marker marker, String etat) {
        marker.setVisible(false);
        etat="Libre";
        //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    }

    /*Quand la position de l'utilisateur change */
    @Override
    public void onLocationChanged(Location location) {


        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

        /*String myLocation = "Latitude : " + latitude + "\nLongitude : " + longitude;
        Toast.makeText(this,myLocation,Toast.LENGTH_LONG).show();*/


        latitudeField.setText(String.valueOf("Lat: " + latitude));
        longitudeField.setText(String.valueOf("Long: " + longitude));


        LatLng position = new LatLng(latitude, longitude);
        final Marker maposition = mMap.addMarker(new MarkerOptions()
                .title("Vous êtes ici !").snippet("(" + latitude + " ; " + longitude + ")")
                .position(position));
        maposition.setAlpha((float) 0.6);
        //maposition.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        maposition.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.markerauto));
        maposition.setVisible(true);

        /*mMap.addCircle(new CircleOptions().center(position).radius(100)
                .strokeColor(0xff00f00).strokeWidth(3));*/

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null);

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
        LatLng latLngCenter = new LatLng(CENTRE.latitude,CENTRE.longitude);
        this.zone = this.mMap.addCircle(new CircleOptions().center(latLngCenter).radius(RADIUS*1000));
        this.zone.setFillColor(Color.argb(66, 180, 200, 255));
        this.zone.setStrokeColor(Color.argb(66, 0, 0, 255));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, ZOOM));
        this.mMap.setOnCameraChangeListener(this);

        /*READ*/
        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);
        //setup GeoFire
        this.geoFire = new GeoFire(new Firebase(BASE));
        //query around current user location with 1km radius
        GeoQuery geoQuery = geoFire.queryAtLocation(CENTRE,1);
        //add an event listener to start updating locations again
        geoQuery.addGeoQueryEventListener(this); //utilisé pour recup key
        //setup markers
        this.markers = new HashMap<String, Marker>();

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //Type Satellite (trop energivore)
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Zoom sur ...
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FRANCE, 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6), 4000, null); //13 normal 15 Hybrid 5 France*/

        //Marqueurs
        final Marker insset = mMap.addMarker(new MarkerOptions().position(INSSET).title("INSSET").snippet("Aurevoir le centre ville !"));
        final Marker campus = mMap.addMarker(new MarkerOptions().position(CAMPUS).title("IUT").snippet("Notre nouveau campus !"));

        //mMap.setInfoWindowAdapter(new MonInfoWindow());
    }


    //Localisation

    /*@Override
    /*Mise à jour des coordonnées*/
    /*
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(source, 400, 1, (LocationListener) this);

        }


    }


    }

    */

    /*Quand le statut de la source change*/
    @Override
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
    @Override
    public void onProviderEnabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_active), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*Quand une source est desactivée*/
    @Override
    public void onProviderDisabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_desactive), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);

        }

    }

    @Override
    public void onStart() {
        super.onStart();


        //mettre à jour place libre/occupé
        /*if( MARKER.etat==true){
            place.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }else if (MARKER.etat==false){
            place.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }*/
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


    private double zoomLevelToRadius (double zoomLevel){
        //Approximation to fit circle into view
        return 16384000/Math.pow(2,zoomLevel);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition){
        //Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
        this.zone.setCenter(center);
        this.zone.setRadius(radius);
        //this.geoQuery.setCenter(CENTRE); ERROR
        // radius en km
        //this.geoQuery.setRadius(radius/1000); ERROR
    }

    /*Display*/

    //final Set<String> parkingZone = new HashSet<String>();
        public void onKeyEntered(String label,GeoLocation location) {
            /*ClusterManager<ClusterMarkerLocation> clusterManager = new ClusterManager<ClusterMarkerLocation>(this,mMap);
            double lat;
            double lng;
            Random generator = new Random();
            for( int i = 0; i < 1000; i++ ) {
                lat = generator.nextDouble() / 3;
                lng = generator.nextDouble() / 3;
                if (generator.nextBoolean()) {
                    lat = -lat;
                }
                if (generator.nextBoolean()) {
                    lng = -lng;
                }
            }*/
            /*String key=label ;
            parkingZone.add(key);

            /*firebase.child("EasySpace/firebase").child(key).once("value",function(dataSnapshot){


            });*/

            Firebase data = new Firebase("https://easyspaceproject.firebaseio.com/EasySpace/firebase");



            //Ajoute les markers (utils)
            //IconGenerator icon = new IconGenerator(this);
            //addIcon(icon, "Default", new LatLng(location.latitude, location.longitude));


            //Ajoute les markers
                final Marker marker  = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
                marker.setTitle(label);

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

            /*data.limitToFirst(2).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previous) {
                    Parking parking = dataSnapshot.getValue(Parking.class);

                    marker.setSnippet(parking.getNbrPlaces()+" - "+parking.getType());

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String typemaj = (String) dataSnapshot.child("type").getValue();
                    String nbrPlacesmj = (String)dataSnapshot.child("nbrPlaces").getValue();
                    marker.setSnippet(nbrPlacesmj+" - "+typemaj);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String typedel = (String) dataSnapshot.child("type").getValue();
                    String nbrPlacesdel = (String) dataSnapshot.child("nbrPlaces").getValue();
                    marker.setSnippet(String.valueOf(nbrPlacesdel)+" - "+typedel);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/

                //Ajoute la couleur verte
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                //Affiche infowindow
                marker.showInfoWindow();
                markers.put(label, marker);

            //clusterManager.addItem(new  ClusterMarkerLocation(new LatLng(location.latitude,location.longitude)));
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