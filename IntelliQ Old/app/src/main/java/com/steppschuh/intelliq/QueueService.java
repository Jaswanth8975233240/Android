package com.steppschuh.intelliq;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class QueueService extends Service {

    public static final String TAG = "intelliq.queueService";

    public static final String BROADCAST_ACTION = "intelliq.BROADCAST";
    public static final String KEY_DATA_STATUS = "intelliq.STATUS";
    public static final String DATA_STATUS_READY = "intelliq.STATUS.READY";
    public static final String DATA_STATUS_COMPANIES_UPDATED = "intelliq.STATUS.COMPANIES.UPDATED";
    public static final String DATA_STATUS_QUEUE_UPDATED = "intelliq.STATUS.QUEUE.UPDATED";
    public static final String DATA_STATUS_TICKET_DISMISSED = "intelliq.STATUS.TICKET.DISMISSED";


    public static final int NOTIFICATION_ID = 123;

    public static final int REFRESH_INTERVAL_SHORT = 10000;
    public static final int REFRESH_INTERVAL_DEFAULT = 20000;
    public static final int REFRESH_INTERVAL_LONG = 30000;
    public static final int REQUEST_TIMEOUT = 9000;

    int refreshIntervalCompanies = REFRESH_INTERVAL_DEFAULT;
    int refreshIntervalQueue = REFRESH_INTERVAL_DEFAULT;


    final int mStartMode = START_REDELIVER_INTENT;       // indicates how to behave if the service is killed
    IBinder mBinder;                                     // interface for clients that bind
    boolean isAppBound = false;
    boolean mAllowRebind = true;                         // indicates whether onRebind should be used

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    boolean updateData = true;
    String currentCompanyId = null;
    String currentQueueId = null;
    int positionInQueue = -1;
    List<Company> companies = new ArrayList<>();

    Handler refreshHandler = new Handler();
    Runnable refreshCompaniesRunable;
    Runnable refreshQueueRunable;

    NotificationManager mNotificationManager;
    boolean hasReceivedSoonNotification = false;
    boolean hasReceivedNowNotification = false;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }

        /*
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage");

            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

            sendTestMessage();

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }*/
    }

    public class QueueServiceBinder extends Binder {
        QueueService getService() {
            // Return this instance of QueueService so clients can call public methods
            return QueueService.this;
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mBinder = new QueueServiceBinder();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        startUpdatingData();
    }

    public void startUpdatingData() {
        Log.d(TAG, "Starting to update data");
        updateData = true;

        refreshHandler = new Handler();
        refreshCompaniesRunable = new Runnable() {
            public void run() {
                try {
                    if (updateData) {
                        requestCompanies();
                    }
                    adjustRefreshInterval();
                    refreshHandler.postDelayed(this, refreshIntervalCompanies);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        refreshQueueRunable = new Runnable() {
            public void run() {
                try {
                    if (updateData && currentCompanyId != null) {
                        requestQueuedPeople(currentCompanyId);
                    }
                    adjustRefreshInterval();
                    refreshHandler.postDelayed(this, refreshIntervalQueue);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        refreshHandler.postDelayed(refreshCompaniesRunable, 10);
        refreshHandler.postDelayed(refreshQueueRunable, 1000);
    }

    public void stopUpdatingData() {
        updateData = false;
        refreshHandler.removeCallbacks(refreshCompaniesRunable);
        refreshHandler.removeCallbacks(refreshQueueRunable);
    }

    public void adjustRefreshInterval() {
        if (!isAppBound) {
            if (currentQueueId != null) {
                refreshIntervalCompanies = REFRESH_INTERVAL_LONG;
                refreshIntervalQueue = REFRESH_INTERVAL_LONG;
            } else {
                stopUpdatingData();
            }
        } else {
            refreshIntervalCompanies = REFRESH_INTERVAL_SHORT;
            refreshIntervalQueue = REFRESH_INTERVAL_SHORT;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        //sendMessage(KEY_DATA_STATUS, DATA_STATUS_READY);

        hasReceivedSoonNotification = false;
        hasReceivedNowNotification = false;

        if (!updateData) {
            startUpdatingData();
        }

        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(TAG, "onBind");
        isAppBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind");
        isAppBound = false;
        adjustRefreshInterval();
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        isAppBound = true;
        if (!updateData) {
            startUpdatingData();
        }
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        stopUpdatingData();
    }

    public void sendMessage(String key, String value) {
        Log.d(TAG, "Sending message: " + key + " = " + value);
        Intent localIntent = new Intent(BROADCAST_ACTION);
        localIntent.putExtra(key, value);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void requestCompanies() {
        Log.d(TAG, "Requesting companies");

        try {
            Ion.with(this)
                    .load(ApiHelper.getAllCompaniesUrl())
                    .setTimeout(REQUEST_TIMEOUT)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                e.printStackTrace();
                            }

                            try {

                                Company currentCompany = getCurrentCompany();

                                companies = new ArrayList<Company>();

                                if (currentCompany != null) {
                                    companies.add(currentCompany);
                                }

                                JsonArray companiesArray = result.getAsJsonArray("companies");
                                for (JsonElement companyEntry : companiesArray) {
                                    try {
                                        Company company = Company.parseFromJson((JsonObject) companyEntry);

                                        if (currentCompany != null) {
                                            if (!currentCompany.getId().equals(company.getId())) {
                                                companies.add(company);
                                            }
                                        } else {
                                            companies.add(company);
                                        }
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Unable to parse company: " + ex.getMessage());
                                        //ex.printStackTrace();
                                    }
                                }

                                Log.d(TAG, "Companies received: " + companies.size());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting companies");
            ex.printStackTrace();
        }

        sendMessage(KEY_DATA_STATUS, DATA_STATUS_COMPANIES_UPDATED);
    }

    public void requestQueuedPeople(final String companyId) {
        Log.d(TAG, "Requesting queue items");

        try {
            Ion.with(this)
                    .load(ApiHelper.getQueueItemsForCompanyUrl(companyId))
                    .setTimeout(REQUEST_TIMEOUT)
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
                                positionInQueue = -1;

                                for (Company company : companies) {
                                    if (company.getId().equals(companyId)) {
                                        company.setQueueItems(items);
                                        company.setPeopleInQueue(items.size());

                                        for (QueueItem queueItem : company.getQueueItems()) {
                                            if (queueItem.getId().equals(currentQueueId)) {
                                                positionInQueue = company.getQueuedItemsBeforeCount(queueItem);
                                            }
                                        }
                                    }
                                }

                                showNotification(positionInQueue);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Error while requesting queue items");
            ex.printStackTrace();
        }

        sendMessage(KEY_DATA_STATUS, DATA_STATUS_QUEUE_UPDATED);
    }

    public void showNotification(int number) {
        if (number < 0) {
            cancelNotification();
        }

        try {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Create an intent for the reply action
            Intent actionIntent = new Intent(this, MainActivity.class);
            PendingIntent actionPendingIntent =
                    PendingIntent.getActivity(this, 0, actionIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Create the action
            NotificationCompat.Action cancelAction =
                    new NotificationCompat.Action.Builder(R.mipmap.ic_clear_black_36dp,
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
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setOnlyAlertOnce(true)
                    .addAction(cancelAction)
                    .setVibrate(new long[]{0, 200, 500, 200, 500})
                    .extend(wearableExtender);

            Company currentCompany = getCurrentCompany();

            if (number > 1) {
                mNotifyBuilder.setContentTitle(getString(R.string.notification_title_default).replace("[NUMBER]", String.valueOf(number)));
                mNotifyBuilder.setContentText(getString(R.string.notification_content_default).replace("[COMPANYNAME]", currentCompany.getName()));
            } else {
                mNotifyBuilder.setContentTitle(getString(R.string.notification_title_get_back).replace("[COMPANYNAME]", currentCompany.getName()));
                mNotifyBuilder.setContentText(getString(R.string.notification_content_get_back).replace("[NUMBER]", currentQueueId));

                if (number == 0) {
                    if (!hasReceivedNowNotification) {
                        Intent intent = new Intent(this, OverlayService.class);
                        intent.putExtra(OverlayService.KEY_NUMBER, currentQueueId);
                        intent.putExtra(OverlayService.KEY_COMPANY_NAME, currentCompany.getName());

                        startService(intent);
                        hasReceivedNowNotification = true;
                    }
                } else {
                    if (!hasReceivedSoonNotification) {
                        UiHelper.vibrate(this, UiHelper.VIBRATE_DURATION_DEFAULT);
                        hasReceivedSoonNotification = true;
                    }
                }
            }



            mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelNotification() {
        try {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception ex) {
        }
    }

    public Company getCurrentCompany() {
        if (currentCompanyId == null) {
            return null;
        }

        for (Company company : companies) {
            if (company.getId().equals(currentCompanyId)) {
                return company;
            }
        }

        return null;
    }

    public List<Company> getCompanies() {
        //Log.d(TAG, "Companies requested, " + companies.size() + " found");
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public void setCurrentCompanyId(String currentCompanyId) {
        this.currentCompanyId = currentCompanyId;
    }

    public void setCurrentQueueId(String currentQueueId) {
        this.currentQueueId = currentQueueId;
    }
}