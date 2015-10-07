package com.example.daniel.przewijaki.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.przewijaki.R;
import com.example.daniel.przewijaki.communicating.DataJSONSingleton;
import com.example.daniel.przewijaki.communicating.EventChanged;
import com.example.daniel.przewijaki.json.DirectionsJSONParser;
import com.example.daniel.przewijaki.locations.ListAdapter;
import com.example.daniel.przewijaki.locations.MyItem;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TabFragment3 extends Fragment {

    private final static String TAG_INFO = "Errors";
    private final static String KEY_EVENT = "SET_LIST";

    private View view;
    private EventBus bus = EventBus.getDefault();

    private String DIALOG_GO = "Prowadź";
    private String DIALOG_OK = "OK";
    private ListView listView;


    private LatLng destPosition;
    private LatLng myPosition;
    public boolean canShow = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_3, container, false);
        Log.d("Errors", "on Create Tab 3");

        bus.register(this);
        return view;
    }


    /**
     * Get event when user choose tab 2 with list of positions
     */
    public void onEvent(EventChanged event) {
        String v = event.getmKeyList();

        if(v.equals(KEY_EVENT))
            loadDataToListView();
    }


    /**
     * Get data and set listView with actionListeners
     */
    private void loadDataToListView() {
        try {
            final List<MyItem> list = DataJSONSingleton.getInstance().getMyItemOnMap();

            listView = (ListView) view.findViewById(R.id.list);

            ListAdapter adapter = new ListAdapter(getActivity(), list);
            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (canShow) {

                        canShow = false;
                        setMyPosition(TabFragment1.getMyLatLng());
                        setDestPosition(list.get(position).getPosition());
                        computeDistanceTime(getDestPosition());

                    } else {
                        Toast.makeText(getActivity(), "Chwileczkę, obliczam...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (NullPointerException e) {
            Log.d("Errors", "Null... in tab 3");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Errors", "onResume " + getClass().toString() + "?" + canShow);
    }


    /* Compute, show distance and duration time between point */
    public void computeDistanceTime(LatLng source) {

        String url = getDirectionsUrl(getMyPosition(), source);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG_INFO, e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            String distance = "";
            String duration = "";

            try {
                if (result.size() < 1) return;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }
                    }
                }
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
                canShow = true;
                return;
            }

            showDialog(getMyPosition(), getDestPosition(), distance, duration);
        }
    }


    /**
     * Show simple DialogWindow and run gps mode
     */
    private void showDialog(LatLng myPos, LatLng destinationPos, String distance,String duration ) {
        Log.d("Errors", "T3 dialog: " + canShow);

        final LatLng source = myPos;
        final LatLng dest = destinationPos;

        LayoutInflater li = LayoutInflater.from(getActivity());
        View view = li.inflate(R.layout.drive_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(view);

        TextView duration_tv = (TextView) view.findViewById(R.id.duration_tv);
        TextView distance_tv = (TextView) view.findViewById(R.id.distance_tv);
        duration_tv.setText("około " + duration);
        distance_tv.setText(distance);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(DIALOG_OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                canShow = true;
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(DIALOG_GO,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String link = "https://maps.google.com/maps?saddr=" + source.latitude + "," + source.longitude + "&daddr=" + dest.latitude + "," + dest.longitude + "&sensor=true";
                                Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(link));
                                myIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(myIntent);
                                canShow = true;
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /* --- Setter & Getters --- */
    public LatLng getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(LatLng myPosition) {
        this.myPosition = myPosition;
    }

    public LatLng getDestPosition() {
        return destPosition;
    }

    public void setDestPosition(LatLng destPosition) {
        this.destPosition = destPosition;
    }

}