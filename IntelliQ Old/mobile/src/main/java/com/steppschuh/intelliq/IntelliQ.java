package com.steppschuh.intelliq;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.steppschuh.intelliq.api.user.User;


public class IntelliQ extends MultiDexApplication {

    public static final String TAG = "IntelliQ";

    private boolean initialized = false;
    private User user;

    private Activity context;

    public void initialize(Activity context) {
        Log.v(TAG, "Initializing app");

        this.context = context;

        user = new User();

        initialized = true;
    }


    /**
     * Getter & Setter
     */
    public boolean isInitialized() {
        return initialized;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
