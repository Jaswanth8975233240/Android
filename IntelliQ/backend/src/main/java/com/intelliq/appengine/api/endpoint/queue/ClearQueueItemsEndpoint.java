package com.intelliq.appengine.api.endpoint.queue;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;


public class ClearQueueItemsEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_CLEAR;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        return true;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("queueKeyId");
        return parameters;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();
        long queueKeyId = request.getParameterAsLong("queueKeyId", -1);

        PermissionEntry permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_EDIT);
        permissionEntry.setSubjectKeyId(queueKeyId);

        permissionSet.getPermissions().add(permissionEntry);
        return permissionSet;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long queueKeyId = request.getParameterAsLong("queueKeyId", -1);
        byte status = (byte) request.getParameterAsInt("status", QueueItemEntry.STATUS_ALL);
        boolean clearWaiting = request.getParameterAsBoolean("clearWaiting", true);
        boolean clearCalled = request.getParameterAsBoolean("clearCalled", true);

        if (status == QueueItemEntry.STATUS_ALL) {
            if (clearWaiting && clearCalled) {
                QueueHelper.deleteItemsInQueue(queueKeyId, QueueItemEntry.STATUS_ALL);
            } else {
                if (clearWaiting) {
                    QueueHelper.deleteItemsInQueue(queueKeyId, QueueItemEntry.STATUS_WAITING);
                }
                if (clearCalled) {
                    QueueHelper.deleteItemsInQueue(queueKeyId, QueueItemEntry.STATUS_CALLED);
                }
                QueueHelper.deleteItemsInQueue(queueKeyId, QueueItemEntry.STATUS_CANCELED);
                QueueHelper.deleteItemsInQueue(queueKeyId, QueueItemEntry.STATUS_DONE);
            }
        } else {
            QueueHelper.deleteItemsInQueue(queueKeyId, status);
        }

        List<QueueItemEntry> entries = QueueHelper.getItemsInQueue(queueKeyId, 0, 100);
        response.setContent(entries);
        return response;
    }


}
