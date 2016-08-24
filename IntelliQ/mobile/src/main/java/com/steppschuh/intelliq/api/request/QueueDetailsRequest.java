package com.steppschuh.intelliq.api.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.steppschuh.intelliq.api.response.BusinessApiResponse;

import java.util.HashMap;
import java.util.Map;

public class QueueDetailsRequest extends SpringAndroidSpiceRequest<BusinessApiResponse> {

    private long keyId;

    public QueueDetailsRequest(long keyId) {
        super(BusinessApiResponse.class);
        this.keyId = keyId;
    }

    @Override
    public BusinessApiResponse loadDataFromNetwork() throws Exception {
        Map<String, String> params = new HashMap<>();

        params.put("includeBusiness", "true");
        if (keyId > -1) {
            params.put("queueKeyId", String.valueOf(keyId));
        } else {
            throw new Exception("Invalid key id provided");
        }

        String url = ApiRequestHelper.getRequestUrl(ApiRequestHelper.ENDPOINT_QUEUE_GET, params);

        return getRestTemplate().getForObject(url, BusinessApiResponse.class);
    }

    public String createCacheKey() {
        return getClass().getSimpleName() + "." + keyId;
    }
}