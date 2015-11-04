package com.steppschuh.intelliq.api.entry;


import com.steppschuh.intelliq.api.DatastoreKey;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueEntry {

    public static final byte VISIBILITY_PRIVATE = 0;
    public static final byte VISIBILITY_PUBLIC = 1;

    public static final int DISTANCE_ANY = -1;
    public static final int DISTANCE_NARROW = 1000;
    public static final int DISTANCE_DEFAULT = 3 * DISTANCE_NARROW;
    public static final int DISTANCE_FAR = 3 * DISTANCE_DEFAULT;

    DatastoreKey key;
    long businessKeyId;

    String name;
    byte visibility;
    long photoImageKeyId;
    long averageWaitingTime;
    int waitingPeople;

    ArrayList<QueueItemEntry> queueItemEntries;

    /**
     * Location
     */
    float latitude;
    float longitude;
    String country;
    String city;
    String postalCode;
    String street;
    String number;

    public QueueEntry() {
        businessKeyId = -1;
        queueItemEntries = new ArrayList<>();
        visibility = VISIBILITY_PUBLIC;
        photoImageKeyId = -1;
        waitingPeople = -1;
        latitude = -1;
        longitude = -1;
        averageWaitingTime = TimeUnit.MINUTES.toMillis(5);
    }

    public String getReadableLocation() {
        String readableLocation;
        if (street != null) {
            readableLocation = street;
            if (number != null) {
                readableLocation += " " + number;
            }
            if (city != null) {
                readableLocation += ", " + city;
            }
        } else {
            readableLocation = "";
        }
        return readableLocation;
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

    public static float getDistance(QueueEntry queueEntry, float latitude, float longitude) {
        if (queueEntry.latitude == -1 || queueEntry.longitude == -1 || latitude == -1 || longitude == -1) {
            return -1;
        } else {
            return getDistance(queueEntry.latitude,  queueEntry.longitude, latitude, longitude);
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

        return new float[] {latitudeMin, longitudeMin, latitudeMax, longitudeMax};
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

    public long getBusinessKeyId() {
        return businessKeyId;
    }

    public void setBusinessKeyId(long businessKeyId) {
        this.businessKeyId = businessKeyId;
    }

    public ArrayList<QueueItemEntry> getQueueItemEntries() {
        return queueItemEntries;
    }

    public void setQueueItemEntries(ArrayList<QueueItemEntry> queueItemEntries) {
        this.queueItemEntries = queueItemEntries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getWaitingPeople() {
        return waitingPeople;
    }

    public void setWaitingPeople(int waitingPeople) {
        this.waitingPeople = waitingPeople;
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
}
