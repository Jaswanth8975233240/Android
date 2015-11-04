package com.steppschuh.intelliq.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.JsonSpiceService;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.api.request.NearbyQueuesRequest;
import com.steppschuh.intelliq.api.response.QueueListApiResponse;

import java.util.ArrayList;

public class QueuesTabNearby extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    IntelliQ app;

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private String lastRequestCacheKey;

    RecyclerView recyclerView;
    QueuesListAdapter queuesListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.queues_tab_nearby, container,false);

        app = (IntelliQ) getActivity().getApplication();

        // start tracking the users location
        app.getUser().updateLocation(getActivity());

        return setupUi(v);
    }

    private View setupUi(View v) {
        ArrayList<QueueEntry> queues = new ArrayList<>();
        queuesListAdapter = new QueuesListAdapter(queues);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(queuesListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
        performApiRequest();
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onRefresh() {
        performApiRequest();
    }

    private void showError() {

    }

    private void performApiRequest() {
        swipeRefreshLayout.setRefreshing(true);
        NearbyQueuesRequest request = new NearbyQueuesRequest(app.getUser().getLatitude(), app.getUser().getLongitude(), QueueEntry.DISTANCE_DEFAULT);
        lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new ApiResponseListener());
        Log.d(IntelliQ.TAG, "performApiRequest()");
    }

    private class ApiResponseListener implements RequestListener<QueueListApiResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            Log.e(IntelliQ.TAG, "onRequestFailure: " + e.getMessage());
            if (QueuesTabNearby.this.isAdded()) {
                //update your UI
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onRequestSuccess(QueueListApiResponse response) {
            Log.d(IntelliQ.TAG, "onRequestSuccess: " + response.getStatusMessage());

            for (QueueEntry queueEntry : response.getContent()) {
                Log.d(IntelliQ.TAG, "Queue entry: " + queueEntry.getName());
            }

            queuesListAdapter.setQueues(response.getContent());
            queuesListAdapter.notifyDataSetChanged();

            if (QueuesTabNearby.this.isAdded()) {
                //update your UI
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }

}