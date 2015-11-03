package com.steppschuh.intelliq.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.steppschuh.intelliq.api.request.NearbyQueuesRequest;
import com.steppschuh.intelliq.api.response.ApiResponse;
import com.steppschuh.intelliq.data.Queue;

import java.util.ArrayList;

public class QueuesTabNearby extends Fragment {

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private String lastRequestCacheKey;

    RecyclerView recyclerView;
    QueuesListAdapter queuesListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.queues_tab_nearby, container,false);

        ArrayList<Queue> queues = new ArrayList<>();

        Queue queue = new Queue(0,0);
        queue.setName("Test Queue 1");
        queues.add(queue);

        queue = new Queue(0,0);
        queue.setName("Test Queue 2");
        queues.add(queue);

        queue = new Queue(0,0);
        queue.setName("Test Queue 3");
        queues.add(queue);

        queuesListAdapter = new QueuesListAdapter(queues);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(queuesListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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


    private void performApiRequest() {
        NearbyQueuesRequest request = new NearbyQueuesRequest(52.393730f, 13.132675f, 5000);
        lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new ApiResponseListener());
        Log.d(IntelliQ.TAG, "performApiRequest()");
    }

    private class ApiResponseListener implements RequestListener<ApiResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            Log.e(IntelliQ.TAG, "onRequestFailure: " + e.getMessage());
            if (QueuesTabNearby.this.isAdded()) {
                //update your UI
            }
        }

        @Override
        public void onRequestSuccess(ApiResponse response) {
            Log.d(IntelliQ.TAG, "onRequestSuccess: " + response.getStatusMessage());
            if (QueuesTabNearby.this.isAdded()) {
                //update your UI
            }
        }
    }

}