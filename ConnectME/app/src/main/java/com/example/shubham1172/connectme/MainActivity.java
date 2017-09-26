package com.example.shubham1172.connectme;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String>{

    private static final int DATA_LOADER_ID = 1; /** Unique number to identify loader */
    private ProgressBar progressBar;
    private TextView result;
    private String mURL = null;
    LoaderManager loaderManager;
    Boolean flag = false; //hack
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager = getLoaderManager();
        result = (TextView)findViewById(R.id.text_home);
    }

    public void buttonClick(View v){
        mURL = ((EditText)findViewById(R.id.home_URL)).getText().toString();
        if(flag)
            loaderManager.restartLoader(DATA_LOADER_ID, null, this);
        else {
            loaderManager.initLoader(DATA_LOADER_ID, null, this);
            flag = true;
        }
    }

    private void notifyLoading(){
        String message = "Fetching data: "+ mURL;
        Toast notify = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        notify.show();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        notifyLoading();
        return new DataLoader(this, mURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        result.setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
