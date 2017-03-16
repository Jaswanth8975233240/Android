package com.intelliq.appengine.api.endpoint;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.endpoint.business.BusinessEndpoint;
import com.intelliq.appengine.api.endpoint.queue.QueueEndpoint;
import com.intelliq.appengine.api.endpoint.queueitem.QueueItemEndpoint;
import com.intelliq.appengine.api.endpoint.user.UserEndpoint;

import java.util.ArrayList;
import java.util.List;

public final class EndpointManager {

    public static final int API_VERSION = 1;

    public static final String DEFAULT_HOST = "http://intelliq.me/";
    public static final String VERSIONED_HOST = "http://" + API_VERSION + "-dot-intelliq-me.appspot.com/";
    public static final String HOST = DEFAULT_HOST;
    public static final String HOST_LOCAL_DEV_SERVER = "localhost:8888";

    public static final String ENDPOINT_API = "api/";

    // Businesses
    public static final String ENDPOINT_BUSINESS = ENDPOINT_API + "business/";
    public static final String ENDPOINT_BUSINESS_GET = ENDPOINT_BUSINESS + "get/";
    public static final String ENDPOINT_BUSINESS_ADD = ENDPOINT_BUSINESS + "add/";
    public static final String ENDPOINT_BUSINESS_EDIT = ENDPOINT_BUSINESS + "edit/";
    public static final String ENDPOINT_BUSINESS_FROM = ENDPOINT_BUSINESS + "from/";

    // Queues
    public static final String ENDPOINT_QUEUE = ENDPOINT_API + "queue/";
    public static final String ENDPOINT_QUEUE_NEARBY = ENDPOINT_QUEUE + "nearby/";
    public static final String ENDPOINT_QUEUE_GET = ENDPOINT_QUEUE + "get/";
    public static final String ENDPOINT_QUEUE_ADD = ENDPOINT_QUEUE + "add/";
    public static final String ENDPOINT_QUEUE_EDIT = ENDPOINT_QUEUE + "edit/";
    public static final String ENDPOINT_QUEUE_POPULATE = ENDPOINT_QUEUE + "populate/";
    public static final String ENDPOINT_QUEUE_DONE = ENDPOINT_QUEUE + "done/";
    public static final String ENDPOINT_QUEUE_CLEAR = ENDPOINT_QUEUE + "clear/";
    public static final String ENDPOINT_QUEUE_COUNT = ENDPOINT_QUEUE + "count/";
    public static final String ENDPOINT_QUEUE_DELETE = ENDPOINT_QUEUE + "delete/";
    public static final String ENDPOINT_QUEUE_ITEMS = ENDPOINT_QUEUE + "items/";

    // Queue Items
    public static final String ENDPOINT_QUEUE_ITEM = ENDPOINT_API + "item/";
    public static final String ENDPOINT_QUEUE_ITEM_GET = ENDPOINT_QUEUE_ITEM + "get/";
    public static final String ENDPOINT_QUEUE_ITEM_ADD = ENDPOINT_QUEUE_ITEM + "add/";
    public static final String ENDPOINT_QUEUE_ITEM_FROM = ENDPOINT_QUEUE_ITEM + "from/";
    public static final String ENDPOINT_QUEUE_ITEM_STATUS = ENDPOINT_QUEUE_ITEM + "status/";
    public static final String ENDPOINT_QUEUE_ITEM_DELETE = ENDPOINT_QUEUE_ITEM + "delete/";
    public static final String ENDPOINT_QUEUE_ITEM_LAST = ENDPOINT_QUEUE_ITEM + "last/";

    // Users
    public static final String ENDPOINT_USER = ENDPOINT_API + "user/";
    public static final String ENDPOINT_USER_GET = ENDPOINT_USER + "get/";
    public static final String ENDPOINT_USER_SIGNIN = ENDPOINT_USER + "signin/";
    public static final String ENDPOINT_USER_SET_LOCATION = ENDPOINT_USER + "set/location/";
    public static final String ENDPOINT_USER_SET_STATUS = ENDPOINT_USER + "set/status/";

    // Images
    public static final String ENDPOINT_IMAGE = "image/";

    public static final List<Endpoint> endpoints = getAvailableEndpoints();

    private EndpointManager() {

    }

    private static List<Endpoint> getAvailableEndpoints() {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        endpoints.add(new QueueEndpoint());
        endpoints.add(new QueueItemEndpoint());
        endpoints.add(new BusinessEndpoint());
        endpoints.add(new UserEndpoint());
        return endpoints;
    }

    public static Endpoint getEndpointForRequest(ApiRequest request) {
        return getEndpointForRequest(request, endpoints);
    }

    public static Endpoint getEndpointForRequest(ApiRequest request, List<Endpoint> availableEndpoints) {
        for (Endpoint endpoint : availableEndpoints) {
            if (endpoint.shouldHandleRequest(request)) {
                return endpoint;
            }
        }
        return null;
    }

}
