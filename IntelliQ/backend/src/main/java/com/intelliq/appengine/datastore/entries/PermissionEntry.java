package com.intelliq.appengine.datastore.entries;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class PermissionEntry {

    public static final int PERMISSION_NONE = -1;
    public static final int PERMISSION_VIEW = 0;
    public static final int PERMISSION_EDIT = 1;
    public static final int PERMISSION_OWN = 2;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    Key key;

    @Persistent
    long userKeyId;

    @Persistent
    long subjectKeyId;

    @Persistent
    String subjectKind;

    @Persistent
    int permission;

    public PermissionEntry() {
        this.permission = PERMISSION_NONE;
    }

    public PermissionEntry(long userKeyId, long subjectKeyId, String subjectKind, int permission) {
        this.userKeyId = userKeyId;
        this.subjectKeyId = subjectKeyId;
        this.subjectKind = subjectKind;
        this.permission = permission;
    }

    public boolean matches(long userKeyId, long subjectKeyId, int permission) {
        if (this.userKeyId != userKeyId || this.subjectKeyId != subjectKeyId) {
            return false;
        }

        // required permission level is higher than the granted one
        if (this.permission < permission) {
            return false;
        }

        return true;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public long getUserKeyId() {
        return userKeyId;
    }

    public void setUserKeyId(long userKeyId) {
        this.userKeyId = userKeyId;
    }

    public long getSubjectKeyId() {
        return subjectKeyId;
    }

    public void setSubjectKeyId(long subbjectKeyId) {
        this.subjectKeyId = subbjectKeyId;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getSubjectKind() {
        return subjectKind;
    }

    public void setSubjectKind(String subjectKind) {
        this.subjectKind = subjectKind;
    }

}
