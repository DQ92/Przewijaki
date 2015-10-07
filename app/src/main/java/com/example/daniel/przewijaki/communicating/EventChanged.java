package com.example.daniel.przewijaki.communicating;

public class EventChanged {

    private String mKeyList = null;
    private final static String KEY_EVENT = "SET_LIST";


    public EventChanged(String mKeyList) {
        this.mKeyList = mKeyList;
    }


    public String getmKeyList(){
        return mKeyList;
    }

}