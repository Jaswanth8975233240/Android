package com.steppschuh.intelliq;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class FragmentQueue extends Fragment implements CallbackReceiver {

    MobileApp app;

    String companyId;
    Company currentCompany;
    QueueItem currentItem;

    View contentFragment;

    Handler refreshHandler = new Handler();
    Runnable refreshRunable;
    int refreshDelay = 7000;
    boolean shouldRefresh = true;

    TextView ticketNumber;
    TextView timeLeft;
    TextView peopleInQueue;
    Button leaveQueue;

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
        ticketNumber = (TextView) contentFragment.findViewById(R.id.ticket_number);
        timeLeft = (TextView) contentFragment.findViewById(R.id.time_left);
        peopleInQueue = (TextView) contentFragment.findViewById(R.id.people_in_queue);
        leaveQueue = (Button) contentFragment.findViewById(R.id.button_leave_queue);
        leaveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem != null) {
                    app.requestQueueCancel(currentItem.getId(), null);
                    currentItem = null;
                    app.setQueueItemId(null);
                }
                app.cancelNotification();
                ((MainActivity) app.getContextActivity()).showCompanies();
            }
        });

        refreshHandler = new Handler();
        refreshRunable = new Runnable() {
            public void run() {
                try {
                    if (shouldRefresh) {
                        app.requestQueuedPeople(currentCompany.getId(), FragmentQueue.this);
                    }
                    refreshHandler.postDelayed(this, refreshDelay);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private void updateUi() {
        currentCompany = null;
        for (Company company : app.getCompanies()) {
            if (company.getId().equals(companyId)) {
                currentCompany = company;
            }
        }

        if (currentCompany == null) {
            ((MainActivity) app.getContextActivity()).showCompanies();
            return;
        }

        for (QueueItem queueItem : currentCompany.getQueueItems()) {
            if (queueItem.getId().equals(app.getQueueItemId())) {
                currentItem = queueItem;
            }
        }

        if (currentItem == null) {
            return;
        }

        getActivity().setTitle(currentCompany.getName() + " " + getString(R.string.queue));

        int numberQuedItemsBefore = currentCompany.getQueuedItemsBeforeCount(currentItem);

        ticketNumber.setText(String.valueOf(currentItem.getTicketNumber()));
        timeLeft.setText(String.valueOf(currentCompany.getWaitingTime() * numberQuedItemsBefore) + " min");
        peopleInQueue.setText(String.valueOf(numberQuedItemsBefore));

        app.showNotification(numberQuedItemsBefore);
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
