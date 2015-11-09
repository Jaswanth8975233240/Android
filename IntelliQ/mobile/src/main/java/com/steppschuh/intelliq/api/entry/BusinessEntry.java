package com.steppschuh.intelliq.api.entry;

import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.api.DatastoreKey;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessEntry {

    DatastoreKey key;

    String name;
    String email;
    long logoImageKeyId;

    ArrayList<QueueEntry> queues;

    public BusinessEntry() {
    }

    public String getReadableDescription(IntelliQ app) {
        if (queues != null && queues.size() > 0) {
            if (app.getUser().hasValidLocation()) {
                float latitude = app.getUser().getLatitude();
                float longitude = app.getUser().getLongitude();
                return queues.get(0).getReadableDistanceTo(latitude, longitude, app);
            } else {
                return queues.get(0).getReadableLocation();
            }
        }
        return "";
    }

    /**
     * Getter & Setter
     */
    public DatastoreKey getKey() {
        return key;
    }

    public void setKey(DatastoreKey key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLogoImageKeyId() {
        return logoImageKeyId;
    }

    public void setLogoImageKeyId(long logoImageKeyId) {
        this.logoImageKeyId = logoImageKeyId;
    }

    public ArrayList<QueueEntry> getQueues() {
        return queues;
    }

    public void setQueues(ArrayList<QueueEntry> queues) {
        this.queues = queues;
    }
}
