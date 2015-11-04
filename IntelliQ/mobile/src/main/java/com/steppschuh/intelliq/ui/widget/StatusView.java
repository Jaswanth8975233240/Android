package com.steppschuh.intelliq.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class StatusView extends RelativeLayout {

    private String heading;
    private String subheading;

    public StatusView(Context context) {
        super(context);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
    }



}
