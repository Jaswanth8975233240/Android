package com.intelliq.appengine.notification;

/**
 * Created by Steppschuh on 04/03/2017.
 */

public abstract class NotificationSender<T extends Notification> {

    protected boolean serviceEnabled = true;

    public NotificationSender() {
    }

    /**
     * Checks if the notification sender is operational at all.
     */
    public boolean canSendNotifications() {
        return serviceEnabled;
    }

    /**
     * Checks if the specified notification can (potentially) be delivered.
     *
     * @param notification
     */
    public boolean canSendNotification(T notification) {
        if (!canSendNotifications()) {
            return false;
        }

        // check recipients
        if (notification.getRecipients().isEmpty()) {
            return false;
        }

        // check body
        if (notification.getBody() == null || notification.getBody().length() < 1) {
            return false;
        }

        // check originator
        if (notification.getOriginator() == null || notification.getOriginator().length() < 1) {
            return false;
        }

        return true;
    }

    /**
     * Tries to deliver the specified notification
     *
     * @param notification
     * @throws NotificationException if the notification could not be delivered
     */
    public abstract void sendNotification(T notification) throws NotificationException;

}
