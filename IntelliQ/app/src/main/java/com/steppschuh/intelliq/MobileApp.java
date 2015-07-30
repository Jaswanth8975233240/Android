package com.steppschuh.intelliq;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
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

    QueueService queueService;
    boolean queueServiceBound = false;

    List<Company> companies = new ArrayList<>();
    String userName = "Unknown";
    String queueItemId;

    NotificationManager mNotificationManager;

    List<CallbackReceiver> callbackReceivers = new ArrayList<>();


    /**
     * Methods for initializing the app
     */
    public void initialize(Activity contextActivity) {
        Log.d(TAG, "Initializing app");

        this.contextActivity = contextActivity;

        try	{
            initializeHelpers();

            initializeQueueService();

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

                Log.d(TAG, "Asynchronously initialization done");
            }
        }).start();
    }

    public void initializeQueueService() {
        try {
            registerQueueDataReceiver();
            startQueueService();
            bindQueueService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startQueueService() {
        Log.d(TAG, "Starting queue service");
        Intent intent = new Intent(this, QueueService.class);
        startService(intent);
    }

    public void stopQueueService() {
        Log.d(TAG, "Stopping queue service");
        queueServiceBound = false;
        Intent intent = new Intent(this, QueueService.class);
        stopService(intent);
    }

    public void bindQueueService() {
        Log.d(TAG, "Binding queue service");
        Intent intent = new Intent(this, QueueService.class);
        bindService(intent, queueServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void registerQueueDataReceiver() {
        IntentFilter mStatusIntentFilter = new IntentFilter(QueueService.BROADCAST_ACTION);

        // Instantiates a new DownloadStateReceiver
        ResponseReceiver QueueServiceStateReceiver = new ResponseReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(QueueServiceStateReceiver, mStatusIntentFilter);
    }

    public void showLoadingScreen() {
        ((MainActivity) contextActivity).showLoading();
    }

    public void hideLoadingScreen() {
        if (queueItemId != null) {
            ((MainActivity) contextActivity).showQueue(queueItemId);
        } else {
            ((MainActivity) contextActivity).showCompanies();
        }

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

                //Log.d(TAG, columnName + ": " + columnValue);
            }
        }
        c.close();
        return name;
    }

    public void requestCompanies() {
        if (queueServiceBound) {
            queueService.requestCompanies();
        }
    }

    public void requestQueuedPeople(final String companyId) {
        if (queueServiceBound) {
            queueService.setCurrentCompanyId(companyId);
            queueService.requestQueuedPeople(companyId);
        }
    }

    public void requestQueueEntry(final String companyId, final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting queue entry");

        if (!queueServiceBound) {
            initializeQueueService();
        }

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
                                queueService.setCurrentQueueId(queueItemId);
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
                            requestQueuedPeople(companyId);
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting queue items");
            ex.printStackTrace();
            requestQueuedPeople(companyId);
        }
    }

    public void requestQueueCancel(final String queueItemId, final CallbackReceiver callbackReceiver) {
        Log.d(TAG, "Requesting queue leave");

        String url = ApiHelper.getCancelQueueItemUrl(queueItemId);

        try {
            Ion.with(contextActivity)
                    .load(url)
                    .setTimeout(5000)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                queueService.setCurrentCompanyId(null);
                                queueService.setCurrentQueueId(null);
                                queueService.cancelNotification();

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

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection queueServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to QueueService, cast the IBinder and get QueueService instance
            QueueService.QueueServiceBinder binder = (QueueService.QueueServiceBinder) service;
            queueService = binder.getService();
            queueServiceBound = true;

            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            queueServiceBound = false;
            Log.d(TAG, "onServiceDisconnected");
        }


    };

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiver received data");
            try {
                Bundle data = intent.getExtras();
                if (data != null) {
                    String status = data.getString(QueueService.KEY_DATA_STATUS);
                    if (status != null) {
                        if (status.equals(QueueService.DATA_STATUS_READY)) {
                            //requestCompanies(null);
                        } else if (status.equals(QueueService.DATA_STATUS_COMPANIES_UPDATED)) {
                            notifyCallbackReceivers();
                        } else if (status.equals(QueueService.DATA_STATUS_QUEUE_UPDATED)) {
                            notifyCallbackReceivers();
                        } else if (status.equals(QueueService.DATA_STATUS_TICKET_DISMISSED)) {
                            queueService.cancelNotification();
                            queueService.stopUpdatingData();
                            stopQueueService();

                            queueItemId = null;
                            ((MainActivity) contextActivity).showCompanies();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (queueServiceBound) {
            unbindService(queueServiceConnection);
            queueServiceBound = false;
        }
    }

    public Activity getContextActivity() {
        return contextActivity;
    }

    public void setContextActivity(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    public List<Company> getCompanies() {
        if (queueServiceBound) {
            companies = queueService.getCompanies();
        }
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

    public QueueService getQueueService() {
        return queueService;
    }

    public boolean isQueueServiceBound() {
        return queueServiceBound;
    }

    public ServiceConnection getQueueServiceConnection() {
        return queueServiceConnection;
    }

    public void addCallbackReceiver(CallbackReceiver callbackReceiver) {
        if (!callbackReceivers.contains(callbackReceiver)) {
            callbackReceivers.add(callbackReceiver);
        }
    }

    public void removeCallbackReceiver(CallbackReceiver callbackReceiver) {
        if (callbackReceivers.contains(callbackReceiver)) {
            callbackReceivers.add(callbackReceiver);
        }
    }

    public void notifyCallbackReceivers() {
        for (CallbackReceiver callbackReceiver : callbackReceivers) {
            callbackReceiver.onCallBackReceived(null);
        }
    }
}
