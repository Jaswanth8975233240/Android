package com.intelliq.appengine.notification;

/**
 * Created by Steppschuh on 04/03/2017.
 */

public abstract class NotificationSender<T extends Notification> {

    /**
     * Checks if the notification sender is operational at all.
     */
    abstract boolean canSendNotifications();

    /**
     * Checks if the specified notification can (potentially) be delivered.
     * @param notification
     */
    abstract boolean canSendNotification(T notification);

    /**
     * Tries to deliver the specified notification
     * @param notification
     * @throws NotificationException if the notification could not be delivered
     */
    abstract void sendNotification(T notification) throws NotificationException;

}
