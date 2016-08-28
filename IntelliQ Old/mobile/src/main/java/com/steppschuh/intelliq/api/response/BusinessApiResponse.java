package com.steppschuh.intelliq.api.response;

import com.steppschuh.intelliq.api.entry.BusinessEntry;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessApiResponse extends ApiResponse {

    private BusinessEntry content;

    public BusinessEntry getContent() {
        return this.content;
    }

}
