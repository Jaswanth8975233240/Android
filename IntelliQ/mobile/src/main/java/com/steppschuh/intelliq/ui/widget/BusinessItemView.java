package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.entry.BusinessEntry;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;

import java.util.ArrayList;

public class BusinessItemView extends RelativeLayout {

    LinearLayout contentCoverContainer;
    TextView contentHeading;
    TextView contentSubHeading;
    ImageView contentImage;

    public BusinessItemView(Context context) {
        super(context);
        init();
    }

    public BusinessItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.business_item, this);
        contentCoverContainer = (LinearLayout) findViewById(R.id.cardViewCoverContainer);
        contentHeading = (TextView) findViewById(R.id.cardViewContentHeading);
        contentSubHeading = (TextView) findViewById(R.id.cardViewContentSubHeading);
        contentImage = (ImageView) findViewById(R.id.cardViewContentImage);
    }

    public void createFromBusinessEntry(BusinessEntry businessEntry) {
        contentHeading.setText(businessEntry.getName());
        contentSubHeading.setText(businessEntry.getName());

        ImageEntry logo = new ImageEntry(businessEntry.getLogoImageKeyId(), ImageEntry.TYPE_LOGO);
        logo.loadIntoImageView(contentImage, getContext());

        ArrayList<BusinessItemQueueView> businessItemQueueViews = new ArrayList<>();
        for (QueueEntry queueEntry : businessEntry.getQueues()) {
            BusinessItemQueueView queueView = new BusinessItemQueueView(getContext());
            queueView.createFromQueueEntry(queueEntry);
            businessItemQueueViews.add(queueView);
        }
        setQueueItems(businessItemQueueViews);
    }

    public void setQueueItems(ArrayList<BusinessItemQueueView> businessItemQueueViews) {
        contentCoverContainer.removeAllViews();
        for (BusinessItemQueueView businessItemQueueView : businessItemQueueViews) {
            contentCoverContainer.addView(businessItemQueueView);
        }
    }

    public LinearLayout getContentCoverContainer() {
        return contentCoverContainer;
    }

    public void setContentCoverContainer(LinearLayout contentCoverContainer) {
        this.contentCoverContainer = contentCoverContainer;
    }

    public TextView getContentHeading() {
        return contentHeading;
    }

    public void setContentHeading(TextView contentHeading) {
        this.contentHeading = contentHeading;
    }

    public TextView getContentSubHeading() {
        return contentSubHeading;
    }

    public void setContentSubHeading(TextView contentSubHeading) {
        this.contentSubHeading = contentSubHeading;
    }

    public ImageView getContentImage() {
        return contentImage;
    }

    public void setContentImage(ImageView contentImage) {
        this.contentImage = contentImage;
    }
}
