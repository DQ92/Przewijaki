package com.example.daniel.przewijaki.communicating;


import com.example.daniel.przewijaki.locations.MyItem;

import java.util.ArrayList;
import java.util.List;

public class DataJSONSingleton {

    private static DataJSONSingleton mInstance = null;

    private String mString;
    private List<MyItem> myItemFromJSON;
    private List<MyItem> myItemOnMap;


    private DataJSONSingleton(){
        mString = "";
        myItemFromJSON = new ArrayList<>();
        myItemOnMap = new ArrayList<>();
    }


    public static DataJSONSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new DataJSONSingleton();
        }
        return mInstance;
    }


    public String getString(){
        return mString;
    }
    public void setString(String value){
        mString = value;
    }

    public List<MyItem> getMyItemOnMap() {
        return myItemOnMap;
    }
    public void setMyItemOnMap(List<MyItem> myItem) {
        myItemOnMap = myItem;
    }

    public List<MyItem> getMyItemFromJSON() {
        return myItemFromJSON;
    }
    public void setMyItemFromJSON(List<MyItem> list) {
        myItemFromJSON = list;
    }

}
