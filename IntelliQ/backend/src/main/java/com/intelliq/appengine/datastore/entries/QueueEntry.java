package com.intelliq.appengine.datastore.entries;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.api.ApiRequest;

import java.util.concurrent.TimeUnit;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class QueueEntry {

    public static final byte VISIBILITY_PRIVATE = 0;
    public static final byte VISIBILITY_PUBLIC = 1;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Key key;

    @Persistent
    long businessKeyId;

    @Persistent
    String name;

    @Persistent
    String description;

    @Persistent
    byte visibility;

    @Persistent
    long photoImageKeyId;

    @Persistent
    long averageWaitingTime;

    @Persistent
    boolean requiresSignIn;

    @Persistent
    boolean textNotificationsEnabled;

    int waitingPeople;

    /**
     * Location
     */
    @Persistent
    float latitude;
    @Persistent
    float longitude;

    @Persistent
    String country;
    @Persistent
    String city;
    @Persistent
    String postalCode;
    @Persistent
    String street;
    @Persistent
    String number;

    public QueueEntry(long businessKeyId) {
        this.businessKeyId = businessKeyId;
        visibility = VISIBILITY_PUBLIC;
        photoImageKeyId = -1;
        waitingPeople = -1;
        latitude = -1;
        longitude = -1;
        requiresSignIn = false;
        textNotificationsEnabled = false;
        averageWaitingTime = TimeUnit.MINUTES.toMillis(5);
    }

    public void parseFromRequest(ApiRequest req) {
        businessKeyId = req.getParameterAsLong("businessKeyId", businessKeyId);
        name = req.getParameter("name", name);
        description = req.getParameter("description", description);
        visibility = (byte) req.getParameterAsInt("visibility", visibility);
        averageWaitingTime = req.getParameterAsLong("averageWaitingTime", averageWaitingTime);
        country = req.getParameter("country", country);
        city = req.getParameter("city", city);
        postalCode = req.getParameter("postalCode", postalCode);
        street = req.getParameter("street", street);
        number = req.getParameter("number", number);
        latitude = req.getParameterAsFloat("latitude", latitude);
        longitude = req.getParameterAsFloat("longitude", longitude);
        requiresSignIn = req.getParameterAsBoolean("requiresSignIn", requiresSignIn);
    }

    /*
     * Methods for calculating the distance between two locations in meters
     */
    public boolean hasValidLocation() {
        if (latitude == -1 && longitude == -1) {
            return false;
        }
        return true;
    }

    public float getDistanceTo(float latitude, float longitude) {
        return getDistance(this, latitude, longitude);
    }

    public static float getDistance(QueueEntry queue, float latitude, float longitude) {
        if (queue.latitude == -1 || queue.longitude == -1 || latitude == -1 || longitude == -1) {
            return -1;
        } else {
            return getDistance(queue.latitude, queue.longitude, latitude, longitude);
        }
    }

    public static float getDistance(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public float[] offsetLocationToRange(long distance) {
        return offsetLocationToRange(latitude, longitude, distance);
    }

    public static float[] offsetLocationToRange(float latitude, float longitude, long distance) {
        float latitudeDelta = Math.abs((float) (distance / 1000 / 111.111));
        float longitudeDelta = Math.abs((float) (distance / 1000 / (111.111 * Math.cos(latitude))));

        float latitudeMin = latitude - latitudeDelta;
        float latitudeMax = latitude + latitudeDelta;
        float longitudeMin = longitude - longitudeDelta;
        float longitudeMax = longitude + longitudeDelta;

        return new float[]{latitudeMin, longitudeMin, latitudeMax, longitudeMax};
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key generateKey() {
        return KeyFactory.createKey(QueueEntry.class.getSimpleName(), name);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte getVisibility() {
        return visibility;
    }

    public void setVisibility(byte visibility) {
        this.visibility = visibility;
    }

    public long getPhotoImageKeyId() {
        return photoImageKeyId;
    }

    public void setPhotoImageKeyId(long photoImageKeyId) {
        this.photoImageKeyId = photoImageKeyId;
    }

    public long getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(long averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getWaitingPeople() {
        return waitingPeople;
    }

    public void setWaitingPeople(int waitingPeople) {
        this.waitingPeople = waitingPeople;
    }

    public boolean getRequiresSignIn() {
        return requiresSignIn;
    }

    public void setRequiresSignIn(boolean requiresSignIn) {
        this.requiresSignIn = requiresSignIn;
    }

    public boolean isTextNotificationsEnabled() {
        return textNotificationsEnabled;
    }

    public void setTextNotificationsEnabled(boolean textNotificationsEnabled) {
        this.textNotificationsEnabled = textNotificationsEnabled;
    }

}
