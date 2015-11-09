package com.steppschuh.intelliq.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.user.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, User.UserChangedListener, DrawerLayout.DrawerListener {

    private static final int REQUEST_GOOGLE_SIGN_IN = 1921;
    private static final int REQUEST_FACEBOOK_SIGN_IN = 1922;

    IntelliQ app;

    GoogleApiClient googleApiClient;
    ProgressDialog loadingDialog;

    // main layouts
    FrameLayout contentRootLayout;
    DrawerLayout drawerLayout;


    // navigation
    NavigationView navigation;
    RelativeLayout navigationRootLayout;
    TextView userName;
    TextView userDescription;
    ImageView userImage;
    ImageView navigationMoreButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (IntelliQ) getApplication();
        if (!app.isInitialized()) {
            app.initialize(this);
        }

        // setup ui
        initializeNavigation();
        contentRootLayout = (FrameLayout) findViewById(R.id.contentRoot);
        if (savedInstanceState == null) {
            Fragment content = new QueuesListFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(contentRootLayout.getId(), content).commit();
        }

        setStatusBarColor(ContextCompat.getColor(this, R.color.primaryDark));

        // setup authentication
        initializeGoogleSignIn();
    }


    /**
     * Lifecycle
     */
    @Override
    protected void onStart() {
        super.onStart();
        app.getUser().registerUserChangedListener(this);

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(IntelliQ.TAG, "Google sign in cached");
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showloadingDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideLoadingDialog();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.getUser().unregisterUserChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case User.PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, update location
                    app.getUser().updateLocation(this);
                } else {
                    // can't get location, fall back to postal code
                    app.getUser().requestPostalCode(this);
                }
                return;
            }
        }
    }

    /**
     * UI events
     */
    @Override
    public void onUserChanged(User user) {
        updateNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                getSupportFragmentManager().popBackStack();
                return true;
            }
            case R.id.action_sign_in: {
                signInGoogleAccount();
                return true;
            }
            case R.id.action_sign_out: {
                signOutGoogleAccount();
                return true;
            }
            case R.id.action_revoke_access: {
                revokeGoogleAccountAccess();
                return true;
            }
            case R.id.action_settings: {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * General UI
     */
    private void showloadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage(getString(R.string.status_loading));
            loadingDialog.setIndeterminate(true);
        }
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }

    public void setStatusBarColor(int color) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    /**
     * Navigation
     */
    private void initializeNavigation() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(this);

        navigation = (NavigationView) findViewById(R.id.navigation);

        View headerLayout = navigation.inflateHeaderView(R.layout.view_navigation_header);
        navigationRootLayout = (RelativeLayout) headerLayout.findViewById(R.id.navigationContainer);
        userName = (TextView) headerLayout.findViewById(R.id.navigationUserName);
        userDescription = (TextView) headerLayout.findViewById(R.id.navigationUserDescription);
        userImage = (ImageView) headerLayout.findViewById(R.id.navigationUserImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogleAccount();
            }
        });

        navigationMoreButton = (ImageView) headerLayout.findViewById(R.id.navigationMore);
        navigationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem navigationItemAccount = navigation.getMenu().findItem(R.id.navigationItemAccount);
                if (navigationItemAccount.isVisible()) {
                    navigationItemAccount.setVisible(false);
                    navigationMoreButton.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_48dp));
                } else {
                    navigationItemAccount.setVisible(true);
                    navigationMoreButton.setImageDrawable(getDrawable(R.drawable.ic_expand_less_black_48dp));
                }
            }
        });

        updateNavigation();
    }

    public void updateNavigation() {
        if (app.getUser().isSignedIn()) {
            userName.setText(app.getUser().getName());
            userDescription.setText(app.getUser().getMail());

            Picasso.with(this)
                    .load(app.getUser().getPhotoUrl())
                    .transform(new CircleTransformation())
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.no_photo)
                    .into(userImage);

        } else {
            // TODO: show signin buttons
        }

        drawerLayout.requestLayout();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        //Log.d(IntelliQ.TAG, "onDrawerStateChanged: " + newState);
        switch (newState) {
            case DrawerLayout.STATE_DRAGGING: {
                // square up image if not squared yet
                if (userImage.getLayoutParams().width < 0) {
                    /*
                    Log.d(IntelliQ.TAG, "Current image dimensions: " + userImage.getMeasuredWidth() + "x" + userImage.getMeasuredHeight());
                    ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(userImage.getMeasuredHeight(), userImage.getMeasuredHeight());
                    userImage.setLayoutParams(imageParams);
                    userImage.requestLayout();
                    Log.d(IntelliQ.TAG, "New image dimensions: " + userImage.getMeasuredWidth() + "x" + userImage.getMeasuredHeight());
                    */
                }
                break;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camara) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Authentication with Google
     */
    private void initializeGoogleSignIn() {
        try {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    //.requestIdToken(getString(R.string.app_client_id))
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.w(IntelliQ.TAG, "Google API client connection failed");
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception ex) {
            Log.e(IntelliQ.TAG, "Unable to setup Google sign in");
            ex.printStackTrace();
        }
    }

    private void signInGoogleAccount() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
    }

    private void signOutGoogleAccount() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        app.getUser().setGoogleAccount(null);
                    }
                });
    }

    private void revokeGoogleAccountAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        app.getUser().setGoogleAccount(null);
                    }
                });
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(IntelliQ.TAG, "Google sign in result: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            app.getUser().setGoogleAccount(acct);
        } else {
            app.getUser().setGoogleAccount(null);
        }
    }

    /**
     * Getter & Setter
     */
    public FrameLayout getContentRootLayout() {
        return contentRootLayout;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }


}
