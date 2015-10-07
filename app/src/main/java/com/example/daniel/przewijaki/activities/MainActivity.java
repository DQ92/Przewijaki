package com.example.daniel.przewijaki.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.daniel.przewijaki.R;
import com.example.daniel.przewijaki.communicating.ActualRadial;
import com.example.daniel.przewijaki.communicating.EventChanged;
import com.example.daniel.przewijaki.communicating.ToolbarTitleChange;
import com.example.daniel.przewijaki.stab.MyFragmentPagerAdapter;
import com.example.daniel.przewijaki.stab.SlidingTabLayout;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity
        implements ToolbarTitleChange {


    private final static String TAG_INFO = "Errors";
    private final static String KEY_EVENT = "SET_LIST";

    private TextView mTitle, mSubtitle;
    private Toolbar mToolbar;
    private String[] mTabsName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_INFO, "onCreate " + getLocalClassName());

        setContentView(R.layout.activity_main);
        mTabsName = getResources().getStringArray(R.array.tabs_name);

        setUi();
    }


    /**
     * Set all widgets
     */
    private void setUi(){
        // Layout manager that allows the user to flip through the pages
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        // Initialize the Sliding Tab Layout
        final SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.custom_tab_layout, R.id.tabText);
        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                mTitle.setText(mTabsName[position]);

                switch(position) {
                    case 0:
                        mTitle.setText(mTabsName[position]);

                        int i = ActualRadial.getInstance().getmActualRiadial();
                        String t = String.valueOf(i/1000);
                        mSubtitle.setText("Wybrany promień: " + t + " km");
                        break;

                    case 1:
                        mTitle.setText("Lista");
                        mSubtitle.setText("najbliższych przewijaków");

                        EventBus bus = EventBus.getDefault();
                        bus.post(new EventChanged(KEY_EVENT));
                        break;

                    case 2:
                        mTitle.setText(mTabsName[position]);
                        mSubtitle.setText("Napisz do nas!");
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        slidingTabLayout.setDividerColors(Color.BLUE);
        slidingTabLayout.setViewPager(viewPager);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        setSupportActionBar(mToolbar);

        mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mSubtitle = (TextView) mToolbar.findViewById(R.id.toolbar_subtitle);
        String t = String.valueOf(ActualRadial.getInstance().getmActualRiadial()/1000);
        mSubtitle.setText("Wybrany promień: " + t + " km");

    }


    /**
     * Set title on subtitle Toolbar with radial
     * @param title
     */
    @Override
    public void setTitle(String title) {
        mSubtitle.setText("Wybrany promień: " + title + " km");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG_INFO, "onSaveInstanceState " + getClass().getName());
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG_INFO,"onResume "+getClass().getName());
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG_INFO,"onPause "+getClass().getName());
    }

}