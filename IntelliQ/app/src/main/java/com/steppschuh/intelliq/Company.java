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
    String name;
    String closingHour;
    String logoUrl;
    int waitingTime;

    List<QueueItem> queueItems = new ArrayList<>();

    /*
   "attributes": {
     "type": "company__c",
     "url": "/services/data/v33.0/sobjects/company__c/a022000000jCYZOAA4"
   },
   "Id": "a022000000jCYZOAA4",
   "Name": "Blind Barber Harry",
   "closingHour__c": 20,
   "logo__c": "http://chic-chester.co.uk/wp-content/uploads/2014/08/20140806_LogoSupporterPlaceholder.png",
   "waitingTime__c": 20
    */


    public static Company parseFromJson(JsonObject jsonObject) throws Exception{
        Company company = new Company();

        Log.d(MobileApp.TAG, "Parsing JSON item: " + jsonObject);

        company.setId(jsonObject.getAsJsonPrimitive("id").getAsString());
        String title = jsonObject.getAsJsonObject("title").getAsJsonPrimitive("value").getAsString();
        company.setName(title);

        Log.d(MobileApp.TAG, "Parsing item: " + company.getName());

        String description = jsonObject.getAsJsonObject("description").getAsJsonPrimitive("value").getAsString();
        //company.setDescription(Html.fromHtml(description).toString());

        return company;
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

    public List<QueueItem> getQueueItems() {
        return queueItems;
    }

    public void setQueueItems(List<QueueItem> queueItems) {
        this.queueItems = queueItems;
    }
}
