package com.intelliq.appengine.logging;

import com.intelliq.appengine.api.endpoint.business.BusinessEndpoint;
import com.intelliq.appengine.api.endpoint.queue.QueueEndpoint;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

import net.steppschuh.slackmessagebuilder.message.attachment.Attachment;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentField;

import java.util.ArrayList;
import java.util.List;

public abstract class BusinessLogging {

    public static void logCreation(BusinessEntry business, UserEntry user) {
        Attachment attachment = generateBusinessAttachment(business);
        attachment.setTitle("Business Created");
        attachment.setText(user.getName() + " created a new business");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static void logEdit(BusinessEntry business, UserEntry user) {
        Attachment attachment = generateBusinessAttachment(business);
        attachment.setTitle("Business Edited");
        attachment.setText(user.getName() + " edited an existing business");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static Attachment generateBusinessAttachment(BusinessEntry business) {
        List<AttachmentField> fields = new ArrayList<>();
        fields.add(new AttachmentField("Name", business.getName()));
        fields.add(new AttachmentField("Mail", business.getMail()));

        if (business.getQueues() != null) {
            fields.add(new AttachmentField("Queues", String.valueOf(business.getQueues().size())));
        }
        if (business.getKey() != null) {
            fields.add(new AttachmentField("ID", String.valueOf(business.getKey().getId())));
        }

        Attachment attachment = SlackLog.generateAttachment(SlackLog.INFO, BusinessEndpoint.class.getSimpleName(), "");
        attachment.setFields(fields);
        return attachment;
    }

}
