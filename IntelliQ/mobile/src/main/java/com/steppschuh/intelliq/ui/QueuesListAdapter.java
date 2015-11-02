package com.steppschuh.intelliq.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

    Context context;
    ArrayList<Queue> queues;

    public QueuesListAdapter(ArrayList<Queue> queues) {
        this.queues = queues;
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.queue_item, parent, false);
        QueueViewHolder queueViewHolder = new QueueViewHolder(v);
        return queueViewHolder;
    }

    @Override
    public void onBindViewHolder(QueueViewHolder queueViewHolder, int position) {
        Queue queue = queues.get(position);

        queueViewHolder.tag1.setVisibility(View.GONE);
        queueViewHolder.tag2.setVisibility(View.GONE);

        if (queue != null) {
            queueViewHolder.contentHeading.setText(queue.getName());
            queueViewHolder.coverImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_photo));
            queueViewHolder.contentImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_logo));
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
        TextView tag1;
        TextView tag2;
        ImageView coverImage;

        TextView contentHeading;
        TextView contentSubHeading;
        TextView action1;
        TextView action2;
        ImageView contentImage;

        QueueViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            contentHeading = (TextView)itemView.findViewById(R.id.cardViewContentHeading);
            contentSubHeading = (TextView)itemView.findViewById(R.id.cardViewContentSubHeading);

            tag1 = (TextView)itemView.findViewById(R.id.cardViewTag1);
            tag2 = (TextView)itemView.findViewById(R.id.cardViewTag2);

            action1 = (TextView)itemView.findViewById(R.id.cardViewAction1);
            action2 = (TextView)itemView.findViewById(R.id.cardViewAction2);
            coverImage = (ImageView)itemView.findViewById(R.id.cardViewCoverImage);
            contentImage = (ImageView)itemView.findViewById(R.id.cardViewContentImage);
        }
    }

}
