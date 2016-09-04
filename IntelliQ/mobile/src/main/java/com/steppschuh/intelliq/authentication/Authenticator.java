package com.steppschuh.intelliq.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.steppschuh.intelliq.api.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephan Schultz on 31.08.2016.
 */
public class Authenticator
{

    private static final String TAG = Authenticator.class.getSimpleName();

    private static final int REQUEST_GOOGLE_SIGN_IN = 1921;

    private static Authenticator instance;

    private User user;
    List<AuthenticatorInstance> authenticators = new ArrayList<>();

    private Authenticator()
    {
    }

    public static Authenticator getInstance() {
        if (instance == null) {
            instance = new Authenticator();
        }
        return instance;
    }

    public static Authenticator from(Context context) {
        Authenticator authenticator = getInstance();

        return authenticator;
    }

    public void addAuthenticator(AuthenticatorInstance authenticator) {
        if (authenticators.contains(authenticator)) {
            Log.w(TAG, "Not adding duplicate AuthenticatorInstance: " + authenticator);
            return;
        }
        authenticators.add(authenticator);
    }

}
