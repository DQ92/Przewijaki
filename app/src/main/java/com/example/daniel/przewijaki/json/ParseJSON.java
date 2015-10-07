package com.example.daniel.przewijaki.json;

import android.os.AsyncTask;
import android.util.Log;

import com.example.daniel.przewijaki.communicating.DataJSONSingleton;
import com.example.daniel.przewijaki.locations.MyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Daniel on 2015-04-28.
 */
public class ParseJSON extends AsyncTask<Void, Integer, Void> {

    private static final String TAG_INFO = "Errors";

    private List<MyItem> listParse = new ArrayList<>();

    protected void onPreExecute() {
        Log.d(TAG_INFO, "onPre Parse ");
    }

    protected Void doInBackground(Void... params) {

        int i;

        try {
            JSONArray json = new JSONArray( DataJSONSingleton.getInstance().getString() );

            for ( i=0; i<json.length(); i++) {
                JSONObject e = json.getJSONObject(i);
                String point = e.getString("latlng");

                String[] point2 = point.split(",");
                double lat1 = Double.parseDouble(point2[0]);
                double lng1 = Double.parseDouble(point2[1]);

                String title = e.getString("name");
                String address = e.getString("address");
                String zip_code = e.getString("zip_code");
                listParse.add(new MyItem(lat1, lng1, title, address, zip_code, 0, 0));
            }

        } catch (JSONException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        DataJSONSingleton.getInstance().setMyItemFromJSON(listParse);
        Log.d(TAG_INFO,"onPost Parse ");
    }
}