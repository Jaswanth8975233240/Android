package com.intelliq.appengine.notification.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intelliq.appengine.notification.NotificationException;
import com.intelliq.appengine.notification.NotificationManager;
import com.intelliq.appengine.util.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Steppschuh on 05/03/2017.
 */

public class MessageBird extends TextNotificationSender {

    private static final Logger log = Logger.getLogger(NotificationManager.class.getName());

    private static final String MESSAGE_API_ENDPOINT = "https://rest.messagebird.com/messages";

    private static final String PARAMETER_RECIPIENTS = "recipients";
    private static final String PARAMETER_ORIGINATOR = "originator";
    private static final String PARAMETER_BODY = "body";

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String KEY_ERRORS = "errors";
    private static final String KEY_DESCRIPTION = "description";

    private String apiKey;

    public MessageBird(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean canSendNotifications() {
        // TODO: check if service is enabled
        return false;
    }

    @Override
    public boolean canSendNotification(TextNotification textNotification) {
        return false;
    }

    @Override
    public void sendNotification(TextNotification textNotification) throws NotificationException {
        List<String> msisdnNumbers = getMsisdnNumbersFromRecipients(textNotification.getRecipients());

        try {
            String response = new Request()
                    .setUrl(MESSAGE_API_ENDPOINT)
                    .setHeader(HEADER_AUTHORIZATION, getAuthorizationHeader(apiKey))
                    .setHeader(HEADER_ACCEPT, "application/json")
                    .setHeader(HEADER_CONTENT_TYPE, "application/json")
                    .setJsonProperty(PARAMETER_RECIPIENTS, getAsCsvString(msisdnNumbers))
                    .setJsonProperty(PARAMETER_BODY, textNotification.getBody())
                    .setJsonProperty(PARAMETER_ORIGINATOR, textNotification.getOriginator())
                    .postJson();

            log.info("Response from MessageBird API:\n" + response);

            // check response for errors
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response).getAsJsonObject();
            if (jsonObject.has(KEY_ERRORS)) {
                JsonArray errors = jsonObject.get(KEY_ERRORS).getAsJsonArray();
                if (errors.size() > 0) {
                    JsonObject error = errors.get(0).getAsJsonObject();
                    if (error.has(KEY_DESCRIPTION)) {
                        throw new IOException(error.get(KEY_DESCRIPTION).getAsString());
                    }
                }
                throw new IOException("Unable to get error from response:\n" + response);
            }
        } catch (IOException e) {
            throw new NotificationException("Unable to send notification: " + e.getMessage(), e);
        }
    }

    public static List<String> getMsisdnNumbersFromRecipients(List<TextNotificationRecipient> recipients) {
        List<String> msisdnNumbers = new ArrayList<>();
        for (TextNotificationRecipient notificationRecipient : recipients) {
            msisdnNumbers.add(notificationRecipient.getMsisdn());
        }
        return msisdnNumbers;
    }

    public static String getAsCsvString(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String getAuthorizationHeader(String apiKey) {
        return "AccessKey " + apiKey;
    }

}
