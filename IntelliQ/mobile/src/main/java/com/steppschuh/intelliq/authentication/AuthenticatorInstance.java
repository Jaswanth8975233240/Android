package com.steppschuh.intelliq.authentication;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Stephan Schultz on 31.08.2016.
 */
public interface AuthenticatorInstance
{

    void initialize(Context context);
    void silentSignIn();
    Intent getSignInIntent();
    void handleIntentResult(Intent intent);
    void signOut();

    void registerAuthenticationListener(AuthenticationListener authenticationListener);
    void unregisterAuthenticationListener(AuthenticationListener authenticationListener);

}
