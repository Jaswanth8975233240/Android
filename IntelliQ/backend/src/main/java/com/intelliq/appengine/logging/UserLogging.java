package com.intelliq.appengine.logging;

import com.intelliq.appengine.api.endpoint.user.UserEndpoint;
import com.intelliq.appengine.datastore.entries.UserEntry;

import net.steppschuh.slackmessagebuilder.message.attachment.Attachment;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentField;

import java.util.ArrayList;
import java.util.List;

public abstract class UserLogging {

    public static void logSignIn(UserEntry user) {
        Attachment attachment = generateUserAttachment(user);
        attachment.setTitle("Sign In");
        attachment.setText("A user signed in");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static void logSignUp(UserEntry user) {
        Attachment attachment = generateUserAttachment(user);
        attachment.setTitle("Sign Up");
        attachment.setText("A user signed in for the first time");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static Attachment generateUserAttachment(UserEntry user) {
        List<AttachmentField> fields = new ArrayList<>();
        fields.add(new AttachmentField("Name", user.getName()));
        fields.add(new AttachmentField("Mail", user.getMail()));
        if (user.getKey() != null) {
            fields.add(new AttachmentField("ID", String.valueOf(user.getKey().getId())));
        }

        Attachment attachment = SlackLog.generateAttachment(SlackLog.INFO, UserEndpoint.class.getSimpleName(), "");
        attachment.setFields(fields);
        return attachment;
    }

}
