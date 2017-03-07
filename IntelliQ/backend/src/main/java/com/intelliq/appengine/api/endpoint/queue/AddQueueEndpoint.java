package com.intelliq.appengine.api.endpoint.queue;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.PermissionHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;
import com.intelliq.appengine.logging.QueueLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;


public class AddQueueEndpoint extends Endpoint {

    public static final long MAXIMUM_QUEUE_CREATIONS = 25;
    private static final Logger log = Logger.getLogger(AddQueueEndpoint.class.getName());

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ADD;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        return true;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();

        // only business owners can add queues
        PermissionEntry permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_OWN);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("businessKeyId", -1));
        permissionSet.getPermissions().add(permissionEntry);

        return permissionSet;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("businessKeyId");
        parameters.add("latitude");
        parameters.add("longitude");
        parameters.add("name");
        parameters.add("averageWaitingTime");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        // prevent spam
        UserEntry user = request.getUser();
        long queuesCreated = user.getStats().getQueuesCreated();
        if (queuesCreated >= MAXIMUM_QUEUE_CREATIONS) {
            log.warning("Prevented queue creation from user: " + user.getKey().getId());
            response.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
            response.setException(new Exception("Spam prevention: You may not create any more queues. Please contact us if you want to increase your quota."));
            return response;
        }

        // save queue
        long businessKeyId = request.getParameterAsLong("businessKeyId", -1);
        QueueEntry queueEntry = new QueueEntry(businessKeyId);
        queueEntry.parseFromRequest(request);
        Key entryKey = QueueHelper.saveEntry(queueEntry);
        queueEntry.setKey(entryKey);

        // update user stats
        user.getStats().setQueuesCreated(queuesCreated + 1);
        UserHelper.saveEntry(user);

        // add permission for queue
        PermissionHelper.grantPermission(user, queueEntry, PermissionEntry.PERMISSION_OWN);

        response.setContent(queueEntry);
        QueueLogging.logCreation(queueEntry, user);
        return response;
    }

}
