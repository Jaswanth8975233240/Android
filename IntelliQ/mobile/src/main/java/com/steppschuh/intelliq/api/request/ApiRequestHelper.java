package com.steppschuh.intelliq.api.request;

import android.util.Log;

import com.steppschuh.intelliq.IntelliQ;

import java.util.ArrayList;
import java.util.Map;

public class ApiRequestHelper {

    public static final int API_VERSION = 1;
    public static final String DEFAULT_HOST = "http://intelliq.me/";
    public static final String VERSIONED_HOST = "http://" + API_VERSION + "-dot-intelliq-me.appspot.com/";
    public static final String HOST = VERSIONED_HOST;

    public static final String ENDPOINT_API = "api/";

    // Businesses
    public static final String ENDPOINT_BUSINESS = ENDPOINT_API + "business/";

    // Queues
    public static final String ENDPOINT_QUEUE = ENDPOINT_API + "queue/";
    public static final String ENDPOINT_QUEUE_NEARBY = ENDPOINT_QUEUE + "nearby/";
    public static final String ENDPOINT_QUEUE_GET = ENDPOINT_QUEUE + "get/";
    public static final String ENDPOINT_QUEUE_ADD = ENDPOINT_QUEUE + "add/";
    public static final String ENDPOINT_QUEUE_POPULATE = ENDPOINT_QUEUE + "populate/";
    public static final String ENDPOINT_QUEUE_DONE = ENDPOINT_QUEUE + "done/";
    public static final String ENDPOINT_QUEUE_CLEAR = ENDPOINT_QUEUE + "clear/";
    public static final String ENDPOINT_QUEUE_COUNT = ENDPOINT_QUEUE + "count/";

    // Queue Items
    public static final String ENDPOINT_QUEUE_ITEM = ENDPOINT_API + "item/";

    // Images
    public static final String ENDPOINT_IMAGE = "image/";


    public static String getRequestUrl(String endpoint, Map<String, String> params) {
        String url = HOST + endpoint;

        if (params != null && params.entrySet().size() > 0) {
            url += "?";
            for (Map.Entry<String, String> param : params.entrySet()) {
                url += param.getKey() + "=" + param.getValue() + "&";
            }
            url = url.substring(0, url.length() - 1);
        }

        Log.v(IntelliQ.TAG, "API request url built: " + url);

        return url;
    }

}
