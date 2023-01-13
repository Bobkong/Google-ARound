package com.google.ar.core.codelabs.arlocalizer;

import android.app.Application;
import android.content.Context;


import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;

public class BaseApplication extends Application {

    private static Context context;

    public static Context getContext(){
        return context;
    }

    /**
     * Initializes Sendbird UIKit
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        PreferenceUtils.init(getApplicationContext());

    }


}
