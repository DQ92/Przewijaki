package com.example.daniel.przewijaki.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.example.daniel.przewijaki.R;
import com.example.daniel.przewijaki.communicating.ActualRadial;
import com.example.daniel.przewijaki.communicating.DataJSONSingleton;
import com.example.daniel.przewijaki.communicating.StaticData;
import com.example.daniel.przewijaki.communicating.ToolbarTitleChange;
import com.example.daniel.przewijaki.json.ParseJSON;
import com.example.daniel.przewijaki.locations.MyItem;
import com.example.daniel.przewijaki.locations.OwnIconRendered;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class TabFragment1 extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GpsStatus.Listener {

    ToolbarTitleChange ttc;

    private static final String TAG_INFO = "Errors";

    private GoogleMap mMap = null;
    private MapView mMapView = null;
    private ClusterManager<MyItem> mClusterManager;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 30 * 1000; // 10 sec
    private static int FATEST_INTERVAL = UPDATE_INTERVAL / 2;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static float DISPLACEMENT = 250;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private Location mLastLocation;

    private static LatLng myLatLng = null;
    private final LatLng cameraLatLng = new LatLng(52.069167, 19.480556); //na Polske
    private final static float cameraZoom = 5.7f;

    public static int actualRadian = 31 * 1000;

    private View view;

    protected boolean isGPSenable, isConnect;
    private DialogAlertGPS mDialog;


    private int newValue = 0;
    private boolean first;
    private NumberPicker mNumberPicker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_INFO, "onCreate "+getClass().getName());

        mLocationRequest = LocationRequest.create();
        mDialog = new DialogAlertGPS(getActivity());

        // GPS enabled on device
        LocationManager locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locMgr.addGpsStatusListener(this);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_INFO, "onCreateView "+getClass().getName() );

        //bus.register(this);
        view = inflater.inflate(R.layout.tab_fragmernt_1, container, false);

        // Parse data from String
        new ParseJSON().execute();

        ttc = (ToolbarTitleChange) getActivity();
        first = true;

        setUi();

        // check availability of play services and then run GOOGLE MAP
        if(checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            setGoogleMap(savedInstanceState);
            setUpMapSettings();

            setClusterManager();
        }
        return view;

    }


    /**
     * Set widgets on map
     */
    private void setUi() {

        ImageButton btn = (ImageButton) view.findViewById(R.id.radius_btn);
        btn.setBackground(getResources().getDrawable(R.drawable.button_radius));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseRadialInDialog();
            }
        });
    }


    /* ------ Add markers on map ------ */
    private class AddMarkersTask extends AsyncTask<Void, Integer, Void> {

        private double maxDistance;
        MyItem min=null;
        List<MyItem> tempList = new ArrayList<>();

        public AddMarkersTask(int v){
            super();
            maxDistance = (double) v;
            Toast.makeText(getActivity(),"Szukam...", Toast.LENGTH_SHORT).show();
        }

        protected void onPreExecute() {

            mClusterManager.clearItems();
            mMap.clear();
            tempList.clear();
        }

        protected Void doInBackground(Void... params) {
            int j=0;
            double temp, m = 10000 * 1000;

            // Find nearby position
            for(MyItem tempItem : DataJSONSingleton.getInstance().getMyItemFromJSON()) {
                temp=0;
                try{
                    temp = SphericalUtil.computeDistanceBetween(getMyLatLng(), tempItem.getPosition() );
                }catch(NullPointerException ex){
                    Log.d(TAG_INFO," Null Adding..");
                }

                if( temp <= maxDistance){
                    double lat1 = tempItem.getPosition().latitude;
                    double lng1 = tempItem.getPosition().longitude;
                    String title = tempItem.getTitle();
                    String addr = tempItem.getAddress();
                    String zip_code = tempItem.getmZip_code();

                    tempList.add(new MyItem(lat1, lng1, title, addr, zip_code, 0, 0.0f));

                    if( temp<m ){
                        m=temp;
                        min = new MyItem(lat1, lng1, title, addr, zip_code, 0, 120.0f);
                        j=tempList.size()-1;
                    }
                }
            }

            // Add markers on map
            for(int y=0; y<tempList.size();y++) {
                if(y != j){

                    mClusterManager.addItem(tempList.get(y));
                } else{
                    if(min != null){
                        mClusterManager.addItem(min);
                    }
                }
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            // Actual new tempList with markers on map
            DataJSONSingleton.getInstance().setMyItemOnMap(tempList);

            if(min != null) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(getMyLatLng());
                builder.include(min.getPosition());
                LatLngBounds bounds = builder.build();

                int padding = 200; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                mMap.moveCamera(cu);
                mMap.animateCamera(cu);

            }else{
                Toast.makeText(getActivity(),"W poliżu nie ma żadnego punktu. \nProszę wybrać większy promień.", Toast.LENGTH_LONG).show();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), 12f));
            }

            CircleOptions co = new CircleOptions()
                    .center(getMyLatLng())
                    .radius(getActualDistance())
                    .fillColor(0x57b2ebf2)
                    .strokeWidth(0.3f)
                    .visible(true);

            mMap.addCircle(co);
        }
    }


    /**
     * Check distance and compute markers on map
     */
    private void updateMarkers(int value){

        if( value != getActualDistance() && value != 0){

            ttc.setTitle(String.valueOf(value/1000));
            ActualRadial.getInstance().setmActualRiadial(value);

            actualRadian = value;
            new AddMarkersTask(value).execute();
        }
    }


    /**
     * Settings for dialog with number picker
     */
    public void chooseRadialInDialog(){

        LayoutInflater li = LayoutInflater.from(getActivity());
        View v = li.inflate(R.layout.radius_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(v);

        mNumberPicker = (NumberPicker) v.findViewById(R.id.numberpicker);

        mNumberPicker.setOrientation(LinearLayout.HORIZONTAL);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(7);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setDisplayedValues(StaticData.values);
        mNumberPicker.setValue( ActualRadial.getInstance().getmValue() );

        mNumberPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(oldVal != newVal) {
                    ActualRadial.getInstance().setmValue(newVal);

                    newValue = Integer.parseInt( StaticData.values[newVal-1] )*1000;
                    first=false;
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Wybierz",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int va = newValue;
                        if (first) va = 31 *1000;
                        updateMarkers(va);
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_INFO,"onStart "+getClass().getName());

         mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG_INFO,"onStop "+getClass().getName());
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG_INFO,"onResume "+getClass().getName());

        checkPlayServices();

        // if data from JSON are wrong, close apk!
        if(DataJSONSingleton.getInstance().getMyItemFromJSON() == null){
            Toast.makeText(getActivity(),"Przykro mi, nie załadowano poprawnie danych. Uruchom pownownie.", Toast.LENGTH_LONG).show();
            System.exit(0);
        }

        // Resuming the periodic location updates
        if(mGoogleApiClient.isConnected() )
            startLocationUpdates();

        mMapView.onResume();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG_INFO,"onDestroy "+getClass().getName());
        super.onDestroy();
        System.exit(0);
        mMapView.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG_INFO,"onPause "+getClass().getName());
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected() ){
            stopLocationUpdates();
        }
        mMapView.onPause();
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }


    /**
     * Creating location request object
    */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG_INFO, "onConnected.."+getClass().getName());

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }


    /**
     * Method to get location
     */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            // if new Location update markers on map
            if( (new LatLng(latitude, longitude)) != getMyLatLng()){

                setMyLatLng(new LatLng(latitude, longitude));
                new AddMarkersTask(getActualDistance()).execute();
            }
        } else {
            isConnect=false;
            if(!isConnect){

                if(!checkGPS())
                    mDialog.showDialog();
            }
            Toast.makeText(getActivity(), "Nie można określić Twojej lokalizacji...", Toast.LENGTH_SHORT).show();
        }
        isConnect=true;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG_INFO, " onLocationChanged ");

        // Assign the new location
        mLastLocation = location;

        // Displaying the new location
        displayLocation();
    }


    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        Log.d(TAG_INFO, "startLocationUpdates ");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        Log.d(TAG_INFO, " stopLocationUpdates ");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG_INFO, "GoogleApiClient connection has been suspend");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG_INFO, "GoogleApiClient connection has failed");
    }


    /**
     * Updates fields based on data stored in the bundle.
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.d(TAG_INFO, "Updating values from bundle");

        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
        }
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY,  mLastLocation);
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(), "This device is not supported.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    /** Set Map! */
    private void setGoogleMap(Bundle savedInstanceState){

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**  Set settings of Google Map */
    private void setUpMapSettings() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings();
        mMap.setIndoorEnabled(true);
    }


    /**
     * Set ClusterManager
     */
    public void setClusterManager(){
        if (getMap() != null) {
            mClusterManager = new ClusterManager<>(getActivity(), mMap);
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            if (getMyLatLng() == null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, cameraZoom));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), cameraZoom));
            mClusterManager.setRenderer(new OwnIconRendered(getActivity(), getMap(), mClusterManager));
        }
    }


    /* --- GPS mode --- */
    /**
     * Listen gps enable on device
     * @param event
     */
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                isGPSenable = true;
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                isGPSenable = false;
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                isGPSenable = true;
                break;
        }
    }


    /** Checking enable GPS on device only */
    public boolean checkGPS(){

        LocationManager mLocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /* --- Setter & Getters --- */
    /**
     * Get map
     * @return
     */
    public GoogleMap getMap() {
        return mMap;
    }

    /**
     * Get my actual position LatLng
     * @return
     */
    public static LatLng getMyLatLng() {
        return myLatLng;
    }

    /**
     * Set my new Location
     * @param l
     */
    private void setMyLatLng(LatLng l){
        myLatLng = l;
    }

    /**
     * Get actual distance to find
     * @return
     */
    public int getActualDistance() {
        return actualRadian;
    }

}