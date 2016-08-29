package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.ui.AnimationHelper;
import com.steppschuh.intelliq.ui.image.BlurTransformation;
import com.steppschuh.intelliq.ui.image.ImageHelper;

public class BusinessItemQueueView extends RelativeLayout {

    public static final int COVER_BLUR_RADIUS = 5;
    public static final int COVER_SAMPLING_FACTOR = 2;

    RelativeLayout queueContainer;
    TextView queueName;
    ImageView queueImage;
    View queueImageOverlay;

    QueueEntry queueEntry;

    private BusinessItemQueueView.OnItemClickListener onItemClickListener;

    public BusinessItemQueueView(Context context) {
        super(context);
        init();
    }

    public BusinessItemQueueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.view_business_item_queue, this);
        queueContainer = (RelativeLayout) findViewById(R.id.queueContainer);
        queueName = (TextView) findViewById(R.id.queueName);
        queueImage = (ImageView) findViewById(R.id.queueImage);
        queueImageOverlay = findViewById(R.id.queueImageOverlay);
    }

    public void createFromQueueEntry(QueueEntry queueEntry) {
        this.queueEntry = queueEntry;

        queueName.setText(queueEntry.getName());

        // prepare image view, fade in when colors are ready
        queueImage.setAlpha(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queueImage.setTransitionName("queueImage." + queueEntry.getKey().getId());
        }
        ImageEntry photo = new ImageEntry(queueEntry.getPhotoImageKeyId(), ImageEntry.TYPE_PHOTO);
        photo.loadIntoImageView(queueImage, new BlurTransformation(getContext(), COVER_BLUR_RADIUS, COVER_SAMPLING_FACTOR), getContext(), new Callback() {
            @Override
            public void onSuccess() {
                updateColors();
            }

            @Override
            public void onError() {
                // fallback drawable will be visible
                AnimationHelper.fadeToOpacity(queueImage, 1f, AnimationHelper.DURATION_SLOW);
            }
        });
    }

    private void updateColors() {
        Palette.from(ImageHelper.drawableToBitmap(queueImage.getDrawable())).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int primaryColor = ContextCompat.getColor(getContext(), R.color.primary);
                int vibrantColor = palette.getVibrantColor(primaryColor);
                int darkMutedColor = palette.getDarkMutedColor(primaryColor);

                AnimationHelper.fadeToBackgroundColor(queueImageOverlay, primaryColor, darkMutedColor, AnimationHelper.DURATION_SLOW);
                AnimationHelper.fadeToOpacity(queueImage, 1f, AnimationHelper.DURATION_SLOW);
            }
        });
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        queueContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(queueEntry, BusinessItemQueueView.this);
                }
            }
        });
    }

    public interface OnItemClickListener {

        void onItemClick(QueueEntry queueEntry, BusinessItemQueueView view);

    }

    public RelativeLayout getQueueContainer() {
        return queueContainer;
    }

    public void setQueueContainer(RelativeLayout queueContainer) {
        this.queueContainer = queueContainer;
    }

    public TextView getQueueName() {
        return queueName;
    }

    public void setQueueName(TextView queueName) {
        this.queueName = queueName;
    }

    public ImageView getQueueImage() {
        return queueImage;
    }

    public void setQueueImage(ImageView queueImage) {
        this.queueImage = queueImage;
    }

    public View getQueueImageOverlay() {
        return queueImageOverlay;
    }

    public void setQueueImageOverlay(View queueImageOverlay) {
        this.queueImageOverlay = queueImageOverlay;
    }

}
