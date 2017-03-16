package com.intelliq.appengine.notification;

import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;

/**
 * Created by Steppschuh on 16/03/2017.
 */

public final class NotificationGenerator {

    public static String generateQueueJoinedNotificationBody(QueueItemEntry queueItemEntry, QueueEntry queueEntry) {
        int waitingQueueItemEntries = QueueHelper.getNumberOfItemsInQueue(queueEntry.getKey().getId(), QueueItemEntry.STATUS_WAITING);
        waitingQueueItemEntries -= 1; // current item is already included
        long averageWaitingTime = queueEntry.getAverageWaitingTime();

        StringBuilder sb = new StringBuilder()
                .append("Your ticket number is ")
                .append(queueItemEntry.getTicketNumber())
                .append(", you will be called as ")
                .append(queueItemEntry.getName())
                .append(" in about ")
                .append(QueueHelper.getReadableWaitingTimeEstimation(waitingQueueItemEntries, averageWaitingTime))
                .append(".");

        // TODO: localize message
        // TODO: append link to ticket

        return sb.toString();
    }

    public static String generateCalledSoonNotificationBody(QueueItemEntry queueItemEntry, QueueEntry queueEntry) {
        StringBuilder sb = new StringBuilder()
                .append(queueEntry.getName())
                .append(" will call you soon! Please get ready and check you ticket with number ")
                .append(queueItemEntry.getTicketNumber())
                .append(".");

        // TODO: localize message
        // TODO: append link to ticket

        return sb.toString();
    }

    public static String generateStatusChangedNotificationBody(QueueItemEntry queueItemEntry, QueueEntry queueEntry) throws NotificationException {
        StringBuilder sb = new StringBuilder();

        switch (queueItemEntry.getStatus()) {
            case QueueItemEntry.STATUS_CALLED: {
                sb.append("You are called! Please get to ")
                        .append(queueEntry.getName())
                        .append(" now.");
                break;
            }
            case QueueItemEntry.STATUS_CANCELED: {
                sb.append(queueEntry.getName())
                        .append(" canceled your ticket.");
                // TODO: ask for feedback
                break;
            }
            case QueueItemEntry.STATUS_DONE: {
                sb.append(queueEntry.getName())
                        .append(" marked your ticket as done. We hope IntelliQ.me improved your waiting experience. Please let us know your feedback: https://intelliq.me");
                // TODO: adjust feedback url
                break;
            }
            default: {
                throw new NotificationException("Unable to generate notification body for status change to: " + queueItemEntry.getStatus());
            }
        }

        // TODO: localize message
        // TODO: append link to ticket

        return sb.toString();
    }

}
