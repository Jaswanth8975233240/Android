package com.steppschuh.intelliq.api.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.steppschuh.intelliq.api.user.User;
import com.steppschuh.intelliq.api.response.QueueListApiResponse;

import java.util.HashMap;
import java.util.Map;

public class NearbyQueuesRequest extends SpringAndroidSpiceRequest<QueueListApiResponse> {

    private float latitude;
    private float longitude;
    private String postalCode;
    private long distance;

    public NearbyQueuesRequest(float latitude, float longitude, long distance) {
        super(QueueListApiResponse.class);
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public NearbyQueuesRequest(String postalCode) {
        super(QueueListApiResponse.class);
        this.postalCode = postalCode;
        latitude = -1;
        longitude = -1;
    }

    @Override
    public QueueListApiResponse loadDataFromNetwork() throws Exception {
        Map<String, String> params = new HashMap<>();

        if (User.isValidLocation(latitude, longitude)) {
            params.put("latitude", String.valueOf(latitude));
            params.put("longitude", String.valueOf(longitude));
            params.put("distance", String.valueOf(distance));
        } else if (User.isValidPostalCode(postalCode)) {
            params.put("postalCode", String.valueOf(postalCode));
        } else {
            throw new Exception("Invalid location info provided");
        }

        String url = ApiRequestHelper.getRequestUrl(ApiRequestHelper.ENDPOINT_QUEUE_NEARBY, params);

        return getRestTemplate().getForObject(url, QueueListApiResponse.class);
    }

    public String createCacheKey() {
        return getClass().getSimpleName() + "." + latitude + ";" + longitude + ";" + distance + ";";
    }
}