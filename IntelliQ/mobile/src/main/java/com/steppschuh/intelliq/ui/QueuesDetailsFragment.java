package com.steppschuh.intelliq.ui;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Callback;
import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.JsonSpiceService;
import com.steppschuh.intelliq.api.entry.BusinessEntry;
import com.steppschuh.intelliq.api.entry.ImageEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;
import com.steppschuh.intelliq.api.request.QueueDetailsRequest;
import com.steppschuh.intelliq.api.response.BusinessApiResponse;
import com.steppschuh.intelliq.ui.image.ImageHelper;
import com.steppschuh.intelliq.ui.widget.StatusHelper;
import com.steppschuh.intelliq.ui.widget.StatusView;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class QueuesDetailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
    private static final String EXTRA_TITLE = "com.antonioleiva.materializeyourapp.extraTitle";

    IntelliQ app;

    private Bundle savedInstanceState;

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
        this.savedInstanceState = savedInstanceState;
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
        // update the view for the passed queue
        updateQueueDetails();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // add the queueKeyId to the data bundle, just in case the fragment
        // will be recreated from a savedInstanceState later
        if (queueEntry != null) {
            outState.putLong("queueKeyId", queueEntry.getKey().getId());
        }
    }

    @Override
    public void onRefresh() {
        Log.v(IntelliQ.TAG, "onRefresh queue details");

        if (businessEntry == null || queueEntry == null) {
            // fragment probably got recreated from a savedInstanceState
            if (savedInstanceState != null) {
                long queueKeyId = savedInstanceState.getLong("queueKeyId", -1);
                if (queueKeyId > -1) {
                    // request queue details for the passed key id
                    requestQueueDetails(queueKeyId);
                }
            }

            return;
        } else {
            // we already have a valid queue entry, update it
            requestQueueDetails(queueEntry.getKey().getId());
        }
    }

    /**
     * UI
     */
    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);

            getActivity().getWindow().setEnterTransition(transition);
            getActivity().getWindow().setReturnTransition(transition);
        }
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

    private void updateQueueDetails() {
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

    /**
     * Request handling
     */
    private void requestQueueDetails(long queueKeyId) {
        QueueDetailsRequest request = new QueueDetailsRequest(queueKeyId);
        lastRequestCacheKey = request.createCacheKey();
        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_SECOND, new ApiResponseListener());
        Log.d(IntelliQ.TAG, "Requesting queue details with cache key: " + lastRequestCacheKey);

    }

    private class ApiResponseListener implements RequestListener<BusinessApiResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            // something went wrong, try to find out what
            if (e.getCause() instanceof HttpClientErrorException) {
                // some network error
                HttpClientErrorException exception = (HttpClientErrorException) e.getCause();
                Log.e(IntelliQ.TAG, "HttpClientErrorException: " + exception.getMessage() + " (" + exception.getStatusCode() + ")");

                Toast.makeText(getActivity(), getString(R.string.status_error_api_title), Toast.LENGTH_SHORT).show();
            } else if (e instanceof RequestCancelledException) {
                // don't show an error message, just retry
                Log.e(IntelliQ.TAG, "RequestCancelledException: " + e.getMessage());
                onRefresh();
            } else if (e instanceof NetworkException || e instanceof NoNetworkException) {
                Log.e(IntelliQ.TAG, "NetworkException: " + e.getMessage());
                Toast.makeText(getActivity(), getString(R.string.status_error_network_title), Toast.LENGTH_SHORT).show();
            } else {
                // damn, unknown error
                Log.e(IntelliQ.TAG, "Unknown onRequestFailure: " + e.getMessage());
                Toast.makeText(getActivity(), getString(R.string.status_error_unknown_title), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRequestSuccess(BusinessApiResponse response) {
            if (response.getStatusCode() == HttpStatus.OK.value())  {
                // API call was successful
                if (response.getContent() != null && response.getContent().getQueues().size() > 0) {
                    // we got some data
                    businessEntry = response.getContent();
                    queueEntry = businessEntry.getQueues().get(0);
                    updateQueueDetails();
                } else {
                    // request was alright, but no queue found
                    StatusView status = StatusHelper.getNoDataError(getActivity());
                    StatusHelper.showStatus(status, getActivity(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRefresh();
                        }
                    });
                }
            } else {
                // API returned an error code
                StatusView status = StatusHelper.getApiError(getActivity());
                status.getStatusSubHeading().setText(status.getStatusSubHeading().getText() + "\n\nException: " + response.getStatusMessage());
                StatusHelper.showStatus(status, getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            }
        }
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