package com.steppschuh.intelliq.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.data.Queue;

import java.util.ArrayList;

public class QueuesTabNearby extends Fragment {

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
}