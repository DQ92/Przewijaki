package com.example.daniel.przewijaki.communicating;

/**
 * Created by Daniel on 2015-05-03.
 */
public class ActualRadial {


    private static ActualRadial mInstance = null;

    /* in meters */
    private int mActualRiadial;
    private int mValue;


    private ActualRadial() {
        mActualRiadial = 30 * 1000;
        mValue = mActualRiadial / 10000;
    }


    public static ActualRadial getInstance() {
        if (mInstance == null) {
            mInstance = new ActualRadial();
        }
        return mInstance;
    }


    public int getmActualRiadial() {
        return mActualRiadial;
    }

    public void setmActualRiadial(int mActualRiadial) {
        this.mActualRiadial = mActualRiadial;
    }

    public int getmValue() {
        return mValue;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
    }
}
