package com.steppschuh.intelliq.ui.widget;

import android.app.Activity;
import android.view.View;

import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.ui.BusinessListAdapter;

public class StatusHelper {

    public static void showStatus(StatusView status, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        status.getStatusActionButton().setOnClickListener(onClickListener);
        businessListAdapter.showStatusView(status);
    }

    public static void showLoadingQueuesStatus(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getLoadingQueuesStatus(context), businessListAdapter, onClickListener);
    }

    public static void showUnknownError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getUnknownError(context), businessListAdapter, onClickListener);
    }

    public static void showNetworkError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getNetworkError(context), businessListAdapter, onClickListener);
    }

    public static void showApiError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getApiError(context), businessListAdapter, onClickListener);
    }

    public static void showNoDataError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getNoDataError(context), businessListAdapter, onClickListener);
    }

    public static void showMissingLocationPermissionError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getMissingLocationPermissionError(context), businessListAdapter, onClickListener);
    }

    public static void showUnknownLocationError(Activity context, BusinessListAdapter businessListAdapter, View.OnClickListener onClickListener) {
        showStatus(getUnknownLocationError(context), businessListAdapter, onClickListener);
    }

    public static StatusView getLoadingQueuesStatus(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_loading_queues_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_loading_queues_message));
        status.getStatusActionButton().setText(context.getString(R.string.action_retry));
        return status;
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

    public static StatusView getApiError(Activity context) {
        StatusView status = new StatusView(context);
        status.getStatusHeading().setText(context.getString(R.string.status_error_api_title));
        status.getStatusSubHeading().setText(context.getString(R.string.status_error_api_message));
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
