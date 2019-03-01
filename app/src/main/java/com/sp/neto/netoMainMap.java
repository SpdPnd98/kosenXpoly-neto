package com.sp.neto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class netoMainMap extends FragmentActivity implements OnMapReadyCallback, SensorEventListener, LocationListener {

    private GoogleMap mMap;
    private ImageButton SOSButton;
    private LatLng myLoc;
    private SensorManager sensorManager;
    private Sensor rotationV;
    private float mDeclination;
    private float[] mRotationMatrix = new float[9]; //getRotationMatrixFromVector will populate mRotationMatrix
    private ProgressBar progressBarMap;
    private DrawerLayout mDrawerLayout;
    private String uid;

    String key = "1";
    FirebaseFirestore keyDB = FirebaseFirestore.getInstance();
    FirebaseStorage picDB = FirebaseStorage.getInstance();
    StorageReference picDBRef = picDB.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neto_main_map);
        uid = getIntent().getExtras().getString("UID");
        //Toast.makeText(this,"neto UID is: " + uid,Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressBarMap = (ProgressBar) findViewById(R.id.progressBarMap);
        progressBarMap.setVisibility(View.VISIBLE);
        /*name = FirebaseDatabase.getInstance().getReference().child(uid).child("displayName").toString();
        contact = FirebaseDatabase.getInstance().getReference().child(uid).child("mobileNo").toString();
        */

        if(!(ContextCompat.checkSelfPermission(netoMainMap.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            // TODO: Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
        }


        //Navigation Drawer: remember to copy paste all these code to all activities required!
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        // For my case, I will start new intents and add these same parts into the new parts
                        Class selectScreen = null;
                        switch (menuItem.getItemId()){
                            case R.id.nav_emergency:
                                if(ContextCompat.checkSelfPermission(netoMainMap.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                    mMap.setMyLocationEnabled(true);
                                }
                                else{
                                    // TODO: Ask for permission
                                    ActivityCompat.requestPermissions(netoMainMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
                                }
                                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Intent i = new Intent(netoMainMap.this,addLocationDetails.class);
                                i.putExtra("Lat",Double.toString(latitude));
                                i.putExtra("Lon",Double.toString(longitude));
                                i.putExtra("UID",uid);
                                startActivityForResult(i,4);
                                break;
                            case R.id.nav_map:
                                break;
                            case R.id.nav_profile:
                                Intent activity = new Intent(netoMainMap.this, readWriteUserDetails.class);
                                activity.putExtra("UID", uid);
                                activity.putExtra("Email",getIntent().getExtras().getString("Email"));
                                activity.putExtra("read",true);
                                sendBroadcast(activity);
                                break;
                            case R.id.nav_contacts:
                                Intent activity2 = new Intent(netoMainMap.this,allCases.class);
                                activity2.putExtra("UID", uid);
                                startActivity(activity2);
                                finish();
                                break;
                            default:
                                break;

                        }


                        return true;
                    }
                });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            //Log.i("onSensorChanged","Triggered!");
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix , event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float) Math.toDegrees(orientation[0]) ;
            bearing += mDeclination;
            updateCamera(bearing);
        }
        else{
            //Log.i("[onSensorChanged]", "Not Triggered!");
        }
    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        //Log.i("Updating camera","Update successful @" + Float.toString(bearing));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    public void onLocationChanged(Location location) {
        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        // getDeclination returns degrees
        mDeclination = field.getDeclination();
        Log.i("Declination",String.valueOf(mDeclination));
    }

    @Override
    public void onProviderEnabled(String string){

    }

    @Override
    public void onProviderDisabled(String string){

    }

    public void onStatusChanged(String string,int something,Bundle bundle){

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
        // TODO: set a loading symbol first, then show activity, then load markers, then hide loading symbol
        mMap = googleMap;
        SOSButton = (ImageButton) findViewById(R.id.SOSbutton);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        try {
        SOSButton.setOnClickListener(SOSLoc);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        myLoc = new LatLng(location.getLatitude(),location.getLongitude());
        //CameraPosition newCamPos = new CameraPosition.Builder(googleMap.getCameraPosition()).target(myLoc).bearing(location.getBearing()).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,16));
        Log.d("Location",String.valueOf(location.getLatitude()) + ", " +String.valueOf(location.getLongitude()));

        // Instantiate Rotation Vector object and sensor
        rotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this,rotationV,SensorManager.SENSOR_DELAY_UI);

        if(ContextCompat.checkSelfPermission(netoMainMap.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
        else{
            // TODO: Ask for permission
            ActivityCompat.requestPermissions(netoMainMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
        }

        FirebaseFirestore caseDB = FirebaseFirestore.getInstance();
        caseDB.collection("casesLatLng").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map latLng = document.getData();
                                LatLng newMarkerLoc = new LatLng(Double.valueOf(latLng.get("Lat").toString()),Double.valueOf(latLng.get("Lon").toString()));
                                mMap.addMarker(new MarkerOptions().position(newMarkerLoc).title(String.valueOf(document.getData().get("Lat")) + " , " + String.valueOf(document.getData().get("Lon")))
                                        .snippet(String.valueOf(document.getData().get("Details"))));
                                //Log.d("ReadDB", document.getId() + " => " + document.getData());

                            }
                        } else {
                             Log.w("ReadDB", "Error getting documents: ", task.getException());
                        }
                    }
                });
        progressBarMap.setVisibility(View.GONE);}
        catch (SecurityException e){
            Log.d("Security Error","Waiting for user to accept location");

            onMapReady(mMap);
        }

        mMap.setOnInfoWindowLongClickListener(markerClicked);
    }

    public View.OnClickListener SOSLoc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(ContextCompat.checkSelfPermission(netoMainMap.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
            }
            else{
                // TODO: Ask for permission
                ActivityCompat.requestPermissions(netoMainMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
            }
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Intent i = new Intent(netoMainMap.this,addLocationDetails.class);
            i.putExtra("Lat",Double.toString(latitude));
            i.putExtra("Lon",Double.toString(longitude));
            startActivityForResult(i,4);


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            double latitude = data.getExtras().getDouble("Lat");
            double longitude = data.getExtras().getDouble("Lon");
            myLoc = new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(myLoc).title(String.valueOf(latitude) + " , " + String.valueOf(longitude))
            .snippet(data.getExtras().getString("Details")));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLoc),10,null);
        }
    }

    public GoogleMap.OnInfoWindowLongClickListener markerClicked = new GoogleMap.OnInfoWindowLongClickListener() {
        @Override
        public void onInfoWindowLongClick(Marker marker) {
            String keyMatch = marker.getSnippet();
            keyDB.collection("casesLatLng").whereEqualTo("Details",keyMatch).limit(1).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //Log.i("Entered search start" , "Here we are.......");
                            if (task.isSuccessful()) {
                                Log.d("task size",String.valueOf(task.getResult().size()));
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.i("Entered foreach loop" , "=============================================================");
                                    key = document.getId();
                                    //Log.i("key value:",key);
                                }
                            } else {
                                Log.w("ReadDB", "Error getting documents: ", task.getException());
                            }
                            Log.d("onComplete","onCOmplete is finished");
                            Intent openUpView = new Intent(netoMainMap.this,viewCase.class);
                            openUpView.putExtra("KEY",key);
                            Log.i("key value:",key);
                            openUpView.putExtra("UID",uid);
                            startActivity(openUpView);
                        }
                    });


        }
    };
}
