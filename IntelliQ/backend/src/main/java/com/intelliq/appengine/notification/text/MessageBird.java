package com.intelliq.appengine.notification.text;

import com.intelliq.appengine.notification.NotificationException;

/**
 * Created by Steppschuh on 05/03/2017.
 */

public class MessageBird extends TextNotificationSender {

    private String apiKey;

    public MessageBird(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean canSendNotifications() {
        return false;
    }

    public boolean canSendNotification(TextNotification textNotification) {
        return false;
    }

    public void sendNotification(TextNotification textNotification) throws NotificationException {
        throw new NotificationException("Not implemented");
    }

}
