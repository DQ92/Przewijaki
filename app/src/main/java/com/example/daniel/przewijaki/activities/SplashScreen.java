package com.example.daniel.przewijaki.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.daniel.przewijaki.R;
import com.example.daniel.przewijaki.communicating.DataJSONSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends Activity {

    private static  int SPLASH_SCREEN_DELAY = 250;
    private final static String TAG_INFO = "Errors";
    private final static int JSONPATH = R.raw.locations;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private DialogAlertGPS mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.d(TAG_INFO,"< -------- onCreate ------- > "+getLocalClassName());

        mDialog = new DialogAlertGPS(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_INFO,"onResume "+getClass().toString());
        new LoadJSONData().execute();
    }


    /**
     * Start MainActivity with tabs
     */
    public void launchActivity() {

        if(!checkPlayServices()){
            return;
        }

        if( !checkGPS() ) {
            mDialog.showDialog();
        } else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_SCREEN_DELAY);

        }
    }


    public boolean checkGPS(){
        LocationManager mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Load data from JSON to String in DataJSONSingleton
     */
    private class LoadJSONData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG_INFO, "Czekaj, pobieram dane... Load JSON");
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                InputStream is = getResources().openRawResource(JSONPATH);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                DataJSONSingleton.getInstance().setString(new String(buffer));

            } catch (IOException e) {
                e.printStackTrace(); //it is checking in TabFragment1
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            launchActivity();
        }
    }

}