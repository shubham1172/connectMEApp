package com.example.shubham1172.connectme;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by shubham1172 on 26/9/17.
 */

public class DataLoader extends AsyncTaskLoader<String>{

    private String url, mJson = null;

    public DataLoader(Context context, String url){
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading(){
        if(mJson!=null)
            deliverResult(mJson);
        else
            forceLoad();
    }

    @Override
    public void deliverResult(String json){
        mJson = json;
        super.deliverResult(json);
    }

    @Override
    public String loadInBackground() {
        if(url==null)
            return null;
        return DataUtility.fetchData(url);
    }
}
