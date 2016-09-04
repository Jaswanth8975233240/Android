package com.intelliq.appengine.api.endpoint.queue;

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
import com.intelliq.appengine.datastore.entries.PermissionEntry;


public class PopulateQueueEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_POPULATE;
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
        int count = request.getParameterAsInt("count", 25);

        try {
            QueueHelper.populateQueueWithSampleItems(queueKeyId, count);
            response.setContent(count);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue"));
        }
        return response;
    }


}
