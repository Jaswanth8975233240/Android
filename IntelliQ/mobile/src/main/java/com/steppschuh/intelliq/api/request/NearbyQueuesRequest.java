package com.steppschuh.intelliq.api.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.steppschuh.intelliq.api.response.ApiResponse;
import com.steppschuh.intelliq.api.response.QueueListApiResponse;

import java.util.HashMap;
import java.util.Map;

public class NearbyQueuesRequest extends SpringAndroidSpiceRequest<QueueListApiResponse> {

    private float latitude;
    private float longitude;
    private long distance;

    public NearbyQueuesRequest(float latitude, float longitude, long distance) {
        super(QueueListApiResponse.class);
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    @Override
    public QueueListApiResponse loadDataFromNetwork() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("distance", String.valueOf(distance));

        String url = ApiRequestHelper.getRequestUrl(ApiRequestHelper.ENDPOINT_QUEUE_NEARBY, params);

        return getRestTemplate().getForObject(url, QueueListApiResponse.class);
    }

    public String createCacheKey() {
        return getClass().getSimpleName() + "." + latitude + ";" + longitude + ";" + distance + ";";
    }
}