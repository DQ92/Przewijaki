package com.example.daniel.przewijaki.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.daniel.przewijaki.R;


/**
 * Created by Daniel on 2015-04-28.
 */
public class DialogAlertGPS {

    private final Context mContext;

    private String DIALOG_MSG ;//= R.string.dialog_gps_msg;
    private String DIALOG_CANCEL ;//= getResources().getString(R.string.cancel);
    private String DIALOG_OK;// = getResources().getString(R.string.ok);


    public DialogAlertGPS(Context context){
        mContext = context;

        DIALOG_MSG = mContext.getResources().getString(R.string.dialog_gps_msg);
        DIALOG_CANCEL = mContext.getResources().getString(R.string.cancel);
        DIALOG_OK = mContext.getResources().getString(R.string.ok);
    }


    public void showDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(DIALOG_MSG);
        alertDialog.setPositiveButton(DIALOG_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(callGPSSettingIntent);
            }
        });
        alertDialog.setNegativeButton(DIALOG_CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);
            }
        });
        alertDialog.show();
    }
}
