package com.steppschuh.intelliq;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class MobileApp extends Application {

    public static final String TAG = "intelliq";

    public boolean isInitialized = false;
    private Activity contextActivity;

    List<Company> companies = new ArrayList<>();

    /**
     * Methods for initializing the app
     */
    public void initialize(Activity contextActivity) {
        Log.d(TAG, "Initializing app");

        this.contextActivity = contextActivity;

        try	{
            initializeHelpers();

            //Invoke asynchronous initialization
            initializeAsync();

            Log.d(TAG, "Initialization done");
            isInitialized = true;
        } catch (Exception ex) {
            Log.e(TAG, "Error during initialization!");
            ex.printStackTrace();
            isInitialized = false;
        }
    }

    private void initializeHelpers() throws Exception {
        Log.d(TAG, "Initializing helpers");

    }

    /**
     * Methods for initializing the app asynchronously
     */
    public void	initializeAsync() {
        (new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "Initializing asynchronously");

                requestCompanies(null);

                Log.d(TAG, "Asynchronously initialization done");
            }
        }).start();
    }

    public void requestCompanies(final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting companies");
        companies = new ArrayList<Company>();

        try {
            Ion.with(contextActivity)
                    .load(ApiHelper.getAllCompaniesUrl())
                    .setTimeout(5000)
                    //.basicAuthentication(API_USER, API_PASS)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }

                            JsonArray companiesArray = result.getAsJsonArray("companies");
                            for (JsonElement companyEntry : companiesArray) {
                                try {
                                    companies.add(Company.parseFromJson((JsonObject) companyEntry));
                                } catch (Exception ex) {
                                    Log.e(TAG, "Unable to parse company: " + ex.getMessage());
                                    //ex.printStackTrace();
                                }
                            }

                            Log.d(TAG, "Companies received: " + companies.size());

                            if (callbackReceiver != null) {
                                callbackReceiver.onCallBackReceived(null);
                            } else {
                                ((MainActivity) contextActivity).showCompanies();
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting companies");
            ex.printStackTrace();
        }
    }




    public Activity getContextActivity() {
        return contextActivity;
    }

    public void setContextActivity(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
