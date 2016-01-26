package com.easyspaceproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.easyspaceproject.Parking;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    static final LatLng INSSET = new LatLng(49.8495161, 3.2874817);
    static final LatLng CAMPUS = new LatLng(49.8374935, 3.3000117);
    static final LatLng FRANCE = new LatLng(46.2157467, 2.2088257);


    private LocationManager locationManager;
    private Location location;
    private String source;

    private double latitude;
    private double longitude;
    private float accuracy;

    private TextView latitudeField;
    private TextView longitudeField;
    //private TextView rechercheField;


    private Button ajouterBouton;

    private Button tarifBouton;
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
        /*FireBase*/
        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Create a new Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);

        new Firebase("https://easyspaceproject.firebaseio.com/EasySpace")
                .addChildEventListener(new ChildEventListener() {
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        adapter.add((String) dataSnapshot.child("text").getValue());
                    }

                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        adapter.remove((String) dataSnapshot.child("text").getValue());
                    }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

        //Loading
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Chargement...");
        progress.setMessage("Veuillez patienter, chargement en cours...");
        progress.show();



        //Création de la bdd
        //SpacesBDD spaceBdd = new SpacesBDD(this);

        /* BDD
        Space space = new Space("Gratuite",latitude,longitude,"Occupée");
        spaceBdd.open();
        spaceBdd.insertSpace(space);
        Space spaceFromBdd = spaceBdd.getSpaceWithLatLong(space.getLatitudeSpace());
        if(spaceFromBdd != null){
            Toast.makeText(this,spaceFromBdd.toString(),Toast.LENGTH_LONG).show();
            spaceFromBdd.setLatitudeSpace(48.5422525);
            spaceBdd.updateSpace(spaceFromBdd.getId(), spaceFromBdd);
        }
        spaceFromBdd = spaceBdd.getSpaceWithLatLong(48.5422525);
        if(spaceFromBdd != null){
            Toast.makeText(this,spaceFromBdd.toString(),Toast.LENGTH_LONG).show();
            spaceBdd.removeSpaceWithID(spaceFromBdd.getId());
        }

        spaceFromBdd = spaceBdd.getSpaceWithLatLong(48.5422525);
        if(spaceFromBdd ==null) {
            Toast.makeText(this, "Notre bdd ne connaît pas cette place", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Cette place est déjà référencé",Toast.LENGTH_LONG).show();
        }
        spaceBdd.close();

        */

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //POUR CLASSE
        // MonLocationListener locationListener = new MonLocationListener();

        //LES SOURCES
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);

        latitudeField = (TextView) findViewById(R.id.idvaleurlatitude);
        longitudeField = (TextView) findViewById(R.id.idvaleurlongitude);
        //rechercheField = (TextView) findViewById(R.id.idrecherche);
        //rechercheField.setHint("Rechercher");

        ajouterBouton = (Button) findViewById(R.id.idboutonajouter);
        ajouterBouton.setOnClickListener(this);
        tarifBouton = (Button) findViewById(R.id.idboutontarif);
        tarifBouton.setOnClickListener(this);
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
    }

    protected boolean isRouteDisplayed() {
        return false;
    }

    //Méthode déclencher au clique sur un bouton
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.idboutonajouter:
                ajouterSpace();
                break;
            default:
                break;
        }
    }

    private void ajouterSpace() {


        Geocoder geo = new Geocoder(MapsActivity.this);

        //ALERTBOX
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.alertdialogperso, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        TextView maPosition = new TextView(this);

        adb.setView(alertDialogView);
        adb.setTitle("Ajouter une place de parking");
        adb.setIcon(android.R.drawable.ic_dialog_alert);


        try {
            List<Address> adresses = geo.getFromLocation(latitude, longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);
                String rue = adresse.getAddressLine(0);
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


                final CheckBox checkBox = (CheckBox) findViewById(R.id.idcheckBox);
                String type = null;
                /*if(checkBox.isChecked()){
                    type = "gratuite";
                }*/



                LatLng position = new LatLng(latitude, longitude);

                Geocoder geo = new Geocoder(MapsActivity.this);
                List<Address> adresses = null;
                try {
                    adresses = geo.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //AJOUT FIREBASE
                Address adresse = adresses.get(0);
                String rue = adresse.getAddressLine(0);
                String cp = adresse.getPostalCode();
                String ville = adresse.getLocality();

                Integer nbrPlaces = 500;
                String phone = "00 00 00 00 00";
                String label = rue;

                Firebase ref = new Firebase("https://glaring-inferno-9753.firebaseio.com/EasySpace/parking");
                Firebase EasySpace = ref.child("places");
                Parking parking = new Parking(label,rue,cp,ville,phone,latitude,longitude,nbrPlaces);
                EasySpace.push().setValue(parking);


                final Marker newplace = mMap.addMarker(new MarkerOptions()
                        .title("Parking ajouté !").snippet("(" + latitude + " ; " + longitude + ")")
                        .position(position));
                newplace.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                String msg = " Place ajoutée au Park !";
                Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();

            }
        });

        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*finish();*/
            }
        });
        adb.show();

        //FIN DIALOGBOX

    }

    /*Quand la position de l'utilisateur change */
    @Override
    public void onLocationChanged(Location location) {


        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

        /*String myLocation = "Latitude : " + latitude + "\nLongitude : " + longitude;
        Toast.makeText(this,myLocation,Toast.LENGTH_LONG).show();*/

        /*ERROR
        String msg = String.format(
                getResources().getString(R.string.nouvelle_position), latitude,
                longitude, accuracy);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();*/

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
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //Type Satellite (trop energivore)
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Zoom sur ...
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FRANCE, 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6), 4000, null); //13 normal 15 Hybrid 5 France

        //Marqueurs
        final Marker insset = mMap.addMarker(new MarkerOptions().position(INSSET).title("INSSET").snippet("Aurevoir le centre ville !"));
        final Marker campus = mMap.addMarker(new MarkerOptions().position(CAMPUS).title("IUT").snippet("Notre nouveau campus !"));


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
}

