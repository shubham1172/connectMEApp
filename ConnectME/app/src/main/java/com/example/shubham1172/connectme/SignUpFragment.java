package com.example.shubham1172.connectme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText username, email, password;
    private String tusername, temail, tpassword;
    private final static String TAG = "SignUpFragment ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        username = (EditText)rootView.findViewById(R.id.username_signup);
        email = (EditText)rootView.findViewById(R.id.email_signup);
        password = (EditText)rootView.findViewById(R.id.password_signup);
        Button button = (Button)rootView.findViewById(R.id.button_signup);
        button.setOnClickListener(this);
        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Verifies form data
     * @return
     */
    protected Boolean verify(){
        tusername = username.getText().toString().trim();
        temail = email.getText().toString().trim();
        tpassword = password.getText().toString().trim();
        if(tusername.length()<3||temail.length()<3||tpassword.length()<3){
            Toast.makeText(getActivity(), "Length too short!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * clears form data
     */
    protected void clearForm(){
        username.setText("");
        email.setText("");
        password.setText("");
    }

    /**
     * OnClick handler
     * @param view
     */
    @Override
    public void onClick(View view){
        if(verify()){ //if verified
            mAuth.createUserWithEmailAndPassword(temail, tpassword) //create user
            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Add display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(tusername).build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //send email verification and sign out
                            if(task.isSuccessful()){
                                mAuth.getCurrentUser().sendEmailVerification();
                                mAuth.signOut();
                                Toast.makeText(getActivity(), "Verify your email and login!", Toast.LENGTH_SHORT).show();
                                clearForm();
                            }else{
                                Log.d(TAG, task.getException().toString());
                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Log.d(TAG, task.getException().toString());
                    Toast.makeText(getActivity(), "Sign up failed!", Toast.LENGTH_SHORT).show();
                }
                }
            });
        }
    }
}