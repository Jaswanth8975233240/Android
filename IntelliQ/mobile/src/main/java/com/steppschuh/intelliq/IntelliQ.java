package com.steppschuh.intelliq;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


public class IntelliQ extends MultiDexApplication {

    public static final String TAG = "IntelliQ";

    private boolean initialized = false;

    public void initialize() {
        Log.d(TAG, "Initializing app");

        initialized = true;
    }


    /**
     * Getter & Setter
     */
    public boolean isInitialized() {
        return initialized;
    }
}
