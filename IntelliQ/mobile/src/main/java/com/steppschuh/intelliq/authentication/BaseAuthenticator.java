package com.steppschuh.intelliq.authentication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephan Schultz on 31.08.2016.
 */
public abstract class BaseAuthenticator implements AuthenticatorInstance
{

    protected static final String TAG = BaseAuthenticator.class.getSimpleName();

    protected List<AuthenticationListener> authenticationListeners = new ArrayList<>();

    @Override
    public void initialize(Context context)
    {
        Log.d(TAG, "Initializing " + this);
    }

    @Override
    public void silentSignIn()
    {
        Log.d(TAG, "Silent sign in using " + this);
    }

    @Override
    public void signOut()
    {
        Log.d(TAG, "Sign out using " + this);
    }

    @Override
    public void handleIntentResult(Intent intent)
    {
        Log.d(TAG, "Handling intent result using " + this);
    }

    public void signInSucceeded() {
        Log.d(TAG, "Sign in using " + this + " succeeded");
        for (AuthenticationListener authenticationListener : authenticationListeners) {
            try {
                authenticationListener.onSignIn(this);
            } catch (Exception ex) {
                Log.w(TAG, "Unable to notify AuthenticationListener: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void signInFailed(Throwable throwable) {
        Log.d(TAG, "Sign in using " + this + " failed: " + throwable.getMessage());
        for (AuthenticationListener authenticationListener : authenticationListeners) {
            try {
                authenticationListener.onSignInFailed(this, throwable);
            } catch (Exception ex) {
                Log.w(TAG, "Unable to notify AuthenticationListener: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void signOutSucceeded() {
        Log.d(TAG, "Sign out using " + this + " succeeded");
        for (AuthenticationListener authenticationListener : authenticationListeners) {
            try {
                authenticationListener.onSignOut(this);
            } catch (Exception ex) {
                Log.w(TAG, "Unable to notify AuthenticationListener: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void signOutFailed(Throwable throwable) {
        Log.d(TAG, "Sign out using " + this + " failed: " + throwable.getMessage());
        for (AuthenticationListener authenticationListener : authenticationListeners) {
            try {
                authenticationListener.onSignOutFailed(this, throwable);
            } catch (Exception ex) {
                Log.w(TAG, "Unable to notify AuthenticationListener: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void registerAuthenticationListener(AuthenticationListener authenticationListener)
    {
        if (authenticationListeners.contains(authenticationListener)) {
            Log.w(TAG, "Not adding duplicate AuthenticationListener: " + authenticationListener);
            return;
        }
        authenticationListeners.add(authenticationListener);
    }

    @Override
    public void unregisterAuthenticationListener(AuthenticationListener authenticationListener)
    {
        if (authenticationListeners.contains(authenticationListener)) {
            authenticationListeners.remove(authenticationListener);
        }
    }

}
