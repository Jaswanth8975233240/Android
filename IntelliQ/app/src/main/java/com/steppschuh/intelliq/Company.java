package com.steppschuh.intelliq;

import android.text.Html;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class Company {

    String id;
    String name = "Unknown";
    String closingHour = "20";
    String logoUrl = "";
    int waitingTime = 0;
    int peopleInQueue = 0;

    List<QueueItem> queueItems = new ArrayList<>();

    public static Company parseFromJson(JsonObject jsonObject) throws Exception{
        Company company = new Company();

        Log.d(MobileApp.TAG, "Parsing JSON item: " + jsonObject);

        company.setId(jsonObject.getAsJsonPrimitive("id").getAsString());
        company.setName(jsonObject.getAsJsonPrimitive("name").getAsString());
        company.setLogoUrl(jsonObject.getAsJsonPrimitive("logo__c").getAsString());
        company.setClosingHour(jsonObject.getAsJsonPrimitive("closinghour__c").getAsString());
        company.setWaitingTime(jsonObject.getAsJsonPrimitive("waitingtime__c").getAsInt());
        company.setPeopleInQueue(jsonObject.getAsJsonPrimitive("waiting").getAsInt());

        Log.d(MobileApp.TAG, "Item parsed: " + company.getName());

        return company;
    }

    public int getQueuedItemsBeforeCount(QueueItem currentItem) {
        if (!containsQueueItem(currentItem)) {
            return -1;
        }

        int count = 0;
        for (QueueItem queueItem : queueItems) {
            if (queueItem.getCheckinTime() < currentItem.getCheckinTime()) {
                count++;
            }
        }
        return count;
    }

    public boolean containsQueueItem(QueueItem item) {
        for (QueueItem queueItem : queueItems) {
            if (queueItem.getId().equals(item.getId())) {
                return true;
            }
        }

        return false;
    }

    public void deleteQueueItem(QueueItem item) {
        for (QueueItem queueItem : queueItems) {
            if (queueItem.getId().equals((item.getId()))) {
                queueItems.remove(queueItem);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClosingHour() {
        return closingHour;
    }

    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getPeopleInQueue() {
        return peopleInQueue;
    }

    public void setPeopleInQueue(int peopleInQueue) {
        this.peopleInQueue = peopleInQueue;
    }

    public List<QueueItem> getQueueItems() {
        return queueItems;
    }

    public void setQueueItems(List<QueueItem> queueItems) {
        this.queueItems = queueItems;
    }
}
