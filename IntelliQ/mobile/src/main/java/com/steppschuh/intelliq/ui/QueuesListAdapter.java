package com.steppschuh.intelliq.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.ui.widget.StatusView;

import java.util.ArrayList;

public class QueuesListAdapter extends RecyclerView.Adapter<QueuesListAdapter.QueueViewHolder> {

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_ERROR = 1;

    Context context;
    ArrayList<QueueEntry> queues;
    StatusView statusView;

    public QueuesListAdapter(ArrayList<QueueEntry> queues) {
        this.queues = queues;
    }

    public void showStatusView(StatusView statusView) {
        this.statusView = statusView;
        queues = null;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (queues != null && queues.size() > 0) {
            return TYPE_DEFAULT;
        } else {
            return TYPE_ERROR;
        }
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v;
        switch (viewType) {
            case TYPE_ERROR: {
                v = statusView;
                break;
            }
            default: {
                v = LayoutInflater.from(context).inflate(R.layout.queue_item, parent, false);
                break;
            }
        }

        QueueViewHolder queueViewHolder = new QueueViewHolder(v, viewType);
        return queueViewHolder;
    }

    @Override
    public void onBindViewHolder(final QueueViewHolder queueViewHolder, int position) {
        switch (queueViewHolder.getViewType()) {
            case TYPE_ERROR: {
                onBindErrorViewHolder(queueViewHolder, position);
                break;
            }
            default: {
                onBindDefaultViewHolder(queueViewHolder, position);
                break;
            }
        }
    }

    private void onBindDefaultViewHolder(final QueueViewHolder queueViewHolder, int position) {
        final QueueEntry queue = queues.get(position);

        queueViewHolder.tag1.setVisibility(View.GONE);
        queueViewHolder.tag2.setVisibility(View.GONE);

        if (queue != null) {
            queueViewHolder.contentHeading.setText(queue.getName());
            queueViewHolder.contentSubHeading.setText(queue.getReadableLocation());

            queueViewHolder.coverImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_photo));
            queueViewHolder.contentImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_logo));

            ImageEntry photo = new ImageEntry(queue.getPhotoImageKeyId(), ImageEntry.TYPE_PHOTO);
            photo.loadIntoImageView(queueViewHolder.coverImage, context);

            queueViewHolder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = String.format("http://maps.google.com/maps?&daddr=%f,%f", queue.getLatitude(), queue.getLongitude());
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception ex) {
                        Snackbar.make(queueViewHolder.cardView, R.string.error_directions, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onBindErrorViewHolder(final QueueViewHolder queueViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        if (queues != null && queues.size() > 0) {
            return queues.size();
        } else if (statusView != null) {
            // show the error view
            return 1;
        } else {
            return 0;
        }
    }

    public ArrayList<QueueEntry> getQueues() {
        return queues;
    }

    public void setQueues(ArrayList<QueueEntry> queues) {
        this.queues = queues;
    }

    public void setStatusView(StatusView statusView) {
        this.statusView = statusView;
    }

    public static class QueueViewHolder extends RecyclerView.ViewHolder {

        private int viewType;

        CardView cardView;
        TextView tag1;
        TextView tag2;
        ImageView coverImage;

        TextView contentHeading;
        TextView contentSubHeading;
        TextView action1;
        TextView action2;
        ImageView contentImage;

        QueueViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            switch (viewType) {
                case TYPE_ERROR: {
                    setupErrorItem(itemView);
                    break;
                }
                default: {
                    setupDefaultItem(itemView);
                    break;
                }
            }
        }

        private void setupDefaultItem(View itemView) {
            cardView = (CardView )itemView.findViewById(R.id.cardView);
            contentHeading = (TextView) itemView.findViewById(R.id.cardViewContentHeading);
            contentSubHeading = (TextView) itemView.findViewById(R.id.cardViewContentSubHeading);

            tag1 = (TextView) itemView.findViewById(R.id.cardViewTag1);
            tag2 = (TextView) itemView.findViewById(R.id.cardViewTag2);

            action1 = (TextView) itemView.findViewById(R.id.cardViewAction1);
            action2 = (TextView) itemView.findViewById(R.id.cardViewAction2);
            coverImage = (ImageView) itemView.findViewById(R.id.cardViewCoverImage);
            contentImage = (ImageView) itemView.findViewById(R.id.cardViewContentImage);
        }

        private void setupErrorItem(View itemView) {

        }

        public int getViewType() {
            return viewType;
        }
    }

}
