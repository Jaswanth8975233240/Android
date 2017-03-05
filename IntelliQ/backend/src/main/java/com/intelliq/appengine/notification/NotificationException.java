package com.intelliq.appengine.notification;

/**
 * Created by Steppschuh on 05/03/2017.
 */

public class NotificationException extends Exception {

    public NotificationException() {
        super();
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }

}