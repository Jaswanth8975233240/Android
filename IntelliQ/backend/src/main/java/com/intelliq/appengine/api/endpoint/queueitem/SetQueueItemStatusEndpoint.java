package com.intelliq.appengine.api.endpoint.queueitem;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;


public class SetQueueItemStatusEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEM_STATUS;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("queueKeyId");
        parameters.add("queueItemKeyId");
        parameters.add("status");
        return parameters;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        QueueEntry queueEntry = QueueHelper.getEntryByKeyId(request.getParameterAsLong("queueKeyId", -1));
        if (queueEntry != null && queueEntry.getRequiresSignIn()) {
            return true;
        }

        // only queue management can call customers
        byte status = (byte) request.getParameterAsInt("status", 0);
        if (status == QueueItemEntry.STATUS_CALLED) {
            return true;
        }

        return false;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();
        PermissionEntry permissionEntry;

        byte status = (byte) request.getParameterAsInt("status", 0);

        // a user may cancel his own ticket or mark it as done
        if (status == QueueItemEntry.STATUS_CANCELED || status == QueueItemEntry.STATUS_DONE) {
            permissionEntry = new PermissionEntry();
            permissionEntry.setPermission(PermissionEntry.PERMISSION_OWN);
            permissionEntry.setSubjectKeyId(request.getParameterAsLong("queueItemKeyId", -1));
            permissionSet.getPermissions().add(permissionEntry);
        }

        permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_EDIT);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("queueKeyId", -1));
        permissionSet.getPermissions().add(permissionEntry);

        return permissionSet;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long queueItemKeyId = request.getParameterAsLong("queueItemKeyId", -1);
        byte status = (byte) request.getParameterAsInt("status", 0);

        try {
            QueueItemEntry queueItemEntry = QueueItemHelper.getEntryByKeyId(queueItemKeyId);
            queueItemEntry.setStatus(status);
            queueItemEntry.setLastStatusChangeTimestamp((new Date()).getTime());
            QueueItemHelper.saveEntry(queueItemEntry);

            response.setContent(queueItemEntry);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue item"));
        }
        return response;
    }

    public static void notifyWaitingQueueItems(long queueKeyId) {

    }

}
