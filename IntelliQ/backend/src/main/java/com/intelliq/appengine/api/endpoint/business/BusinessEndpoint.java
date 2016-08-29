package com.intelliq.appengine.api.endpoint.business;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;

public class BusinessEndpoint extends Endpoint {

	public static final List<Endpoint> endpoints = getAvailableEndpoints();
		
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_BUSINESS;
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
    	endpoints.add(new GetBusinessEndpoint());
    	endpoints.add(new AddBusinessEndpoint());
    	endpoints.add(new EditBusinessEndpoint());
    	endpoints.add(new FromBusinessEndpoint());
    	return endpoints;
    }
	
}
