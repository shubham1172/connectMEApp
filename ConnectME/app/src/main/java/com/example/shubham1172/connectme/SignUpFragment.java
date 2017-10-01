package com.example.shubham1172.connectme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SignUpFragment extends Fragment {

    public View onCreateView(Bundle savedInstanceState, ViewGroup container, LayoutInflater inflater) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }
}