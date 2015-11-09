package com.steppschuh.intelliq.ui;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.squareup.picasso.Callback;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.JsonSpiceService;
import com.steppschuh.intelliq.api.entry.BusinessEntry;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;

public class QueuesDetailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
    private static final String EXTRA_TITLE = "com.antonioleiva.materializeyourapp.extraTitle";

    IntelliQ app;

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private String lastRequestCacheKey;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String imageTransitionName;

    BusinessEntry businessEntry;
    QueueEntry queueEntry;

    FloatingActionButton fab;

    ImageView queueImage;
    View queueImageOverlay;
    TextView businessName;
    TextView businessShortDescription;
    ImageView businessImage;

    TextView queueDescription;
    LinearLayout queueDetailBar;
    RelativeLayout queueDetailBarLeft;
    RelativeLayout queueDetailBarRight;
    TextView queueDetailBarValueLeft;
    TextView queueDetailBarValueRight;
    TextView queueDetailBarDescriptionLeft;
    TextView queueDetailBarDescriptionRight;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_queue_details, container,false);

        app = (IntelliQ) getActivity().getApplication();

        return setupUi(v);
    }

    private View setupUi(View v) {
        // setup animations
        initActivityTransitions();
        getActivity().supportPostponeEnterTransition();

        // setup toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        // setup views
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        businessName = (TextView) v.findViewById(R.id.businessName);
        businessShortDescription = (TextView) v.findViewById(R.id.businessShortDescription);
        businessImage = (ImageView) v.findViewById(R.id.businessImage);

        queueImage = (ImageView) v.findViewById(R.id.queueImage);
        queueImageOverlay = v.findViewById(R.id.queueImageOverlay);
        queueImageOverlay.setVisibility(View.GONE);
        queueImage = (ImageView) v.findViewById(R.id.queueImage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queueImage.setTransitionName(imageTransitionName);
        }
        queueDescription = (TextView) v.findViewById(R.id.queueDescription);

        queueDetailBar = (LinearLayout) v.findViewById(R.id.queueDetailBar);
        queueDetailBarLeft = (RelativeLayout) v.findViewById(R.id.queueDetailBarLeft);
        queueDetailBarRight = (RelativeLayout) v.findViewById(R.id.queueDetailBarRight);
        queueDetailBarValueLeft = (TextView) v.findViewById(R.id.queueDetailBarValueLeft);
        queueDetailBarValueRight = (TextView) v.findViewById(R.id.queueDetailBarValueRight);
        queueDetailBarDescriptionLeft = (TextView) v.findViewById(R.id.queueDetailBarDescriptionLeft);
        queueDetailBarDescriptionRight = (TextView) v.findViewById(R.id.queueDetailBarDescriptionRight);

        // update view for passed business & queue entry
        createFrom(businessEntry, queueEntry);
        return v;
    }

    private void createFrom(BusinessEntry businessEntry, QueueEntry queueEntry) {
        if (businessEntry == null || queueEntry == null) {
            return;
        }

        // business description
        collapsingToolbarLayout.setTitle(queueEntry.getName());
        businessName.setText(businessEntry.getName());
        businessShortDescription.setText(businessEntry.getReadableDescription(app));

        ImageEntry logo = new ImageEntry(businessEntry.getLogoImageKeyId(), ImageEntry.TYPE_LOGO);
        logo.loadIntoImageView(businessImage, getContext());

        // prepare image view, fade in when colors are ready
        queueImage.setAlpha(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            queueImage.setTransitionName("queueImage." + queueEntry.getKey().getId());
        }
        ImageEntry photo = new ImageEntry(queueEntry.getPhotoImageKeyId(), ImageEntry.TYPE_PHOTO);
        photo.loadIntoImageView(queueImage, null, getContext(), new Callback() {
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

        // queue description
        queueDetailBarValueLeft.setText(queueEntry.getReadableNumberOfWaitingPeople());
        queueDetailBarValueRight.setText(queueEntry.getReadableNumberOfRemainingMinutes());
        queueDescription.setText(queueEntry.getDescription());
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
        onRefresh();
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        AnimationHelper.fadeStatusBarToDefaultColor(getActivity());
        super.onDestroy();
    }

    @Override
    public void onRefresh() {

    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);

            getActivity().getWindow().setEnterTransition(transition);
            getActivity().getWindow().setReturnTransition(transition);
        }
    }

    private void updateToolbarColors(Palette palette) {
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primary));
        updateBackground((FloatingActionButton) getActivity().findViewById(R.id.fab), palette);
        getActivity().supportStartPostponedEnterTransition();
    }

    private void updateColors() {
        Palette.from(ImageHelper.drawableToBitmap(queueImage.getDrawable())).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int primaryColor = ContextCompat.getColor(getContext(), R.color.primary);
                int vibrantColor = palette.getVibrantColor(primaryColor);
                int vibrantDarkColor = palette.getDarkVibrantColor(vibrantColor);
                int mutedColor = palette.getMutedColor(primaryColor);
                int mutedDarkColor = palette.getDarkMutedColor(mutedColor);

                AnimationHelper.fadeToOpacity(queueImage, 1f, AnimationHelper.DURATION_SLOW);

                // vibrant views
                AnimationHelper.fadeFabToBackgroundColor(fab, vibrantColor, AnimationHelper.DURATION_SLOW);
                AnimationHelper.fadeToBackgroundColor(queueImageOverlay, primaryColor, vibrantColor, AnimationHelper.DURATION_SLOW);

                // muted views
                collapsingToolbarLayout.setContentScrimColor(mutedDarkColor);
                collapsingToolbarLayout.setStatusBarScrimColor(mutedDarkColor);
                AnimationHelper.fadeToBackgroundColor(queueDetailBar, primaryColor, mutedDarkColor, AnimationHelper.DURATION_SLOW);
                AnimationHelper.fadeStatusBarToColor(getActivity(), mutedDarkColor, AnimationHelper.DURATION_SLOW);

            }
        });
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    /**
     * Getter & Setter
     */
    public BusinessEntry getBusinessEntry() {
        return businessEntry;
    }

    public void setBusinessEntry(BusinessEntry businessEntry) {
        this.businessEntry = businessEntry;
    }

    public QueueEntry getQueueEntry() {
        return queueEntry;
    }

    public void setQueueEntry(QueueEntry queueEntry) {
        this.queueEntry = queueEntry;
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

    public void setImageTransitionName(String imageTransitionName) {
        this.imageTransitionName = imageTransitionName;
    }
}