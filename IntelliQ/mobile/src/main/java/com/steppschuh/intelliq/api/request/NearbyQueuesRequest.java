package com.steppschuh.intelliq.api.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.steppschuh.intelliq.api.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

public class NearbyQueuesRequest extends SpringAndroidSpiceRequest<ApiResponse> {

    private float latitude;
    private float longitude;
    private long distance;

    public NearbyQueuesRequest(float latitude, float longitude, long distance) {
        super(ApiResponse.class);
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    @Override
    public ApiResponse loadDataFromNetwork() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("distance", String.valueOf(distance));

        String url = ApiRequestHelper.getRequestUrl(ApiRequestHelper.ENDPOINT_QUEUE_NEARBY, params);

        return getRestTemplate().getForObject(url, ApiResponse.class);
    }

    public String createCacheKey() {
        return getClass().getSimpleName() + "." + latitude + ";" + longitude + ";" + distance + ";";
    }
}