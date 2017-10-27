package com.example.shubham1172.connectme;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    protected static final int RC_CHAT_ACTIVITY = 1;

    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    public void updateUI(final FirebaseUser firebaseUser){
        //update the UI if logged in
        if(firebaseUser!=null&&firebaseUser.isEmailVerified()){
            startActivityForResult(new Intent(MainActivity.this, ChatActivity.class), RC_CHAT_ACTIVITY); //redirect to GroupActivity
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RC_CHAT_ACTIVITY){
            if(resultCode==RESULT_OK){
                //signed out
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
            }else{
                //back button pressed
                finish();
            }
        }
    }
}
