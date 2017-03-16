package com.intelliq.appengine.api.endpoint.queueitem;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
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
import com.intelliq.appengine.notification.NotificationGenerator;
import com.intelliq.appengine.notification.NotificationException;
import com.intelliq.appengine.notification.text.TextNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class AddQueueItemEndpoint extends Endpoint {

    private static final Logger log = Logger.getLogger(AddQueueItemEndpoint.class.getName());

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

        // get queue
        long queueKeyId = request.getParameterAsLong("queueKeyId", -1);
        QueueEntry queueEntry = QueueHelper.getEntryByKeyId(queueKeyId);

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
                // check if the queue items has already been processed
                if (existingQueueItemEntry.getStatus() == QueueItemEntry.STATUS_DONE) {
                    // create a new queue item
                } else if (existingQueueItemEntry.getStatus() == QueueItemEntry.STATUS_CANCELED) {
                    // create a new queue item
                } else {
                    // return the existing queue item
                    response.setContent(existingQueueItemEntry);
                    return response;
                }
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

        // send notification
        try {
            sendQueueJoinedNotification(queueItemEntry, queueEntry);
        } catch (NotificationException e) {
            log.info("Unable to send notification: " + e.getMessage());
        }

        response.setContent(queueItemEntry);
        return response;
    }

    public static void sendQueueJoinedNotification(QueueItemEntry queueItemEntry, QueueEntry queueEntry) throws NotificationException {
        if (!queueEntry.isTextNotificationsEnabled()) {
            throw new NotificationException("Text notifications are not enabled for this queue");
        }
        TextNotification notification = new TextNotification();
        notification.setRecipient(queueItemEntry.asTextNotificationRecipient());
        notification.setBody(NotificationGenerator.generateQueueJoinedNotificationBody(queueItemEntry, queueEntry));
        notification.send();
    }

}
