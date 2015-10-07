package com.example.daniel.przewijaki.locations;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class MyItem implements ClusterItem {


    private String mZip_code;
    private LatLng mPosition;
    private String mTitle, mAddress;
    private int mId;
    private float color = 0.0f;


    public MyItem(double lat, double lng , String t, String addr, String zip_code, int i, float color) {
        mPosition = new LatLng(lat, lng);
        this.mTitle = t;
        this.mZip_code = zip_code;
        this.mId = i;
        this.color = color;
        this.mAddress = addr;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    public String getTitle() {
        return mTitle;
    }


    public String getAddress(){ return mAddress; }


    public int getId() {
        return mId;
    }


    public float getColor(){
        return color;
    }


    public String getmZip_code() {
        return mZip_code;
    }


    public void setmZip_code(String mZip_code) {
        this.mZip_code = mZip_code;
    }

    @Override
    public String toString() {
        return String.valueOf(mPosition.latitude) + " " + String.valueOf(mPosition.longitude+ " " + mTitle);
    }
}