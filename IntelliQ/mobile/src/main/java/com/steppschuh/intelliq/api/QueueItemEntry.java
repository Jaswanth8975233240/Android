package com.steppschuh.intelliq.api;

import java.util.Comparator;
import java.util.Date;

public class QueueItemEntry {

    public static final byte STATUS_ALL = -1;
    public static final byte STATUS_WAITING = 0;
    public static final byte STATUS_CANCELED = 1;
    public static final byte STATUS_CALLED = 2;
    public static final byte STATUS_DONE = 3;

    long keyId;
    long queueKeyId;
    String name;
    boolean showName;
    boolean usingApp;
    long entryTimestamp;
    long lastStatusChangeTimestamp;
    int ticketNumber;
    byte status;
    float latitude;
    float longitude;

    public QueueItemEntry(long queueKeyId) {
        this.queueKeyId = queueKeyId;
        entryTimestamp = new Date().getTime();
        lastStatusChangeTimestamp = entryTimestamp;
        status = STATUS_WAITING;
        showName = true;
        usingApp = true;
        latitude = -1;
        longitude = -1;
        ticketNumber = -1;
    }


    /**
     * Getter & Setter
     */
    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
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

    public long getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(long entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
        this.lastStatusChangeTimestamp = entryTimestamp;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public long getQueueKeyId() {
        return queueKeyId;
    }

    public void setQueueKeyId(long queueKeyId) {
        this.queueKeyId = queueKeyId;
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