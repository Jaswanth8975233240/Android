package com.steppschuh.intelliq.api.response;

import com.steppschuh.intelliq.api.entry.BusinessEntry;
import com.steppschuh.intelliq.api.entry.QueueEntry;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessListApiResponse extends ApiResponse {

    private ArrayList<BusinessEntry> content;

    public ArrayList<BusinessEntry> getContent() {
        return this.content;
    }

}
