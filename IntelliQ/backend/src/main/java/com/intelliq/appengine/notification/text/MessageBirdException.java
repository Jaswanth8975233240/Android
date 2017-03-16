package com.intelliq.appengine.notification.text;

/**
 * Created by Steppschuh on 07/03/2017.
 */

public class MessageBirdException extends Exception {

    public MessageBirdException() {
        super();
    }

    public MessageBirdException(String message) {
        super(message);
    }

    public MessageBirdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageBirdException(Throwable cause) {
        super(cause);
    }

}
