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
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.JsonSpiceService;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.api.request.NearbyQueuesRequest;
import com.steppschuh.intelliq.api.response.QueueListApiResponse;
import com.steppschuh.intelliq.ui.widget.StatusHelper;
import com.steppschuh.intelliq.ui.widget.StatusView;

import org.springframework.web.client.HttpClientErrorException;

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
        onRefresh();
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
        app.getUser().updateLocation(getActivity());
        performApiRequest();
    }


    private void performApiRequest() {
        swipeRefreshLayout.setRefreshing(true);

        NearbyQueuesRequest request = null;

        // check if some location info is available
        if (app.getUser().hasValidLocation()) {
            // latitude & longitude set, awesome
            request = new NearbyQueuesRequest(app.getUser().getLatitude(), app.getUser().getLongitude(), QueueEntry.DISTANCE_DEFAULT);
        } else if (app.getUser().hasValidPostalCode()) {
            // at least some postal code, let's hope we find something
            request = new NearbyQueuesRequest(app.getUser().getPostalCode());
        } else {
            // no location info at all, show error messages
            if (app.getUser().hasGrantedLocationPermission(getActivity())) {
                // we have the permission but no data
                StatusHelper.showUnknownLocationError(getActivity(), queuesListAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            } else {
                // permission denied
                StatusHelper.showMissingLocationPermissionError(getActivity(), queuesListAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.getUser().openPermissionSettings(getActivity());
                    }
                });
            }
        }

        // actually request some data
        if (request != null) {
            lastRequestCacheKey = request.createCacheKey();
            spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new ApiResponseListener());
            Log.d(IntelliQ.TAG, "Requesting nearby queues with cache key: " + lastRequestCacheKey);
        }
    }

    private class ApiResponseListener implements RequestListener<QueueListApiResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            // something went wrong, try to find out what
            if (e.getCause() instanceof HttpClientErrorException) {
                // some network error
                HttpClientErrorException exception = (HttpClientErrorException) e.getCause();
                Log.e(IntelliQ.TAG, "HttpClientErrorException: " + exception.getMessage() + " (" + exception.getStatusCode() + ")");

                StatusView status = StatusHelper.getNetworkError(getActivity());
                status.getStatusSubHeading().setText(status.getStatusSubHeading().getText() + "\n\n" + exception.getMessage());
                StatusHelper.showStatus(status, queuesListAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            } else if (e instanceof RequestCancelledException) {
                // don't show an error message, just retry
                Log.e(IntelliQ.TAG, "RequestCancelledException: " + e.getMessage());
                onRefresh();
            } else {
                // damn, unknown error
                Log.e(IntelliQ.TAG, "Unknown onRequestFailure: " + e.getMessage());
                StatusHelper.showUnknownError(getActivity(), queuesListAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            }

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onRequestSuccess(QueueListApiResponse response) {
            if (response.getContent().size() > 0) {
                // we got some data
                queuesListAdapter.setQueues(response.getContent());
                queuesListAdapter.notifyDataSetChanged();
            } else {
                // request was alright, but no queues found
                StatusHelper.showNoDataError(getActivity(), queuesListAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            }

            swipeRefreshLayout.setRefreshing(false);

            if (QueuesTabNearby.this.isAdded()) {
                //update your UI
            }
        }
    }

}