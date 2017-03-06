package com.intelliq.appengine.notification;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Steppschuh on 04/03/2017.
 */

public abstract class Notification<T extends NotificationRecipient> {

    public static final String ORIGINATOR_INTELLIQ = "IntelliQ.me";

    protected String originator = ORIGINATOR_INTELLIQ;
    protected String body;
    protected List<T> recipients;

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

    public List<T> getRecipients() {
        return recipients;
    }

    public Notification setRecipients(List<T> recipients) {
        this.recipients = recipients;
        return this;
    }

    public Notification setRecipient(T recipient) {
        this.recipients = Arrays.asList(recipient);
        return this;
    }

}
