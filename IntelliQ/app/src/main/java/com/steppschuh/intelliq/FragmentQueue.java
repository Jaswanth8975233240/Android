package com.steppschuh.intelliq;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class FragmentQueue extends Fragment implements CallbackReceiver {

    MobileApp app;

    View contentFragment;

    Handler refreshHandler = new Handler();
    Runnable refreshRunable;
    int refreshDelay = 5000;
    boolean shouldRefresh = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentFragment = inflater.inflate(R.layout.fragment_queue, container, false);

        app = (MobileApp) getActivity().getApplicationContext();
        getActivity().setTitle(getString(R.string.queue));

        setupUi();
        updateUi();

        return contentFragment;
    }

    private void setupUi() {
        refreshHandler = new Handler();
        refreshRunable = new Runnable() {
            public void run() {
                if (shouldRefresh) {
                    //app.requestCompanies(FragmentQueue.this);
                }
                refreshHandler.postDelayed(this, refreshDelay);
            }
        };
    }

    private void updateUi() {

    }

    @Override
    public void onCallBackReceived(Bundle data) {
        updateUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldRefresh = false;
        refreshHandler.removeCallbacks(refreshRunable);
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldRefresh = true;
        refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunable, refreshDelay);
    }
}
