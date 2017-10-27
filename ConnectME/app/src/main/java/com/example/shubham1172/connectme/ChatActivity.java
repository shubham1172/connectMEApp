package com.example.shubham1172.connectme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String TAG = "ChatActivity";
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private ListView mListView;
    private Switch privacyMode;
    private ProgressBar progressBar;
    private EditText chatMessage;
    private boolean receiveUpdates;
    private MessageAdapter messageAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private DatabaseReference chatReference;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiveUpdates = false;

        firebaseAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("users").
                child(firebaseAuth.getCurrentUser().getUid());
        chatReference = FirebaseDatabase.getInstance().getReference().child("messages");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();

        progressBar = (ProgressBar)findViewById(R.id.progress_chat);
        chatMessage = (EditText)findViewById(R.id.chat_edit_text);
        mListView  = (ListView) findViewById(R.id.messageListView);

        List<ConnectMessage> connectMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.chat_message, connectMessages);
        mListView.setAdapter(messageAdapter);

        chatReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ConnectMessage connectMessage = dataSnapshot.getValue(ConnectMessage.class);
                messageAdapter.add(connectMessage);
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
        userReference.child("privacy").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!(boolean)dataSnapshot.getValue()) {
                    receiveUpdates = true;
                    startLocationUpdates();
                }else{
                    receiveUpdates = false;
                    stopLocationUpdates();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        RelativeLayout relativeLayout = (RelativeLayout)menu.findItem(R.id.privacy_mode).getActionView();
        privacyMode = (Switch)relativeLayout.findViewById(R.id.show_switch);
        // Sync db with switch
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean privacyValue = (boolean)dataSnapshot.child("privacy").getValue();
                receiveUpdates = !privacyValue;
                privacyMode.setChecked(privacyValue);
                progressBar.setVisibility(ProgressBar.GONE);
                googleApiClient.connect();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Sync changes with db
        privacyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //enable privacy
                    userReference.child("privacy").setValue(true);
                }else{
                    //disable privacy
                    userReference.child("privacy").setValue(false);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.location:
                //open location activity
                startActivity(new Intent(this, LocationActivity.class));
                return true;
            case R.id.sign_out:
                //sign out
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** OnClick handler */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.chat_send_button:
                sendMessage();
                break;
        }
    }

    private void sendMessage(){
        String message = chatMessage.getText().toString().trim();
        if(message.length()!=0){
            // send message
            ConnectMessage connectMessage = new ConnectMessage(
                    message, firebaseAuth.getCurrentUser().getDisplayName());
            chatReference.push().setValue(connectMessage);
            chatMessage.setText("");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected " + receiveUpdates);
        if(receiveUpdates)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        userReference.child("location").child("lat").setValue(location.getLatitude());
        userReference.child("location").child("lng").setValue(location.getLongitude());
    }
}
