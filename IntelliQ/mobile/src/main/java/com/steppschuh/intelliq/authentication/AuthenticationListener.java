package com.steppschuh.intelliq.authentication;

/**
 * Created by Stephan Schultz on 31.08.2016.
 */
public interface AuthenticationListener
{

    void onSignIn(AuthenticatorInstance authenticator);
    void onSignInFailed(AuthenticatorInstance authenticator, Throwable throwable);

    void onSignOut(AuthenticatorInstance authenticator);
    void onSignOutFailed(AuthenticatorInstance authenticator, Throwable throwable);

}
