package com.steppschuh.intelliq.api;

public class DatastoreKey {

    private String kind;
    private long id;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
