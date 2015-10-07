package com.example.daniel.przewijaki.locations;

import android.content.Context;

import com.example.daniel.przewijaki.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * All about markers
 */
public class OwnIconRendered extends DefaultClusterRenderer<MyItem> {

    public OwnIconRendered(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        float color = item.getColor();

        //custom Marker
        if(color!=0.0f){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.nearby));
        }
        else{
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        }
        //markerOptions.icon(item.getId());
        String temp = item.getAddress();
        markerOptions.snippet(temp);
        markerOptions.title(item.getTitle());


        super.onBeforeClusterItemRendered(item, markerOptions);
    }


    @Override
    protected void onClusterRendered(Cluster<MyItem> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }


    @Override
    public MyItem getClusterItem(Marker marker) {
        return super.getClusterItem(marker);
    }
}