package com.steppschuh.intelliq;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class MobileApp extends Application {

    public static final String TAG = "intelliq";
    public static final int NOTIFICATION_ID = 123;

    public boolean isInitialized = false;
    private Activity contextActivity;

    List<Company> companies = new ArrayList<>();
    String userName = "Unknown";
    String queueItemId;

    NotificationManager mNotificationManager;


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

                userName = getOwnerName();
                if (userName == null) {
                    userName = android.os.Build.MODEL;
                }

                Log.d(TAG, "User name: " + userName);

                requestCompanies(null);

                Log.d(TAG, "Asynchronously initialization done");
            }
        }).start();
    }

    public String getOwnerMail() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public String getOwnerName() {
        String name = getOwnerMail();

        Cursor c = contextActivity.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        int count = c.getCount();
        String[] columnNames = c.getColumnNames();
        boolean b = c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {
            for (int j = 0; j < columnNames.length; j++) {
                String columnName = columnNames[j];
                String columnValue = c.getString(c.getColumnIndex(columnName));

                if (columnName.equalsIgnoreCase("display_name")) {
                    name = columnValue;
                }

                Log.d(TAG, columnName + ": " + columnValue);
            }
        }
        c.close();
        return name;
    }

    public void requestCompanies(final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting companies");
        companies = new ArrayList<Company>();

        try {
            Ion.with(contextActivity)
                    .load(ApiHelper.getAllCompaniesUrl())
                    .setTimeout(5000)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }

                            try {
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

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

    public void requestQueuedPeople(final String companyId, final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting queue items");

        try {
            Ion.with(contextActivity)
                    .load(ApiHelper.getQueueItemsForCompanyUrl(companyId))
                    .setTimeout(5000)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }

                            try {
                                List<QueueItem> items = new ArrayList<QueueItem>();

                                JsonArray itemsArray = result.getAsJsonArray("qItems");
                                for (JsonElement item : itemsArray) {
                                    try {
                                        items.add(QueueItem.parseFromJson((JsonObject) item));
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Unable to parse queue item: " + ex.getMessage());
                                        //ex.printStackTrace();
                                    }
                                }

                                Log.d(TAG, "Queue items received: " + items.size());

                                for (Company company : companies) {
                                    if (company.getId().equals(companyId)) {
                                        company.setQueueItems(items);
                                        company.setPeopleInQueue(items.size());
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            if (callbackReceiver != null) {
                                callbackReceiver.onCallBackReceived(null);
                            } else {
                                ((MainActivity) contextActivity).showQueue(companyId);
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting queue items");
            ex.printStackTrace();
        }
    }

    public void requestQueueEntry(final String companyId, final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting queue entry");

        String url = ApiHelper.getAddQueueItemUrl(userName, companyId);
        Log.d(TAG, url);

        try {
            Ion.with(contextActivity)
                    .load(url)
                    .setTimeout(5000)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    throw new Exception(e.getMessage());
                                }

                                queueItemId = result.getAsJsonPrimitive("qItemId").getAsString();
                                Log.d(TAG, "Queue item id: " + queueItemId);

                                QueueItem item = new QueueItem();
                                item.setId(queueItemId);
                                item.setCompanyId(companyId);
                                item.setName(userName);
                                item.setCheckinTime((new Date()).getTime());

                                for (Company company : companies) {
                                    if (company.getId().equals(companyId)) {
                                        company.getQueueItems().add(item);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            // update queued people list for that company
                            requestQueuedPeople(companyId, callbackReceiver);
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting queue items");
            ex.printStackTrace();
            requestQueuedPeople(companyId, callbackReceiver);
        }
    }

    public void requestQueueCancel(final String queueItemId, final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting queue leave");

        String url = ApiHelper.getCancelQueueItemUrl(queueItemId);
        Log.d(TAG, url);

        try {
            Ion.with(contextActivity)
                    .load(url)
                    .setTimeout(5000)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    throw new Exception(e.getMessage());
                                }

                                Log.d(TAG, "Queue item canceled: " + queueItemId);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting queue leave");
            ex.printStackTrace();
        }
    }

    public void showNotification(int number) {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the action
        NotificationCompat.Action cancelAction =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                        getString(R.string.cancel_button), actionPendingIntent)
                        .build();

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .addAction(cancelAction)
                        .setHintHideIcon(false);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
                .extend(wearableExtender);

        if (number > 2) {
            mNotifyBuilder.setContentTitle(number + " people in queue");
            mNotifyBuilder.setContentText("You still have a few minutes before you should get back.");
        } else {
            mNotifyBuilder.setContentTitle("Get back, you're next!");
            mNotifyBuilder.setContentText("You should be called up soon, make sure to be there on time.");
        }

        mNotificationManager.notify(
                NOTIFICATION_ID,
                mNotifyBuilder.build());
    }

    public void cancelNotification() {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQueueItemId() {
        return queueItemId;
    }

    public void setQueueItemId(String queueItemId) {
        this.queueItemId = queueItemId;
    }
}
