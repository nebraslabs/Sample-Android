package com.nebrasapps.sampleproject;


import android.app.Application;

import com.nebrasapps.sampleproject.storage.SharedData;

/**
 * Created by NebrasApps.com on 01/10/2017
 * https://github.com/nebrasapps/Sample-Android/
 */

public class MainApp extends Application {
    private SharedData sharedData;

    @Override
    public void onCreate()
    {
        super.onCreate();
        // creating singleton object to use all over the app
        sharedData = new SharedData(this);

    }
}
