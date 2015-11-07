package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
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
    TextView businessName;
    TextView businessShortDescription;
    ImageView businessImage;

    private BusinessItemQueueView.OnItemClickListener onItemClickListener;

    public BusinessItemView(Context context) {
        super(context);
        init();
    }

    public BusinessItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.view_business_item, this);
        contentCoverContainer = (LinearLayout) findViewById(R.id.cardViewCoverContainer);
        businessName = (TextView) findViewById(R.id.businessName);
        businessShortDescription = (TextView) findViewById(R.id.businessShortDescription);
        businessImage = (ImageView) findViewById(R.id.businessImage);
    }

    public void createFromBusinessEntry(BusinessEntry businessEntry) {
        businessName.setText(businessEntry.getName());
        businessShortDescription.setText(businessEntry.getName());

        ImageEntry logo = new ImageEntry(businessEntry.getLogoImageKeyId(), ImageEntry.TYPE_LOGO);
        logo.loadIntoImageView(businessImage, getContext());

        ArrayList<BusinessItemQueueView> businessItemQueueViews = new ArrayList<>();
        for (QueueEntry queueEntry : businessEntry.getQueues()) {
            BusinessItemQueueView queueView = new BusinessItemQueueView(getContext());
            queueView.createFromQueueEntry(queueEntry);
            queueView.setOnItemClickListener(onItemClickListener);
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

    public void setOnItemClickListener(BusinessItemQueueView.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public LinearLayout getContentCoverContainer() {
        return contentCoverContainer;
    }

    public void setContentCoverContainer(LinearLayout contentCoverContainer) {
        this.contentCoverContainer = contentCoverContainer;
    }

    public TextView getBusinessName() {
        return businessName;
    }

    public void setBusinessName(TextView businessName) {
        this.businessName = businessName;
    }

    public TextView getBusinessShortDescription() {
        return businessShortDescription;
    }

    public void setBusinessShortDescription(TextView businessShortDescription) {
        this.businessShortDescription = businessShortDescription;
    }

    public ImageView getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(ImageView businessImage) {
        this.businessImage = businessImage;
    }
}
