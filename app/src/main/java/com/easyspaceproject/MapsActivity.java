package com.easyspaceproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener{

    private GoogleMap mMap;
    static final LatLng INSSET = new LatLng(49.8495161, 3.2874817);
    static final LatLng CAMPUS = new LatLng(49.8374935, 3.3000117);

    private LocationManager locationManager;
    private Location location;
    private String source;
    private Marker maPosition;
    private double latitude;
    private double longitude;
    private float accuracy;

    private TextView latitudeField;
    private TextView longitudeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get Your Current Location
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //MonLocationListener locationListener = new MonLocationListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        latitudeField = (TextView) findViewById(R.id.idvaleurlatitude);
        longitudeField = (TextView) findViewById(R.id.idvaleurlongitude);
    /*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //


        Criteria criteria = new Criteria();
        source = locationManager.getBestProvider(criteria, false);
        onResume();
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        //*/


    }
    protected boolean isRouteDisplayed() {
        return false;
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Zoom sur ...
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAMPUS, 4));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null); //13 normal 15 Hybrid

        //Marqueurs
        final Marker insset = mMap.addMarker(new MarkerOptions().position(INSSET).title("Aurevoir le centre ville !"));
        final Marker campus = mMap.addMarker(new MarkerOptions().position(CAMPUS).title("Notre nouveau campus !"));
        maPosition = mMap.addMarker(new MarkerOptions().title("Vous êtes ici !").position(new LatLng(0, 0)));
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }


    }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates((LocationListener) this);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }




    }

    /*Quand une source est activée
    public void onProviderEnabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_active), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*Quand une source est desactivée
    public void onProviderDisabled(String source) {
        String msg = String.format(getResources().getString(R.string.source_desactive), source);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*Quand le statut de la source change
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
        String msg = String.format(getResources().getString(R.string.source_statut),source, newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*public void onLocationChanged(final Location location){



    }

    */
    /*Quand la position de l'utilisateur change */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

        //String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
        /*String msg = String.format(
                getResources().getString(R.string.nouvelle_position), latitude,
                longitude, accuracy);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();*/

        /*final StringBuilder msg = new StringBuilder("Lat:");
        msg.append(location.getLatitude());
        msg.append("; Lng : ");
        msg.append(location.getLongitude());
        Toast.makeText(msg.toString(),LENGTH_SHORT).show();*/



        latitudeField.setText(String.valueOf(latitude));
        longitudeField.setText(String.valueOf(longitude));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
