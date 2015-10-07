package com.example.daniel.przewijaki.locations;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.daniel.przewijaki.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MyItem> myItem;


    public ListAdapter(Activity activity, List<MyItem> movieItems) {
        this.activity = activity;
        this.myItem = movieItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView addr = (TextView) convertView.findViewById(R.id.address);
        TextView zip = (TextView) convertView.findViewById(R.id.zip_code);

        // getting myItem data for the row
        MyItem m = myItem.get(position);

        // title
        title.setText(m.getTitle());

        // address
        addr.setText(m.getAddress());

        // zip-code
        zip.setText(m.getmZip_code());

        return convertView;
    }


    @Override
    public int getCount() {
        return myItem.size();
    }


    @Override
    public Object getItem(int position) {
        return myItem.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

}