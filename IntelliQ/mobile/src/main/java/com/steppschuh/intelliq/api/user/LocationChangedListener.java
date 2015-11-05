package com.steppschuh.intelliq.api.user;

public interface LocationChangedListener {

    public abstract void onLocationChanged(float latitude, float longitude);
    public abstract void onLocationChanged(String postalCode);

}
