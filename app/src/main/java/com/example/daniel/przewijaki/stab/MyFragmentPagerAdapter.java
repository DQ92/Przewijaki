package com.example.daniel.przewijaki.stab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ViewGroup;

import com.example.daniel.przewijaki.R;
import com.example.daniel.przewijaki.activities.TabFragment1;
import com.example.daniel.przewijaki.activities.TabFragment3;
import com.example.daniel.przewijaki.activities.TabFragment4;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private int icons[] = {
                            R.drawable.one,
                            R.drawable.three,
                            R.drawable.four };
    private String tabTitles[];
    private Context context;


    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles = context.getResources().getStringArray(R.array.tabs_name);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // nie wiem czy to dobre rozwiązanie ale działa
        // http://stackoverflow.com/a/28064115
        //super.destroyItem(ViewGroup container, int position, Object object);
    }


    @Override
    public int getCount() {
        return icons.length;
    }


    // Return the correct Fragment based on index
    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return new TabFragment1();
        } else if(position == 1) {
            return new TabFragment3();
        }else if(position == 2) {
            return new TabFragment4();
        }
        return null;

    }

    /**
     * Set icon on tabs with titles
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {

        // Generate title based on item position
        Drawable image = context.getResources().getDrawable(icons[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());

        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("  \n" + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}