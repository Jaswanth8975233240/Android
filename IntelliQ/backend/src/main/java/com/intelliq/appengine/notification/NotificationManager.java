package com.intelliq.appengine.notification;

import com.intelliq.appengine.notification.text.MessageBird;
import com.intelliq.appengine.notification.text.TextNotificationSender;
import com.intelliq.appengine.util.KeyStore;

import java.util.logging.Logger;

/**
 * Created by Steppschuh on 06/03/2017.
 */

public final class NotificationManager extends NotificationSender {

    private static final Logger log = Logger.getLogger(NotificationManager.class.getName());

    private static NotificationManager instance;

    private TextNotificationSender textNotificationSender;

    private NotificationManager() {
        textNotificationSender = new MessageBird(KeyStore.getKey(KeyStore.MESSAGE_BIRD_KEY_DEV));
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    @Override
    public boolean canSendNotifications() {
        return textNotificationSender.canSendNotifications();
    }

    @Override
    public boolean canSendNotification(Notification notification) {
        try {
            return getBestNotificationSender(notification).canSendNotification(notification);
        } catch (NotificationException e) {
            log.warning("Unable to get NotificationSender: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void sendNotification(Notification notification) throws NotificationException {
        getBestNotificationSender(notification).sendNotification(notification);
    }

    /**
     * Will return the NotificationSender which is best suited for delivering
     * the specified notification.
     *
     * @param notification
     */
    public NotificationSender getBestNotificationSender(Notification notification) throws NotificationException {
        return textNotificationSender;
    }

    public TextNotificationSender getTextNotificationSender() {
        return textNotificationSender;
    }

}
