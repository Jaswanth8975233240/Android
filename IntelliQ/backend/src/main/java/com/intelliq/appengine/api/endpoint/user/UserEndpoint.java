package com.intelliq.appengine.api.endpoint.user;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;

public class UserEndpoint extends Endpoint {

    public static final List<Endpoint> endpoints = getAvailableEndpoints();

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_USER;
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
        endpoints.add(new GetUserEndpoint());
        endpoints.add(new SignInUserEndpoint());
        endpoints.add(new SetUserLocationEndpoint());
        return endpoints;
    }

}
