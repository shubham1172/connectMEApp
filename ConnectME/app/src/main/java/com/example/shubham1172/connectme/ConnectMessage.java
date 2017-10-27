package com.example.shubham1172.connectme;

import java.util.Calendar;

/**
 *  ConnectMessage class
 *  Contains two member methods, text and username
 */

public class ConnectMessage {

    private String text; //message text
    private String username; //message username
    private long time;

    public ConnectMessage(){

    }

    public ConnectMessage(String text, String username){
        this.text = text;
        this.username = username;
        this.time = Calendar.getInstance().getTimeInMillis();
    }

    public String getText(){
        return text;
    }

    public String getUsername(){
        return username;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }
}
