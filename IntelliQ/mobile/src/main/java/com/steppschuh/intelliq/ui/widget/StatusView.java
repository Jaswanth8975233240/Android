package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.steppschuh.intelliq.R;

public class StatusView extends RelativeLayout {

    public static final int ERROR_UNKNOWN = 0;
    public static final int ERROR_NO_DATA = 1;
    public static final int ERROR_NO_LOCATION = 2;
    public static final int ERROR_NO_CONNECTION = 3;

    private TextView statusHeading;
    private TextView statusSubHeading;
    private ImageView statusImage;
    private RelativeLayout statusImageContainer;
    private Button statusActionButton;

    public StatusView(Context context) {
        super(context);
        init();
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.status, this);
        statusHeading = (TextView) findViewById(R.id.statusHeading);
        statusSubHeading = (TextView) findViewById(R.id.statusSubHeading);
        statusImageContainer = (RelativeLayout) findViewById(R.id.statusImageContainer);
        statusImage = (ImageView) findViewById(R.id.statusImage);
        statusActionButton = (Button) findViewById(R.id.statusAction);
    }

    public TextView getStatusHeading() {
        return statusHeading;
    }

    public void setStatusHeading(TextView statusHeading) {
        this.statusHeading = statusHeading;
    }

    public TextView getStatusSubHeading() {
        return statusSubHeading;
    }

    public void setStatusSubHeading(TextView statusSubHeading) {
        this.statusSubHeading = statusSubHeading;
    }

    public ImageView getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(ImageView statusImage) {
        this.statusImage = statusImage;
    }

    public RelativeLayout getStatusImageContainer() {
        return statusImageContainer;
    }

    public void setStatusImageContainer(RelativeLayout statusImageContainer) {
        this.statusImageContainer = statusImageContainer;
    }

    public Button getStatusActionButton() {
        return statusActionButton;
    }

    public void setStatusActionButton(Button statusActionButton) {
        this.statusActionButton = statusActionButton;
    }
}
