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
import com.intelliq.appengine.notification.NotificationException;
import com.intelliq.appengine.notification.NotificationGenerator;
import com.intelliq.appengine.notification.text.TextNotification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;


public class SetQueueItemStatusEndpoint extends Endpoint {

    private static final Logger log = Logger.getLogger(SetQueueItemStatusEndpoint.class.getName());

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

            QueueEntry queueEntry = QueueHelper.getEntryByKeyId(queueItemEntry.getQueueKeyId());

            // send notifications
            try {
                notifyWaitingQueueItems(queueEntry);
            } catch (NotificationException e) {
                log.info("Unable to send called soon notification: " + e.getMessage());
            }
            try {
                sendStatusChangeNotification(queueItemEntry, queueEntry);
            } catch (NotificationException e) {
                log.info("Unable to send status change notification: " + e.getMessage());
            }
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue item"));
        }

        return response;
    }

    public static void notifyWaitingQueueItems(QueueEntry queueEntry) throws NotificationException {
        if (!queueEntry.isTextNotificationsEnabled()) {
            throw new NotificationException("Text notifications are not enabled for this queue");
        }
        int indexOfQueueItemToNotify = 3; // TODO: make adjustable, depend on average waiting time
        List<QueueItemEntry> waitingQueueItems = new ArrayList<>();
        try {
            waitingQueueItems = QueueHelper.getItemsInQueue(queueEntry.getKey().getId(), QueueItemEntry.STATUS_WAITING, indexOfQueueItemToNotify, indexOfQueueItemToNotify + 1);
            if (waitingQueueItems.isEmpty()) {
                log.fine("No waiting queue item entry at index " + indexOfQueueItemToNotify);
                return;
            }

            QueueItemEntry queueItemEntry = waitingQueueItems.get(0);
            sendCalledSoonNotification(queueItemEntry, queueEntry);
        } catch (JDOObjectNotFoundException e) {
            throw new NotificationException("Unable to get waiting queue items: " + e.getMessage(), e);
        }
    }

    public static void sendCalledSoonNotification(QueueItemEntry queueItemEntry, QueueEntry queueEntry) throws NotificationException {
        if (!queueEntry.isTextNotificationsEnabled()) {
            throw new NotificationException("Text notifications are not enabled for this queue");
        }
        TextNotification notification = new TextNotification();
        notification.setRecipient(queueItemEntry.asTextNotificationRecipient());
        notification.setBody(NotificationGenerator.generateCalledSoonNotificationBody(queueItemEntry, queueEntry));
        notification.send();
    }

    public static void sendStatusChangeNotification(QueueItemEntry queueItemEntry, QueueEntry queueEntry) throws NotificationException {
        if (!queueEntry.isTextNotificationsEnabled()) {
            throw new NotificationException("Text notifications are not enabled for this queue");
        }
        TextNotification notification = new TextNotification();
        notification.setRecipient(queueItemEntry.asTextNotificationRecipient());
        notification.setBody(NotificationGenerator.generateStatusChangedNotificationBody(queueItemEntry, queueEntry));
        notification.send();
    }

}
