package com.intelliq.appengine.api.endpoint.queue;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;

public class QueueEndpoint extends Endpoint {

    public static final List<Endpoint> endpoints = getAvailableEndpoints();

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE;
    }

    @Override
    public ApiResponse processRequest(ApiRequest request) {
        Endpoint endpoint = EndpointManager.getEndpointForRequest(request, endpoints);
        if (endpoint != null) {
            return endpoint.processRequest(request);
        } else {
            return super.processRequest(request);
        }
    }

    private static List<Endpoint> getAvailableEndpoints() {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        endpoints.add(new GetQueueEndpoint());
        endpoints.add(new AddQueueEndpoint());
        endpoints.add(new EditQueueEndpoint());
        endpoints.add(new GetNearbyQueuesEndpoint());
        endpoints.add(new GetNumberOfItemsInQueueEndpoint());
        endpoints.add(new GetItemsInQueueEndpoint());
        endpoints.add(new MarkAllQueueItemsAsDoneEndpoint());
        endpoints.add(new PopulateQueueEndpoint());
        endpoints.add(new ClearQueueItemsEndpoint());
        return endpoints;
    }

}
