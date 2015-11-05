package com.steppschuh.intelliq.ui.widget;

import android.app.Activity;
import android.view.View;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.ui.QueuesListAdapter;

public class StatusHelper {

    public static void showStatus(StatusView status, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        status.getStatusActionButton().setOnClickListener(onClickListener);
        queuesListAdapter.showStatusView(status);
    }

    public static void showUnknownError(Activity context, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        showStatus(getUnknownError(context), queuesListAdapter, onClickListener);
    }

    public static void showNetworkError(Activity context, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        showStatus(getNetworkError(context), queuesListAdapter, onClickListener);
    }

    public static void showNoDataError(Activity context, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        showStatus(getNoDataError(context), queuesListAdapter, onClickListener);
    }

    public static void showMissingLocationPermissionError(Activity context, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        showStatus(getMissingLocationPermissionError(context), queuesListAdapter, onClickListener);
    }

    public static void showUnknownLocationError(Activity context, QueuesListAdapter queuesListAdapter, View.OnClickListener onClickListener) {
        showStatus(getUnknownLocationError(context), queuesListAdapter, onClickListener);
    }

    public static StatusView getUnknownError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_unknown_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_unknown_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_retry));
        return status;
    }

    public static StatusView getNetworkError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_network_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_network_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_retry));
        return status;
    }

    public static StatusView getNoDataError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_no_data_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_no_data_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_retry));
        return status;
    }

    public static StatusView getMissingLocationPermissionError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_missing_location_permission_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_missing_location_permission_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_settings));
        return status;
    }

    public static StatusView getUnknownLocationError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_unknown_location_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_unknown_location_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_retry));
        return status;
    }

}
