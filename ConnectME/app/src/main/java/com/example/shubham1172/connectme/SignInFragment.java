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
import com.google.firebase.auth.FirebaseUser;

public class SignInFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText email, password;
    private String temail, tpassword;
    private final static String TAG = "SignInFragment ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText)rootView.findViewById(R.id.email_signin);
        password = (EditText)rootView.findViewById(R.id.password_signin);
        Button button = (Button)rootView.findViewById(R.id.button_signin);
        button.setOnClickListener(this);
        return rootView;
    }

    /**
     * Verifies form data
     * @return
     */
    protected Boolean verify(){
        temail = email.getText().toString().trim();
        tpassword = password.getText().toString().trim();
        if(temail.length()<3||tpassword.length()<3){
            Toast.makeText(getActivity(), "Length too short!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * clears form data
     */
    protected void clearForm(){
        email.setText("");
        password.setText("");
    }

    @Override
    public void onClick(View v){
        if(verify()){
            mAuth.signInWithEmailAndPassword(temail, tpassword)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified())
                        ((MainActivity) getActivity()).updateUI(mAuth.getCurrentUser());
                    else{
                        mAuth.signOut();
                        clearForm();
                        Toast.makeText(getActivity(), "Verify your email to login!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //Sign in fails
                    Log.d(TAG, task.getException().toString());
                    Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
            });
        }
    }
}
