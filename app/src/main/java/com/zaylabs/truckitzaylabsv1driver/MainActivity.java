package com.zaylabs.truckitzaylabsv1driver;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    SupportMapFragment sMapFragment;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private StorageReference mImageRef;
    private DatabaseReference mDBRef;
    private FirebaseAuth mAuth;
    private String userID;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;



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
        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectDriver();
                }else{
                    disconnectDriver();
                }
            }
        });
        
        
        
        sMapFragment.getMapAsync(this);

    }

    private void connectDriver(){
        Toast.makeText(this,"User is Online!",Toast.LENGTH_SHORT).show();
    }

    private void disconnectDriver(){
        Toast.makeText(this,"User is Offline!",Toast.LENGTH_SHORT).show();
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
    public void onMapReady(GoogleMap googleMap) {

    }
}
