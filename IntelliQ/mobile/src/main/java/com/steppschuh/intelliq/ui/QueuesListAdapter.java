package com.steppschuh.intelliq.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.data.Queue;

import java.util.ArrayList;

public class QueuesListAdapter extends RecyclerView.Adapter<QueuesListAdapter.QueueViewHolder> {

    ArrayList<Queue> queues;

    public QueuesListAdapter(ArrayList<Queue> queues) {
        this.queues = queues;
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item, parent, false);
        QueueViewHolder queueViewHolder = new QueueViewHolder(v);
        return queueViewHolder;
    }

    @Override
    public void onBindViewHolder(QueueViewHolder queueViewHolder, int position) {
        Queue queue = queues.get(position);

        queueViewHolder.cardViewTag1.setVisibility(View.GONE);
        queueViewHolder.cardViewTag2.setVisibility(View.GONE);

        if (queue != null) {
            queueViewHolder.cardViewContentHeading.setText(queue.getName());
        }
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }

    public ArrayList<Queue> getQueues() {
        return queues;
    }

    public void setQueues(ArrayList<Queue> queues) {
        this.queues = queues;
    }

    public static class QueueViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardViewTag1;
        TextView cardViewTag2;
        ImageView cardViewCoverImage;

        TextView cardViewContentHeading;
        TextView cardViewContentSubHeading;
        TextView cardViewAction1;
        TextView cardViewAction2;
        ImageView cardViewContentImage;

        QueueViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            cardViewContentHeading = (TextView)itemView.findViewById(R.id.cardViewContentHeading);
            cardViewContentSubHeading = (TextView)itemView.findViewById(R.id.cardViewContentSubHeading);

            cardViewTag1 = (TextView)itemView.findViewById(R.id.cardViewTag1);
            cardViewTag2 = (TextView)itemView.findViewById(R.id.cardViewTag2);

            cardViewAction1 = (TextView)itemView.findViewById(R.id.cardViewAction1);
            cardViewAction2 = (TextView)itemView.findViewById(R.id.cardViewAction2);
            cardViewCoverImage = (ImageView)itemView.findViewById(R.id.cardViewCoverImage);
            cardViewContentImage = (ImageView)itemView.findViewById(R.id.cardViewContentImage);
        }
    }

}
