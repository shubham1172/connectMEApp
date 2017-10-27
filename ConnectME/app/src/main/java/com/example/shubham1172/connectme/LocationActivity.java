package com.example.shubham1172.connectme;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private DatabaseReference usersReference;
    private ChildEventListener mChildEventListener;
    private LatLngBounds.Builder builder;
    private Map<String, Marker> mapdict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        builder = new LatLngBounds.Builder();
        mapdict = new HashMap<String, Marker>();

        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMarkerToMap(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addMarkerToMap(dataSnapshot);
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
        };
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
        usersReference.addChildEventListener(mChildEventListener);
    }

    private void addMarkerToMap(DataSnapshot dataSnapshot) {
        double lat,lng;
        try {
            lat = (double) dataSnapshot.child("location").child("lat").getValue();
            lng = (double) dataSnapshot.child("location").child("lng").getValue();
        }catch (Exception e){
            lat = 0.0;
            lng = 0.0;
        }
        LatLng latLng = new LatLng(lat, lng);
        if((boolean)dataSnapshot.child("privacy").getValue()) {
            return; //privacy ON
        }
        String username = (String) dataSnapshot.child("username").getValue();
        // already exists
        if(mapdict.containsKey(username)){
            mapdict.get(username).setPosition(latLng);
            return;
        }
        // user's marker
        if (username.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            username = "You";
        }
        TextView bubbleText = new TextView(getApplicationContext());
        bubbleText.setText(username);
        IconGenerator generator = new IconGenerator(getApplicationContext());
        generator.setBackground(getDrawable(R.drawable.amu_bubble_mask));
        generator.setContentView(bubbleText);
        Bitmap icon = generator.makeIcon();

        Marker marker = mMap.addMarker(new MarkerOptions().
                position(latLng).
                title(latLng.toString()).
                icon(BitmapDescriptorFactory.fromBitmap(icon)));
        builder.include(latLng);
        mapdict.put(username, marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
    }
}
