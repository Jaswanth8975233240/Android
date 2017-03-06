package com.intelliq.appengine.datastore.entries;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.datastore.Location;

@PersistenceCapable(detachable = "true")
public class QueueItemEntry {

    public static final byte STATUS_ALL = -1;
    public static final byte STATUS_WAITING = 0;
    public static final byte STATUS_CANCELED = 1;
    public static final byte STATUS_CALLED = 2;
    public static final byte STATUS_DONE = 3;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Key key;

    @Persistent
    long queueKeyId;

    @Persistent
    long userKeyId;

    @Persistent
    String name;

    @Persistent
    boolean showName;

    @Persistent
    boolean usingApp;

    @Persistent
    String phoneNumber;

    @Persistent
    long entryTimestamp;

    @Persistent
    long lastStatusChangeTimestamp;

    @Persistent
    int ticketNumber;

    @Persistent
    byte status;

    public QueueItemEntry(long queueKeyId) {
        this.queueKeyId = queueKeyId;
        entryTimestamp = new Date().getTime();
        lastStatusChangeTimestamp = entryTimestamp;
        status = STATUS_WAITING;
        showName = true;
        usingApp = true;
        ticketNumber = -1;
        userKeyId = -1;
    }

    public void parseFromRequest(ApiRequest req) {
        queueKeyId = req.getParameterAsLong("queueKeyId", queueKeyId);
        userKeyId = req.getParameterAsLong("userKeyId", userKeyId);
        name = req.getParameter("name", name);
        phoneNumber = req.getParameter("phoneNumber", phoneNumber);
        showName = req.getParameterAsBoolean("showName", showName);
        usingApp = req.getParameterAsBoolean("usingApp", usingApp);
    }

    public void makeDummyItem() {
        name = "Dummy Customer";
        entryTimestamp = (long) (new Date().getTime() - (Math.random() * TimeUnit.MINUTES.toMillis(60)));
        lastStatusChangeTimestamp = entryTimestamp;

        if (Math.random() < 0.5) {
            // make sure we have more people waiting
            status = STATUS_WAITING;
        } else {
            // set random status
            status = (byte) Math.round(Math.random() * 3);
        }

        showName = Math.random() < 0.5;
        usingApp = Math.random() < 0.5;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key generateKey() {
        return KeyFactory.createKey(QueueItemEntry.class.getSimpleName(), name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public boolean isUsingApp() {
        return usingApp;
    }

    public void setUsingApp(boolean usingApp) {
        this.usingApp = usingApp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(long entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
        this.lastStatusChangeTimestamp = entryTimestamp;
    }

    public long getQueueKeyId() {
        return queueKeyId;
    }

    public void setQueueKeyId(long queueKeyId) {
        this.queueKeyId = queueKeyId;
    }

    public long getUserKeyId() {
        return userKeyId;
    }

    public void setUserKeyId(long userKeyId) {
        this.userKeyId = userKeyId;
    }

    public long getLastStatusChangeTimestamp() {
        return lastStatusChangeTimestamp;
    }

    public void setLastStatusChangeTimestamp(long lastStatusChangeTimestamp) {
        this.lastStatusChangeTimestamp = lastStatusChangeTimestamp;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public static class EntryTimestampComparator implements Comparator<QueueItemEntry> {
        @Override
        public int compare(QueueItemEntry one, QueueItemEntry another) {
            int returnVal = 0;
            if (one.getEntryTimestamp() < another.getEntryTimestamp()) {
                returnVal = -1;
            } else if (one.getEntryTimestamp() > another.getEntryTimestamp()) {
                returnVal = 1;
            } else if (one.getEntryTimestamp() == another.getEntryTimestamp()) {
                returnVal = 0;
            }
            return returnVal;
        }
    }

}
