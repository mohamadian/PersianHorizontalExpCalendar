package com.mohamadian.persianhorizontalexpcalendarexample;

import android.app.Application;
import android.content.Context;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Tasnim on 10/29/2017.
 */

public class MyApp  extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}

