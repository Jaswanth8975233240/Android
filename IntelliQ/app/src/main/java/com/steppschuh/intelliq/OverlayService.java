package com.steppschuh.intelliq;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Steppschuh on 28/07/15.
 */
public class OverlayService extends Service {

    public static final String TAG = "intelliq.overlayService";

    public static final String KEY_NUMBER = "number";
    public static final String KEY_COMPANY_NAME = "company_name";


    WindowManager mWindowManager;
    View mView;
    Animation mAnimation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerOverlayReceiver();

        try {
            Bundle data = intent.getExtras();
            showDialog(data.getString(KEY_NUMBER), data.getString(KEY_COMPANY_NAME));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendMessage(String key, String value) {
        Log.d(TAG, "Sending message: " + key + " = " + value);
        Intent localIntent = new Intent(QueueService.BROADCAST_ACTION);
        localIntent.putExtra(key, value);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void showDialog(String ticketNumber, String companyName){
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mView = View.inflate(getApplicationContext(), R.layout.fragment_overlay, null);
        mView.setTag(TAG);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage(QueueService.KEY_DATA_STATUS, QueueService.DATA_STATUS_TICKET_DISMISSED);

                    if (mView != null) {
                        mView.setVisibility(View.GONE);
                    }

                    hideDialog();

                    stopSelf();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        TextView number = (TextView) mView.findViewById(R.id.ticket_number);
        number.setText(ticketNumber);

        TextView title = (TextView) mView.findViewById(R.id.ticket_title);
        title.setText(getString(R.string.notification_title_get_back).replace("[COMPANYNAME]", companyName));

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in);
        mView.startAnimation(mAnimation);
        mWindowManager.addView(mView, mLayoutParams);

        UiHelper.vibrate(this, UiHelper.VIBRATE_DURATION_DEFAULT);
    }

    private void hideDialog(){
        if(mView != null && mWindowManager != null){
            mWindowManager.removeView(mView);
            mView = null;
        }
    }

    @Override
    public void onDestroy() {
        unregisterOverlayReceiver();
        super.onDestroy();
    }

    private void registerOverlayReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(overlayReceiver, filter);
    }

    private void unregisterOverlayReceiver() {
        hideDialog();
        unregisterReceiver(overlayReceiver);
    }


    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "[onReceive]" + action);
            if (action.equals(Intent.ACTION_SCREEN_ON)) {

            }
            else if (action.equals(Intent.ACTION_USER_PRESENT)) {

            }
            else if (action.equals(Intent.ACTION_SCREEN_OFF)) {

            }
        }
    };
}