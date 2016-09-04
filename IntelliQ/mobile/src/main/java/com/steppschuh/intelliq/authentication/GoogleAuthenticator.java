package com.steppschuh.intelliq.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Stephan Schultz on 31.08.2016.
 */
public class GoogleAuthenticator extends BaseAuthenticator implements GoogleApiClient.ConnectionCallbacks
{

    private static final String CLIENT_ID_SERVER = "1008259459239-ic0lmsu9hhl6i929pav41u8smjbbc86s.apps.googleusercontent.com";
    private static final String CLIENT_ID_ANDROID = "1008259459239-k2a5eul8m47hjia98q8o4og0j5v9ohq3.apps.googleusercontent.com";
    private static final String CLIENT_ID_ANDROID_DEBUG = "1008259459239-n4m47n17ggbeg4huoffkuea0vqouj0s8.apps.googleusercontent.com";

    GoogleApiClient googleApiClient;
    GoogleSignInResult googleSignInResult;

    @Override
    public void initialize(Context context)
    {
        super.initialize(context);
        try {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(CLIENT_ID_SERVER)
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception ex) {
            Log.e(TAG, "Unable to initialize Google API Client");
            ex.printStackTrace();
            googleApiClient = null;
        }
    }

    @Override
    public void silentSignIn()
    {
        super.silentSignIn();
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = pendingResult.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>()
            {
                @Override
                public void onResult(GoogleSignInResult result)
                {
                    handleSignInResult(result);
                }
            });
        }
    }

    @Override
    public Intent getSignInIntent()
    {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    @Override
    public void handleIntentResult(Intent intent)
    {
        super.handleIntentResult(intent);
        try {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        } catch (Exception ex) {
            Log.e(TAG, "Unable to handle intent result");
            ex.printStackTrace();
        }
    }

    public void handleSignInResult(GoogleSignInResult result)
    {
        googleSignInResult = result;
        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

            Log.d(TAG, "Google ID: " + googleSignInAccount.getId());
            Log.d(TAG, "Google ID token: " + googleSignInAccount.getIdToken());

            signInSucceeded();
        } else {
            Exception exception = new Exception(result.getStatus().getStatusMessage());
            signInFailed(exception);
        }
    }

    @Override
    public void signOut()
    {
        super.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        if (status.isSuccess()) {
                            signOutSucceeded();
                        } else {
                            Exception exception = new Exception(status.getStatusMessage());
                            signOutFailed(exception);
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.d(TAG, "Google API client connected");
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d(TAG, "Google API client connection suspended");
    }

}
