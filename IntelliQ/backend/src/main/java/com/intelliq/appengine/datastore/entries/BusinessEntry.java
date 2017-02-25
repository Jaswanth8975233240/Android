package com.intelliq.appengine.datastore.entries;

import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.api.ApiRequest;

@PersistenceCapable(detachable = "true")
public class BusinessEntry {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Key key;

    @Persistent
    String name;

    @Persistent
    String mail;

    @Persistent
    long logoImageKeyId;

    ArrayList<QueueEntry> queues;

    public BusinessEntry() {
        logoImageKeyId = -1;
    }

    public void parseFromRequest(ApiRequest req) {
        name = req.getParameter("name", name);
        mail = req.getParameter("mail", mail);
        logoImageKeyId = req.getParameterAsLong("logoImageKeyId", logoImageKeyId);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key generateKey() {
        return KeyFactory.createKey(BusinessEntry.class.getSimpleName(), name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String email) {
        this.mail = email;
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
