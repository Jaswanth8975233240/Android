package com.intelliq.appengine;

import net.steppschuh.slackmessagebuilder.message.attachment.Attachment;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentBuilder;
import net.steppschuh.slackmessagebuilder.message.attachment.AttachmentField;
import net.steppschuh.slackmessagebuilder.message.MessageBuilder;
import net.steppschuh.slackmessagebuilder.message.MessageLink;
import net.steppschuh.slackmessagebuilder.request.Webhook;

public class MessageBuilderTest {

    //@Test
    public void builder_exampleMessage() throws Exception {
        // create a webhook
        String hookUrl = "https://hooks.slack.com/services/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";
        Webhook webhook = new Webhook(hookUrl);

        // create some content
        MessageLink gitHubLink = new MessageLink("https://github.com/Steppschuh/SlackMessageBuilder", "GitHub repo");

        // create a message
        MessageBuilder messageBuilder = new MessageBuilder()
                .setChannel("#log")
                .setUsername("Slack Message Builder")
                .setIconEmoji(":+1:")
                .setText("I'm the message text with a link to a " + gitHubLink + " :octocat:");

        // add some attachments
        int attachmentCount = 3;
        for (int index = 1; index <= attachmentCount; index++) {
            messageBuilder.addAttachment(generateMessageAttachment(index));
        }

        // send message
        webhook.postMessageSynchronous(messageBuilder.build());
    }

    public static Attachment generateMessageAttachment(int index) {
        AttachmentBuilder attachmentBuilder = new AttachmentBuilder()
                .setTitle("Attachment #" + index)
                .setText("This is the main text in message attachment " + index + ", and can contain standard message markup.")
                .setColor(getHexCode(index))
                .addField(new AttachmentField("User", System.getProperty("user.name")))
                .addField(new AttachmentField("Index", String.valueOf(index)))
                .setFooter("Created by " + MessageBuilderTest.class.getSimpleName());

        return attachmentBuilder.build();
    }

    public static String getHexCode(int index) {
        String[] colors = {"#00BCD4", "#2196F3", "#673AB7", "#E91E63", "#795548"};
        return colors[Math.abs(index % (colors.length - 1))];
    }

}