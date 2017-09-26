package com.example.shubham1172.connectme;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by shubham1172 on 26/9/17.
 */

public final class DataUtility {
    private DataUtility(){}

    /**
     * @param stringURL
     * @return new URL object from given string url
     */
    private static URL createUrl(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
        }catch (MalformedURLException e){
            Log.e(DataUtility.class.getSimpleName(),"Error creating URL ",e);
        }
        return url;
    }

    /**
     * @param url
     * @return json response from API
     * @throws IOException
     * Testing now for a get request
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url==null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000); //milliseconds
            urlConnection.setConnectTimeout(15000); //milliseconds
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //check response code
            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else
                Log.e(DataUtility.class.getSimpleName(),"Error response code: "+urlConnection.getResponseCode());
        }catch(IOException e){
            Log.e(DataUtility.class.getSimpleName(), "Problem reading data");
        }finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
            if(inputStream!=null)
                inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * @param inputStream from @makeHttpRequest
     * @return string from request
     * @throws IOException
     * Parses the data from input stream using buffered reader
     * Old memories of JAVA :)
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while(line!=null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    public static String fetchData(String requestURL){

        URL url = createUrl(requestURL);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(DataUtility.class.getSimpleName(),"Error fetching");
        }
        //for now, simply return it without parsing
        return jsonResponse;
    }
}
