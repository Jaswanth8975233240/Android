package com.intelliq.appengine.logging;

import net.steppschuh.slackmessagebuilder.message.MessageBuilder;
import net.steppschuh.slackmessagebuilder.message.attachment.Attachment;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentBuilder;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentField;
import net.steppschuh.slackmessagebuilder.request.Webhook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public final class SlackLog {

    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/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";

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    private static final Logger log = Logger.getLogger(SlackLog.class.getName());
    private static final Webhook slackWebhook = new Webhook(WEBHOOK_URL);
    private static final MessageBuilder defaultMessageBuilder = getDefaultMessageBuilder();

    public static MessageBuilder getDefaultMessageBuilder() {
        MessageBuilder messageBuilder = new MessageBuilder();
        resetToDefaultMessageBuilder(messageBuilder);
        return messageBuilder;
    }

    public static void resetToDefaultMessageBuilder() {
        resetToDefaultMessageBuilder(defaultMessageBuilder);
    }

    public static void resetToDefaultMessageBuilder(MessageBuilder messageBuilder) {
        messageBuilder
                .setChannel("#log")
                .setUsername("Backend")
                .setText(null)
                .setAttachments(null)
                .setIconEmoji(":intelliq:")
                .setIconUrl(null);
    }

    public static void v(Object tag, Object content) {
        v(tag.getClass().getSimpleName(), content);
    }

    public static void v(String tag, Object content) {
        log.finer(tag + ": " + content.toString());
        Attachment attachment = generateAttachment(VERBOSE, tag, content);
        post(attachment);
    }

    public static void d(Object tag, Object content) {
        d(tag.getClass().getSimpleName(), content);
    }

    public static void d(String tag, Object content) {
        log.fine(tag + ": " + content.toString());
        Attachment attachment = generateAttachment(DEBUG, tag, content);
        post(attachment);
    }

    public static void i(Object tag, Object content) {
        i(tag.getClass().getSimpleName(), content);
    }

    public static void i(String tag, Object content) {
        log.info(tag + ": " + content.toString());
        Attachment attachment = generateAttachment(INFO, tag, content);
        post(attachment);
    }

    public static void w(Object tag, Object content) {
        w(tag.getClass().getSimpleName(), content);
    }

    public static void w(String tag, Object content) {
        log.warning(tag + ": " + content.toString());
        Attachment attachment = generateAttachment(WARN, tag, content);
        post(attachment);
    }

    public static void e(Object tag, Object content) {
        e(tag.getClass().getSimpleName(), content);
    }

    public static void e(String tag, Object content) {
        log.severe(tag + ": " + content.toString());
        Attachment attachment = generateAttachment(ERROR, tag, content);
        post(attachment);
    }

    public static void e(Exception exception) {
        e("Exception", exception);
    }

    public static void e(Object tag, Exception exception) {
        e(tag.getClass().getSimpleName(), exception);
    }

    public static void e(String tag, Exception exception) {
        log.warning(tag + ": " + exception.getMessage());
        String message = exception.getClass().getSimpleName() + ": " + exception.getMessage();
        Attachment attachment = generateAttachment(ERROR, tag, message);

        List<AttachmentField> fields = new ArrayList<>();
        StackTraceElement[] traces = exception.getStackTrace();
        if (traces != null && traces.length > 0) {
            fields.add(new AttachmentField("Method", traces[0].getMethodName()));
            fields.add(new AttachmentField("Line", String.valueOf(traces[0].getLineNumber())));
            String className = traces[0].getClassName();
            int dotIndex = className.lastIndexOf('.');
            if (dotIndex != -1) {
                className = className.substring(dotIndex + 1);
            }
            fields.add(new AttachmentField("Class", className));
        }
        attachment.setFields(fields);

        post(attachment);
    }

    public static void post(Attachment attachment) {
        post(null, attachment);
    }

    public static void post(String message, Attachment attachment) {
        resetToDefaultMessageBuilder();
        defaultMessageBuilder.setText(message);
        defaultMessageBuilder.addAttachment(attachment);
        try {
            slackWebhook.postMessageSynchronous(defaultMessageBuilder.build());
        } catch (Exception ex) {
            log.warning("Unable to post to Slack: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Attachment generateAttachment(int logLevel, String tag, Object content) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        String readableTime = df.format(now);

        StringBuilder sb = new StringBuilder(tag)
                .append(" - ").append(readableTime)
                .append(" (").append(now.getTime()).append(")");

        return getAttachmentBuilder(logLevel)
                .setText(content.toString())
                .setFooter(sb.toString())
                .build();
    }

    public static AttachmentBuilder getAttachmentBuilder(int logLevel) {
        return new AttachmentBuilder()
                .setColor(getHexCode(logLevel));
    }

    public static String getHexCode(int logLevel) {
        switch (logLevel) {
            case DEBUG: {
                return "#607D8B";
            }
            case INFO: {
                return "#009688";
            }
            case WARN: {
                return "#FFC107";
            }
            case ERROR: {
                return "#f44336";
            }
            default: {
                return "#9E9E9E";
            }
        }
    }

}