package com.intelliq.appengine.notification.text;

import com.intelliq.appengine.notification.NotificationException;
import com.intelliq.appengine.util.KeyStore;

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created by Steppschuh on 06/03/2017.
 */
public class MessageBirdTest {

    @Test
    public void canSendNotification_validNotification_returnsTrue() throws Exception {
        // TODO: implement
    }

    @Test
    public void canSendNotification_invalidNotification_returnsFalse() throws Exception {
        // TODO: implement
    }

    @Test
    public void sendNotification_validNotification_getsDelivered() throws Exception {
        MessageBird messageBird = new MessageBird(KeyStore.getKey(KeyStore.MESSAGE_BIRD_KEY_DEV));
        TextNotification textNotification = new TextNotification();
        textNotification.setRecipient(new TextNotificationRecipient("4915154854847"));
        textNotification.setBody("This is a test notification");
        messageBird.sendNotification(textNotification);
    }

    @Test
    public void sendNotification_invalidRecipient_throwsException() throws Exception {
        try {
            MessageBird messageBird = new MessageBird(KeyStore.getKey(KeyStore.MESSAGE_BIRD_KEY_DEV));
            TextNotification textNotification = new TextNotification();
            textNotification.setRecipient(new TextNotificationRecipient("1234"));
            textNotification.setBody("This is a test notification");
            messageBird.sendNotification(textNotification);
            fail("A NotificationException should have been thrown");
        } catch (NotificationException e) {
            e.printStackTrace();
            // expected
        }
    }

}