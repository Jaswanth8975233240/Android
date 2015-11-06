package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.ui.BlurTransformation;
import com.steppschuh.intelliq.ui.ImageHelper;

public class BusinessItemQueueView extends RelativeLayout {

    RelativeLayout itemContainer;
    TextView itemCoverHeading;
    ImageView itemCoverImage;
    View itemCoverOverlay;

    public BusinessItemQueueView(Context context) {
        super(context);
        init();
    }

    public BusinessItemQueueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.business_item_queue, this);
        itemContainer = (RelativeLayout) findViewById(R.id.itemContainer);
        itemCoverHeading = (TextView) findViewById(R.id.itemCoverHeading);
        itemCoverImage = (ImageView) findViewById(R.id.itemCoverImage);
        itemCoverOverlay = findViewById(R.id.itemCoverOverlay);

    }

    public void createFromQueueEntry(QueueEntry queueEntry) {
        itemCoverHeading.setText(queueEntry.getName());
        ImageEntry photo = new ImageEntry(queueEntry.getPhotoImageKeyId(), ImageEntry.TYPE_PHOTO);
        //photo.loadIntoImageView(itemCoverImage, getContext());
        photo.loadIntoImageView(itemCoverImage, new BlurTransformation(getContext(), 4), getContext(), new Callback() {
            @Override
            public void onSuccess() {
                updateColors();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void updateColors() {
        Log.v(IntelliQ.TAG, "Updating color palette");
        Palette.from(ImageHelper.drawableToBitmap(itemCoverImage.getDrawable())).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                int vibrantColor = palette.getVibrantColor(ContextCompat.getColor(getContext(), R.color.primary));
                int titleTextColor = swatch.getTitleTextColor();
                int bodyTextColor = swatch.getBodyTextColor();

                itemCoverOverlay.setBackgroundColor(vibrantColor);
                Log.v(IntelliQ.TAG, "Palette updated");
            }
        });
    }

    public RelativeLayout getItemContainer() {
        return itemContainer;
    }

    public void setItemContainer(RelativeLayout itemContainer) {
        this.itemContainer = itemContainer;
    }

    public TextView getItemCoverHeading() {
        return itemCoverHeading;
    }

    public void setItemCoverHeading(TextView itemCoverHeading) {
        this.itemCoverHeading = itemCoverHeading;
    }

    public ImageView getItemCoverImage() {
        return itemCoverImage;
    }

    public void setItemCoverImage(ImageView itemCoverImage) {
        this.itemCoverImage = itemCoverImage;
    }

    public View getItemCoverOverlay() {
        return itemCoverOverlay;
    }

    public void setItemCoverOverlay(View itemCoverOverlay) {
        this.itemCoverOverlay = itemCoverOverlay;
    }
}
