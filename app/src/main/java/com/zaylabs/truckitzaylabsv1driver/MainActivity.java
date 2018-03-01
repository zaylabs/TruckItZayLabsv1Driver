package com.zaylabs.truckitzaylabsv1driver;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zaylabs.truckitzaylabsv1driver.fragment.CargoCalculator;
import com.zaylabs.truckitzaylabsv1driver.fragment.HelpFragment;
import com.zaylabs.truckitzaylabsv1driver.fragment.HistoryFragment;
import com.zaylabs.truckitzaylabsv1driver.fragment.ProfileFragment;
import com.zaylabs.truckitzaylabsv1driver.fragment.SettingsFragment;
import com.zaylabs.truckitzaylabsv1driver.fragment.WalletFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;


    SupportMapFragment sMapFragment;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private StorageReference mImageRef;
    private DatabaseReference mDBRef;
    private FirebaseAuth mAuth;
    private String userID;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mRefAvailable;
    private Location mlat;
    private Location mlng;
    private Location mlocation;

    private TextView mNameField, mEmail, mTextViewDP;

    private ImageView mDisplayPic;
    private Switch mWorkingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        sMapFragment = SupportMapFragment.newInstance();




        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDBRef = mDatabase.child("users").child("driver").child(userID);
        mImageRef = mStorageRef.child(userID);
        mRefAvailable= FirebaseDatabase.getInstance().getReference("driversAvailable").child(userID);


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, "User is Signed In", Toast.LENGTH_SHORT).show();
                    getUserInfo();
                } else {
                    Intent intent = new Intent(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, com.zaylabs.truckitzaylabsv1driver.SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mAuth.signOut();

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        mTextViewDP =hView.findViewById(R.id.TextViewDP);

        mEmail = hView.findViewById(R.id.TextViewUserEmail);
        mNameField=hView.findViewById(R.id.TextViewUser);
        mDisplayPic =hView.findViewById(R.id.displaypic);

        mWorkingSwitch = hView.findViewById(R.id.workingSwitch);

        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mWorkingSwitch.isChecked()){
                    connectDriver();
                }else {
                    disconnectDriver();
                }
            }
        });
        navigationView.getMenu().getItem(0).setCheckable(true);
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
        if (!sMapFragment.isAdded()){
            sFm.beginTransaction().add(R.id.map,sMapFragment).commit();}
        else{
            sFm.beginTransaction().show(sMapFragment).commit();}


        sMapFragment.getMapAsync(this);

    }



    private void connectDriver(){
        Toast.makeText(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, "Driver Available", Toast.LENGTH_SHORT).show();

    }


    private void disconnectDriver(){
        Toast.makeText(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, "Driver Not Available", Toast.LENGTH_SHORT).show();
    }

    private void getUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            mNameField.setText(name);
            String email = user.getEmail();
            mEmail.setText(email);
            if (user.getPhotoUrl()!=null){
                String photodp = user.getPhotoUrl().toString();
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View hView =  navigationView.getHeaderView(0);
                navigationView.setNavigationItemSelectedListener(this);
                Picasso.with(hView.getContext()).load(photodp).resize(150,150).centerCrop().into(mDisplayPic);
            }}
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

        int id = item.getItemId();

        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).commit();

        if (id == R.id.Home) {
            if (!sMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map,sMapFragment).commit();
            else
                sFm.beginTransaction().show(sMapFragment).commit();
        }

        else if (id == R.id.Profile) {

            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.ProfileFragment());
            ft.commit();
        } else if (id == R.id.History) {
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.HistoryFragment());
            ft.commit();
        } else if (id == R.id.wallet) {
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.WalletFragment());
            ft.commit();
        } else if (id == R.id.documents) {
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.DocumentsFragment());
            ft.commit();
        } else if (id == R.id.action_settings) {
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.SettingsFragment());
            ft.commit();
        } else if (id == R.id.logout) {
            mAuth.signOut();
        } else if (id == R.id.get_help) {
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.cm, new com.zaylabs.truckitzaylabsv1driver.fragment.HelpFragment());
            ft.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }


    @Override
        public void onMapReady(GoogleMap map) {
        mMap = map;
        enableMyLocation();
        setOnMyLocationButtonClick();
        setOnMyLocationClick();





    }



    private void setOnMyLocationClick() {
        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
    public void onMyLocationClick(@NonNull Location location) {

                Toast.makeText(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
                mlocation = location;
                Map<String, Object> driverAvailable = new HashMap<>();
                driverAvailable.put("currentLocation", mlocation);
                mRefAvailable.updateChildren(driverAvailable);
            }
        });
    }
    private void setOnMyLocationButtonClick() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                // Return false so that we don't consume the event and the default behavior still occurs
                // (the camera animates to the user's current position).
                return false;
            }

        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(com.zaylabs.truckitzaylabsv1driver.MainActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}


