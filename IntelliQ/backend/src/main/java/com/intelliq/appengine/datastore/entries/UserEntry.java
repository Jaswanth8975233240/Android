package com.intelliq.appengine.datastore.entries;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.datastore.PermissionHelper;

@PersistenceCapable(detachable = "true")
public class UserEntry {

    public static final byte STATUS_DEFAULT = 0;
    public static final byte STATUS_BANNED = -1;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Key key;

    @Persistent
    String googleUserId;

    @Persistent
    String facebookUserId;

    @Persistent
    String name;

    @Persistent
    String mail;

    @Persistent
    String pictureUrl;

    @Persistent
    String locale;

    @Persistent
    byte status;

    @Persistent
    float latitude;

    @Persistent
    float longitude;

    @Persistent
    long lastLocationUpdate;

    @Persistent(defaultFetchGroup = "true")
    @Element(dependent = "true")
    UserStatsEntry stats;

    public UserEntry() {
        super();
        status = STATUS_DEFAULT;
        stats = new UserStatsEntry();
    }

    public UserEntry parseFromGooglePayload(Payload payload) {
        if (payload != null) {
            googleUserId = payload.getSubject();
            name = (String) payload.get("name");
            mail = payload.getEmail();
            pictureUrl = (String) payload.get("picture");
            locale = (String) payload.get("locale");
        }
        return this;
    }

    public boolean hasPermission(PermissionEntry requestedPermission) {
        // set the current user
        requestedPermission.setUserKeyId(key.getId());

        // check if the permission contains any requirements
        if (requestedPermission.getPermission() == PermissionEntry.PERMISSION_NONE) {
            return true;
        }

        // check if the permission subject is valid
        if (requestedPermission.getSubjectKeyId() < 1) {
            return true;
        }

        // check if the permission is about this user
        if (UserEntry.class.getSimpleName().equals(requestedPermission.getSubjectKind())) {
            if (requestedPermission.getSubjectKeyId() == this.key.getId()) {
                return true;
            }
        }

        // look up the PermissionEntry in the data store
        return PermissionHelper.hasPermission(requestedPermission);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        if (key != null) {
            sb.append(" (").append(key.getId()).append(")");
        }
        return sb.toString();
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
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

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public UserStatsEntry getStats() {
        return stats;
    }

    public void setStats(UserStatsEntry stats) {
        this.stats = stats;
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

    public long getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(long lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }

}
