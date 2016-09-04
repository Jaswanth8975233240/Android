package com.intelliq.appengine.api.endpoint.queueitem;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;


public class DeleteQueueItemEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEM_DELETE;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        QueueEntry queueEntry = QueueHelper.getEntryByKeyId(request.getParameterAsLong("queueKeyId", -1));
        if (queueEntry != null && queueEntry.getRequiresSignIn()) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("queueKeyId");
        parameters.add("queueItemKeyId");
        return parameters;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();

        // user wants to delete his own ticket
        PermissionEntry permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_OWN);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("queueItemKeyId", -1));
        permissionSet.getPermissions().add(permissionEntry);

        // queue management wants to remove an item
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


        try {
            QueueItemHelper.deleteEntryByKeyId(queueItemKeyId);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue item"));
        }
        return response;
    }

}
