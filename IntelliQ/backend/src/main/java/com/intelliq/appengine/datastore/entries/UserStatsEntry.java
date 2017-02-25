package com.intelliq.appengine.datastore.entries;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class UserStatsEntry {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    transient Key key;

    @Persistent(mappedBy = "stats")
    transient UserEntry user;

    @Persistent
    long queuesJoined;

    @Persistent
    long queuesCreated;

    @Persistent
    long businessesCreated;

    @Persistent
    long lastSignIn;

    @Persistent
    long firstSignIn;

    @Persistent
    long reports;

    public UserStatsEntry() {
        super();
        queuesJoined = 0;
        queuesCreated = 0;
        firstSignIn = (new Date()).getTime();
        lastSignIn = firstSignIn;
        reports = 0;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public UserEntry getUser() {
        return user;
    }

    public void setUser(UserEntry user) {
        this.user = user;
    }

    public long getQueuesJoined() {
        return queuesJoined;
    }

    public void setQueuesJoined(long queuesJoined) {
        this.queuesJoined = queuesJoined;
    }

    public long getQueuesCreated() {
        return queuesCreated;
    }

    public void setQueuesCreated(long queuesCreated) {
        this.queuesCreated = queuesCreated;
    }

    public long getLastSignIn() {
        return lastSignIn;
    }

    public void setLastSignIn(long lastSignIn) {
        this.lastSignIn = lastSignIn;
    }

    public long getFirstSignIn() {
        return firstSignIn;
    }

    public void setFirstSignIn(long firstSignIn) {
        this.firstSignIn = firstSignIn;
    }

    public long getReports() {
        return reports;
    }

    public void setReports(long reports) {
        this.reports = reports;
    }

    public long getBusinessesCreated() {
        return businessesCreated;
    }

    public void setBusinessesCreated(long businessesCreated) {
        this.businessesCreated = businessesCreated;
    }

}
