package com.steppschuh.intelliq;

import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class QueueItem {

    /*
    "attributes": {
      "type": "QItem__c",
      "url": "/services/data/v33.0/sobjects/QItem__c/a012000001nq8PjAAI"
    },
    "Id": "a012000001nq8PjAAI",
    "Name": "Harry",
    "position__c": "15",
    "company__c": "a022000000jCYZOAA4",
    "checkinTime__c": 1434200706461
     */

    String id;
    String name;
    String companyId;
    String status;
    long checkinTime;
    int position;
    int ticketNumber;

    public static QueueItem parseFromJson(JsonObject jsonObject) throws Exception{
        QueueItem queueItem = new QueueItem();

        Log.d(MobileApp.TAG, "Parsing JSON item: " + jsonObject);

        queueItem.setId(jsonObject.getAsJsonPrimitive("Id").getAsString());
        queueItem.setName(jsonObject.getAsJsonPrimitive("Name").getAsString());

        Log.d(MobileApp.TAG, "Item parsed: " + queueItem.getName());

        return queueItem;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public long getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(long checkinTime) {
        this.checkinTime = checkinTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
}
