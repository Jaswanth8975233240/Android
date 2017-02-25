package com.intelliq.appengine.api.endpoint.queueitem;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.PermissionHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;


public class AddQueueItemEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEM_ADD;
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
        parameters.add("name");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long queueKeyId = request.getParameterAsLong("queueKeyId", -1);

        // create queue item
        QueueItemEntry queueItemEntry = new QueueItemEntry(queueKeyId);
        queueItemEntry.parseFromRequest(request);

        // indicates that the item was created by the queue management
        boolean addedByManagement = false;

        // assign the queue item to the current user
        UserEntry user = request.getUser();
        if (user != null) {
            PermissionEntry editQueuePermission = new PermissionEntry();
            editQueuePermission.setPermission(PermissionEntry.PERMISSION_EDIT);
            editQueuePermission.setSubjectKeyId(queueKeyId);
            addedByManagement = user.hasPermission(editQueuePermission);

            queueItemEntry.setUserKeyId(user.getKey().getId());
        }

        // make sure that user is not already in this queue
        if (queueItemEntry.getUserKeyId() > -1 && !addedByManagement) {
            QueueItemEntry existingQueueItemEntry = QueueItemHelper.getQueueItemByUserKeyId(queueItemEntry.getUserKeyId(), queueItemEntry.getQueueKeyId());
            if (existingQueueItemEntry != null) {
                // return the existing queue item
                response.setContent(existingQueueItemEntry);
                return response;
            }
        }

        // get next available ticket number
        int lastTicketNumber = QueueHelper.getLastTicketNumberInQueue(queueKeyId);
        queueItemEntry.setTicketNumber(lastTicketNumber + 1);

        Key entryKey = QueueItemHelper.saveEntry(queueItemEntry);
        queueItemEntry.setKey(entryKey);

        if (user != null) {
            // update user stats
            if (!addedByManagement) {
                user.getStats().setQueuesJoined(user.getStats().getQueuesJoined() + 1);
                UserHelper.saveEntry(user);
            }

            // add permission for queue item
            PermissionHelper.grantPermission(user, queueItemEntry, PermissionEntry.PERMISSION_OWN);
        }

        response.setContent(queueItemEntry);
        return response;
    }

}
