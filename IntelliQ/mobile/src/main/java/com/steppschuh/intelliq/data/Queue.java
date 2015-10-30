package com.steppschuh.intelliq.data;

public class Queue {

    private long keyId;
    private long averageWaitingTime;
    private long businessKeyId;

    private String name;
    private long photoImageKeyId;
    private int visibility;
    private int waitingPeople;

    public Queue(long keyId, long businessKeyId) {
        this.keyId = keyId;
        this.businessKeyId = businessKeyId;
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

    public long getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(long averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public long getBusinessKeyId() {
        return businessKeyId;
    }

    public void setBusinessKeyId(long businessKeyId) {
        this.businessKeyId = businessKeyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhotoImageKeyId() {
        return photoImageKeyId;
    }

    public void setPhotoImageKeyId(long photoImageKeyId) {
        this.photoImageKeyId = photoImageKeyId;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getWaitingPeople() {
        return waitingPeople;
    }

    public void setWaitingPeople(int waitingPeople) {
        this.waitingPeople = waitingPeople;
    }
}
