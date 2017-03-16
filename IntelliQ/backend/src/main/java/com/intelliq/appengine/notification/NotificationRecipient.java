package com.intelliq.appengine.notification;

/**
 * Created by Steppschuh on 06/03/2017.
 */

public abstract class NotificationRecipient {

    private String name;

    public NotificationRecipient() {
    }

    public String getName() {
        return name;
    }

    public NotificationRecipient setName(String name) {
        this.name = name;
        return this;
    }

}
