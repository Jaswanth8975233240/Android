package com.intelliq.appengine.notification;

import java.util.List;

/**
 * Created by Steppschuh on 04/03/2017.
 */

public abstract class Notification {

    protected String originator;
    protected String body;
    protected List<NotificationRecipient> recipients;

    public Notification() {
    }

    public String getOriginator() {
        return originator;
    }

    public Notification setOriginator(String originator) {
        this.originator = originator;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Notification setBody(String body) {
        this.body = body;
        return this;
    }

    public List<NotificationRecipient> getRecipients() {
        return recipients;
    }

    public Notification setRecipients(List<NotificationRecipient> recipients) {
        this.recipients = recipients;
        return this;
    }

}
