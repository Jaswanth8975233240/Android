package com.steppschuh.intelliq.api.user;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;

import java.util.ArrayList;

public class User {

    public static final int PERMISSION_REQUEST_SETTINGS = 0;
    public static final int PERMISSION_REQUEST_LOCATION = 1;

    private String name;
    private String mail;
    private String idToken;
    private String photoUrl;

    private String postalCode;
    private float latitude;
    private float longitude;

    private GoogleSignInAccount googleAccount;

    private ArrayList<Long> queueEntryIds;
    private ArrayList<Long> queueItemEntryIds;

    private ArrayList<LocationChangedListener> locationChangedListeners;
    private ArrayList<UserChangedListener> userChangedListeners;

    public User() {
        latitude = -1.0f;
        longitude = -1.0f;

        queueEntryIds = new ArrayList<>();
        queueItemEntryIds = new ArrayList<>();
        locationChangedListeners = new ArrayList<>();
        userChangedListeners = new ArrayList<>();
    }

    /**
     * Account handling
     */
    public boolean isSignedIn() {
        if (googleAccount != null) {
            return true;
        }

        return false;
    }

    public void updateFromGoogleAccount() {
        Log.d(IntelliQ.TAG, "Updating user from Google account");
        if (googleAccount != null) {
            name = googleAccount.getDisplayName();
            mail = googleAccount.getEmail();
            idToken = googleAccount.getIdToken();
            photoUrl = googleAccount.getPhotoUrl().toString();
        }

        notifyUserChangedListeners();
    }

    /**
     * Location handling
     */
    public void updateLocation(Activity context) {
        Log.v(IntelliQ.TAG, "Location update requested");
        if (!hasGrantedLocationPermission(context)) {
            Log.w(IntelliQ.TAG, "Location permission not yet granted");
            setLocation(-1.0f, -1.0f);
            requestLocationPermission(context, false);
            return;
        }

        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //noinspection ResourceType
            Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //noinspection ResourceType
            Location networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation == null && networkLocation == null) {
                throw new Exception("No location info available");
            }

            boolean useNetwork = false;

            if (gpsLocation == null) {
                useNetwork = true;
            } else {
                if (gpsLocation.getTime() < networkLocation.getTime()) {
                    useNetwork = true;
                }
            }

            if (useNetwork) {
                setLocation((float) networkLocation.getLatitude(), (float) networkLocation.getLongitude());
            } else {
                setLocation((float) gpsLocation.getLatitude(), (float) gpsLocation.getLongitude());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            setLocation(-1, -1);
        }
    }

    public boolean hasGrantedLocationPermission(Activity context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity context, boolean force) {
        if (!hasGrantedLocationPermission(context)) {

            // Should we show an explanation?
            if (!force && ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationPermissionRationale(context);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
        }
    }

    public void showLocationPermissionRationale(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.rationale_location_title));
        builder.setMessage(context.getString(R.string.rationale_location_message));

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.action_got_it), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestLocationPermission(context, true);
            }
        });

        builder.show();
    }

    public void requestPostalCode(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.request_postal_code_title));
        builder.setMessage(context.getString(R.string.request_postal_code_message));

        // Calculate and apply margin for input
        Resources resources = context.getResources();
        int horizontalMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, resources.getDisplayMetrics()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(layoutParams);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (postalCode != null) {
            input.setText(postalCode);
        }
        container.setPadding(horizontalMargin, 0, horizontalMargin, 0);
        container.addView(input, layoutParams);
        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String postalCode = input.getText().toString();
                if (isValidPostalCode(postalCode)) {
                    setPostalCode(postalCode);
                    dialog.dismiss();
                } else {
                    Log.w(IntelliQ.TAG, "Invalid postal code provided");
                    input.setError(context.getString(R.string.error_invalid_postal_code));
                }
            }
        });

        builder.show();
    }

    public void openPermissionSettings(Activity context) {
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(settingsIntent, PERMISSION_REQUEST_SETTINGS);
    }

    public boolean hasValidPostalCode() {
        return isValidPostalCode(postalCode);
    }

    public static boolean isValidPostalCode(String code) {
        if (code != null && code.length() > 0) {
            // TODO: validate better
            return true;
        }
        return false;
    }

    public boolean hasValidLocation() {
        return isValidLocation(latitude, longitude);
    }

    public static boolean isValidLocation(float latitude, float longitude) {
        if (latitude != -1.0f && longitude != -1.0f) {
            return true;
        }
        return false;
    }

    /**
     * Location observer handling
     */
    public void registerLocationChangedListener(LocationChangedListener locationChangedListener) {
        if (!locationChangedListeners.contains(locationChangedListener)) {
            locationChangedListeners.add(locationChangedListener);
        }
    }

    public void unregisterLocationChangedListener(LocationChangedListener locationChangedListener) {
        if (locationChangedListeners.contains(locationChangedListener)) {
            locationChangedListeners.remove(locationChangedListener);
        }
    }

    public void notifyLocationChangedListeners(float latitude, float longitude) {
        for (LocationChangedListener locationChangedListener : locationChangedListeners) {
            try {
                locationChangedListener.onLocationChanged(latitude, longitude);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void notifyLocationChangedListeners(String postalCode) {
        for (LocationChangedListener locationChangedListener : locationChangedListeners) {
            try {
                locationChangedListener.onLocationChanged(postalCode);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * User observer handling
     */
    public void registerUserChangedListener(UserChangedListener userChangedListener) {
        if (!userChangedListeners.contains(userChangedListener)) {
            userChangedListeners.add(userChangedListener);
        }
    }

    public void unregisterUserChangedListener(UserChangedListener userChangedListener) {
        if (userChangedListeners.contains(userChangedListener)) {
            userChangedListeners.remove(userChangedListener);
        }
    }

    public void notifyUserChangedListeners() {
        for (UserChangedListener userChangedListener : userChangedListeners) {
            try {
                userChangedListener.onUserChanged(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Interfaces
     */
    public interface LocationChangedListener {
        public abstract void onLocationChanged(float latitude, float longitude);
        public abstract void onLocationChanged(String postalCode);
    }

    public interface UserChangedListener {
        public abstract void onUserChanged(User user);
    }

    /**
     * Getter & Setter
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        notifyLocationChangedListeners(postalCode);
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        notifyLocationChangedListeners(latitude, longitude);
    }

    public ArrayList<Long> getQueueEntryIds() {
        return queueEntryIds;
    }

    public void setQueueEntryIds(ArrayList<Long> queueEntryIds) {
        this.queueEntryIds = queueEntryIds;
    }

    public ArrayList<Long> getQueueItemEntryIds() {
        return queueItemEntryIds;
    }

    public void setQueueItemEntryIds(ArrayList<Long> queueItemEntryIds) {
        this.queueItemEntryIds = queueItemEntryIds;
    }

    public GoogleSignInAccount getGoogleAccount() {
        return googleAccount;
    }

    public void setGoogleAccount(GoogleSignInAccount googleAccount) {
        this.googleAccount = googleAccount;
        updateFromGoogleAccount();
    }
}
