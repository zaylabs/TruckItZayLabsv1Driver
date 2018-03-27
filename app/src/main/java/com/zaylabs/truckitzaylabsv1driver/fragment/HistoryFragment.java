package com.zaylabs.truckitzaylabsv1driver.fragment;


import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.content.ClipData;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Script;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.nearby.messages.Distance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zaylabs.truckitzaylabsv1driver.MainActivity;
import com.zaylabs.truckitzaylabsv1driver.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.R.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private DatabaseReference mDatabase;
    private ListView mListView;
    private ArrayList<String> mCustomers = new ArrayList<>();
    private String mName;
    private GeoFire mgeoFire;
    private GeoQuery mgeoQuery;
    private double mRadius = 10;
    private DatabaseReference mCustomerLocation;
    private Location mDriverLocation;
    private DatabaseReference mdbUsers;
    private DatabaseReference mCustomerDatabase;
    private DatabaseReference mBaseDB;
    private String mUserID;
    private String mDistance;
    private FirebaseAuth mAuth;
    private String userID;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mBaseDB= FirebaseDatabase.getInstance().getReference();
        mDriverLocation = ((MainActivity) getActivity()).mCurrentLocation;
        mDatabase = FirebaseDatabase.getInstance().getReference("customerRequest");
        mdbUsers = FirebaseDatabase.getInstance().getReference("users").child("customer");
        mListView = (ListView) view.findViewById(R.id.mListView);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(view.getContext(), layout.simple_expandable_list_item_1, mCustomers);
        mListView.setAdapter(arrayAdapter);


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mUserID = dataSnapshot.getKey();
                DataSnapshot mLocation = dataSnapshot.child("PickUpLocation");
                GeoLocation mGeoLocation = GeoFire.getLocationValue(mLocation);
                //mName=dataSnapshot

                if (mGeoLocation != null) {
                    Location loc1 = new Location("");
                    Location loc2 = new Location("");

                    loc1.setLatitude(mDriverLocation.getLatitude());
                    loc1.setLongitude(mDriverLocation.getLongitude());


                    loc2.setLatitude(mGeoLocation.latitude);
                    loc2.setLongitude(mGeoLocation.longitude);

                    float distance = loc1.distanceTo(loc2) / 1000;
                    DecimalFormat decimalFormat = new DecimalFormat("0.##");
                    mDistance = decimalFormat.format(distance);
                    mCustomers.add(mUserID +"-"+ mName+"-" + mDistance+"-"+mGeoLocation );
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    mCustomers.add(mUserID + "-" + mLocation);
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void getCustomerInfo() {

    }

}


