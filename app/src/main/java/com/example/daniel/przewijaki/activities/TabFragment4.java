package com.example.daniel.przewijaki.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.daniel.przewijaki.R;

public class TabFragment4 extends Fragment {

    private static final float VERSION = 0.8f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);
        Log.d("Errors", "on Create Tab 4");

        setUi(view);

        return view;
    }

    /**
     * Set widgets
     * @param view
     */
    private void setUi(View view){
        TextView version_tv = (TextView) view.findViewById(R.id.version_tv);
        version_tv.setText("Build Version "+VERSION+" BETA");

        ImageButton emailBtn = (ImageButton) view.findViewById(R.id.email_btn);
        emailBtn.setBackground(getResources().getDrawable(R.drawable.email_btn));

        ImageButton wwwBtn = (ImageButton) view.findViewById(R.id.www_btn);
        wwwBtn.setBackground(getResources().getDrawable(R.drawable.www_btn));

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMeEmail();
            }
        });

        wwwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite();
            }
        });
    }


    private void openWebsite() {
        String url = "http://www.gdzieprzewijac.pl";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }


    private void sendMeEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "danielq44@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Przewijaki Android");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

}
