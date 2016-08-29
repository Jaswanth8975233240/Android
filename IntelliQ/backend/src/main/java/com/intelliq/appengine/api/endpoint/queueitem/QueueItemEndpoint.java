package com.intelliq.appengine.api.endpoint.queueitem;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.api.endpoint.queue.GetNearbyQueuesEndpoint;

public class QueueItemEndpoint extends Endpoint {

	public static final List<Endpoint> endpoints = getAvailableEndpoints();
		
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_QUEUE_ITEM;
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
    	endpoints.add(new GetQueueItemEndpoint());
    	endpoints.add(new AddQueueItemEndpoint());
    	endpoints.add(new GetLastItemInQueueEndpoint());
    	endpoints.add(new SetQueueItemStatusEndpoint());
    	endpoints.add(new DeleteQueueItemEndpoint());
    	return endpoints;
    }
	
}
