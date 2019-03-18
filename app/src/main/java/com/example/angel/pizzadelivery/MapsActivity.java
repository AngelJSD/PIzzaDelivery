package com.example.angel.pizzadelivery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button bt_save;
    Bundle bundle;
    String name, pizza;
    int dni, number;
    double lat, lng;
    private Location last;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bundle = getIntent().getExtras();
        name = bundle.getString("NAME");
        pizza = bundle.getString("PIZZA");
        dni = bundle.getInt("DNI");
        number = bundle.getInt("NUMBER");

        bt_save = findViewById(R.id.bt_save);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AdminSQLite admin = new AdminSQLite(MapsActivity.this,
                        "admin", null, 1);

                SQLiteDatabase db = admin.getWritableDatabase();
                ContentValues reg = new ContentValues();
                reg.put("dni", dni);
                reg.put("name", name);
                reg.put("pizza", pizza);
                reg.put("number", number);
                reg.put("lat", Double.toString(lat));
                reg.put("lng", Double.toString(lng));

                db.insert("'order'", null, reg);
                //db.execSQL("INSERT INTO 'order'(number,name,pizza,lat,dni,lng) VALUES (" +number+ ","+name+ ","+pizza+ ","+Double.toString(lat)+ ","+dni+ ","+Double.toString(lng)+ ")" );
                db.close();
                Toast.makeText(MapsActivity.this,
                        "Order saved!",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MapsActivity.this, OrderListActivity.class);
                startActivity(intent);

            }
        });


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

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this,
                    "Please, enable the access to your phone location",
                    Toast.LENGTH_SHORT).show();

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
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
        else {
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //makeUseOfNewLocation(location);

                    last = location;

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
                String locationProvider = LocationManager.NETWORK_PROVIDER;

                last = locationManager.getLastKnownLocation(locationProvider);
                if (last!=null){
                    mMap.setMyLocationEnabled(true);
                }
                else {
                    Toast.makeText(MapsActivity.this,
                            "An error occurred getting your location",
                            Toast.LENGTH_SHORT).show();
                    last.setLatitude(-34);
                    last.setLongitude(151);
                }
            }
            else {
                Toast.makeText(MapsActivity.this,
                        "An error occurred getting your location",
                        Toast.LENGTH_SHORT).show();
                last.setLatitude(-34);
                last.setLongitude(151);
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(last.getLatitude(), last.getLongitude()), 16));
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Toast.makeText(this, "Tap to select delivery location", Toast.LENGTH_LONG).show();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            public void onMapClick(LatLng point){

                mMap.clear();
                lat = point.latitude;
                lng = point.longitude;

                LatLng deliveyLoction = new LatLng(lat, lng);
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pizza_dribbble));

                mMap.addMarker(new MarkerOptions().position(deliveyLoction).title("Delivery location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.pizzadribbble)));
                bt_save.setEnabled(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager != null) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
                            String locationProvider = LocationManager.NETWORK_PROVIDER;
                            last = locationManager.getLastKnownLocation(locationProvider);
                            if (last!=null){
                                mMap.setMyLocationEnabled(true);
                            }
                            else {
                                Toast.makeText(MapsActivity.this,
                                        "An error occurred getting your location",
                                        Toast.LENGTH_SHORT).show();
                                last.setLatitude(-34);
                                last.setLongitude(151);
                            }
                        }
                        else {
                            Toast.makeText(MapsActivity.this,
                                    "An error occurred getting your location",
                                    Toast.LENGTH_SHORT).show();
                            last.setLatitude(-34);
                            last.setLongitude(151);
                        }
                        //Intent intent = new Intent(MapsActivity.this, NewOrderActivity.class);
                        //startActivity(intent);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    last.setLatitude(-34);
                    last.setLongitude(151);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(last.getLatitude(), last.getLongitude()), 16));
                Toast.makeText(this, "Tap to select delivery location", Toast.LENGTH_LONG).show();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
                    public void onMapClick(LatLng point){

                        mMap.clear();
                        lat = point.latitude;
                        lng = point.longitude;

                        LatLng deliveyLoction = new LatLng(lat, lng);
                        //marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pizza_dribbble));

                        mMap.addMarker(new MarkerOptions().position(deliveyLoction).title("Delivery location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.pizzadribbble)));
                        bt_save.setEnabled(true);
                    }
                });

            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }


}
