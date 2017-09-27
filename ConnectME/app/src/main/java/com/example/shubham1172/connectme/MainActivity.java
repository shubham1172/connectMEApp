package com.example.shubham1172.connectme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private final String TAG = "MainActivity tag";
    private EditText email_edit;
    private EditText pass_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email_edit  = (EditText)findViewById(R.id.home_email);
        pass_edit = (EditText)findViewById(R.id.home_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user if signed in and update the UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Updates UI according to login state
     * @param firebaseUser
     */
    private void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser!=null){
            startActivity(new Intent(this, HomeActivity.class));
        }else{
            pass_edit.setText(""); //clear views
        }
    }

    /**
     * Validates form data
     * @return
     */
    private Boolean getData(){
        String email = email_edit.getText().toString().trim();
        String password = pass_edit.getText().toString().trim();
        if(email.length()>3&&password.length()>3)
            return true;
        Toast.makeText(this,"Please check the form again!", Toast.LENGTH_SHORT).show();
        pass_edit.setText(""); //clear views
        return false;
    }

    /**
     * Sign up button onClick code
     * @param view
     */
    public void signUp(View view){
        if(getData())
            mAuth.createUserWithEmailAndPassword(email_edit.getText().toString(), pass_edit.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Sign in success
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Verify your email to login!", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        updateUI(null);
                    }else{
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Sign up failed!",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    /**
     * Sign in button onClick code
     * @param view
     */
    public void signIn(View view){
        if(getData())
            mAuth.signInWithEmailAndPassword(email_edit.getText().toString(), pass_edit.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user.isEmailVerified())
                            updateUI(user);
                        else {
                            Toast.makeText(MainActivity.this, "Verify your email to login!", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }
}
