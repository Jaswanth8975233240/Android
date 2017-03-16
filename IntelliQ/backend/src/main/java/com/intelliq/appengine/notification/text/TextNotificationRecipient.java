package com.intelliq.appengine.notification.text;

import com.intelliq.appengine.notification.NotificationRecipient;

/**
 * Created by Steppschuh on 06/03/2017.
 */

public class TextNotificationRecipient extends NotificationRecipient {

    private String msisdn;

    public TextNotificationRecipient() {
    }

    public TextNotificationRecipient(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public TextNotificationRecipient setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

}
