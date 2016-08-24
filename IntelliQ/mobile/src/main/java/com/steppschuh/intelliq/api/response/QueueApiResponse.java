package com.steppschuh.intelliq.api.response;

import com.steppschuh.intelliq.api.entry.QueueEntry;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueApiResponse extends ApiResponse {

    private QueueEntry content;

    public QueueEntry getContent() {
        return this.content;
    }

}
