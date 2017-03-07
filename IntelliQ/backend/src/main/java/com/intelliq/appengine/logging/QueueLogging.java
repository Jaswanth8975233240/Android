package com.intelliq.appengine.logging;

import com.intelliq.appengine.api.endpoint.queue.QueueEndpoint;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

import net.steppschuh.slackmessagebuilder.message.attachment.Attachment;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentField;

import java.util.ArrayList;
import java.util.List;

public abstract class QueueLogging {

    public static void logCreation(QueueEntry queue, UserEntry user) {
        Attachment attachment = generateQueueAttachment(queue);
        attachment.setTitle("Queue Created");
        attachment.setText(user.getName() + " created a new queue");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static void logEdit(QueueEntry queue, UserEntry user) {
        Attachment attachment = generateQueueAttachment(queue);
        attachment.setTitle("Queue Edited");
        attachment.setText(user.getName() + " edited an existing queue");
        attachment.setThumbUrl(user.getPictureUrl());
        SlackLog.post(attachment);
    }

    public static Attachment generateQueueAttachment(QueueEntry queue) {
        List<AttachmentField> fields = new ArrayList<>();
        fields.add(new AttachmentField("Name", queue.getName()));
        fields.add(new AttachmentField("City", queue.getCity()));

        if (queue.getVisibility() == QueueEntry.VISIBILITY_PUBLIC) {
            fields.add(new AttachmentField("Visibility", "Public"));
        } else {
            fields.add(new AttachmentField("Visibility", "Private"));
        }
        fields.add(new AttachmentField("Business ID", String.valueOf(queue.getBusinessKeyId())));
        if (queue.getKey() != null) {
            fields.add(new AttachmentField("ID", String.valueOf(queue.getKey().getId())));
        }

        Attachment attachment = SlackLog.generateAttachment(SlackLog.INFO, QueueEndpoint.class.getSimpleName(), "");
        attachment.setFields(fields);
        return attachment;
    }

}
