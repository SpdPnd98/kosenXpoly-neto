package com.sp.neto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class viewProfile extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    FirebaseAuth appAuth = FirebaseAuth.getInstance();
    FirebaseUser user = appAuth.getCurrentUser();
    private Button editProfile;
    private TextView viewDisplayName;
    private TextView viewEmail;
    private TextView viewMobileNumber;
    private String id, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        id = getIntent().getExtras().getString("UID");
        //email = getIntent().getExtras().getString("Email");

        //Navigation Drawer: remember to copy paste all these code to all activities required!
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Log.d("viewProfileIntent",getIntent().getExtras().toString());

        viewMobileNumber = (TextView) findViewById(R.id.contactNumberView);
        viewDisplayName = (TextView) findViewById(R.id.displayNameView);

        viewMobileNumber.setText(getIntent().getExtras().getString("MobileNumber","Failed"));
        viewDisplayName.setText(getIntent().getExtras().getString("DisplayName","Failed"));
        //viewEmail.setText(getIntent().getExtras().get("Email").toString());

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
                        switch (menuItem.getItemId()) {
                            case R.id.nav_emergency:
                                if (ContextCompat.checkSelfPermission(viewProfile.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                } else {
                                    // TODO: Ask for permission
                                    ActivityCompat.requestPermissions(viewProfile.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                                }
                                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Intent i = new Intent(viewProfile.this, addLocationDetails.class);
                                i.putExtra("Lat", Double.toString(latitude));
                                i.putExtra("Lon", Double.toString(longitude));
                                i.putExtra("UID",getIntent().getExtras().getString("UID"));
                                //i.putExtra("Email",getIntent().getExtras().getString("Email"));
                                startActivity(i);
                                finish();
                                break;
                            case R.id.nav_map:
                                Intent activity = new Intent(viewProfile.this, netoMainMap.class);
                                activity.putExtra("UID", getIntent().getExtras().getString("UID"));
                                //activity.putExtra("Email",getIntent().getExtras().getString("Email"));
                                startActivity(activity);
                                finish();
                                break;
                            case R.id.nav_profile:
                                break;
                            case R.id.nav_contacts:
                                Intent activity2 = new Intent(viewProfile.this, allCases.class);
                                activity2.putExtra("UID", getIntent().getExtras().getString("UID"));
                                //activity2.putExtra("Email",getIntent().getExtras().getString("Email"));
                                startActivity(activity2);
                                finish();
                                break;
                            default:
                                //finish();
                                break;

                        }
                        return true;
                    }
                });
        editProfile = (Button) findViewById(R.id.editProfile);
        viewDisplayName.setText(getIntent().getExtras().getString("DisplayName"));
        viewMobileNumber.setText(getIntent().getExtras().getString("MobileNumber"));
       // viewEmail.setText(getIntent().getExtras().getString("Email"));

        editProfile.setOnClickListener(onEditProfile);


    }
    private View.OnClickListener onEditProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(viewProfile.this, EditProfile.class);
            i.putExtra("DisplayName", viewDisplayName.getText().toString());
            //i.putExtra("Email", viewEmail.getText().toString());
            i.putExtra("MobileNumber", viewMobileNumber.getText().toString());
            i.putExtra("UID",id);

            startActivity(i);
            finish();
        }
    };


}
