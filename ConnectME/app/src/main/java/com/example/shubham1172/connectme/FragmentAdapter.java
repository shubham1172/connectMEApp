package com.example.shubham1172.connectme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by shubham1172 on 1/10/17.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    public FragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return new SignUpFragment();
            case 2:
                return new SignInFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //SignUp and SignIn
        return 2;
    }
}
