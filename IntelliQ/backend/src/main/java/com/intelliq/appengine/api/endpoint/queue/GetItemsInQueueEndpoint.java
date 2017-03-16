package com.intelliq.appengine.api.endpoint.queue;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;


public class GetItemsInQueueEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEMS;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("queueKeyId");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long queueKeyId = request.getParameterAsLong("queueKeyId", -1);
        int offset = request.getParameterAsInt("offset", 0);
        int count = request.getParameterAsInt("count", 100);
        byte status = (byte) request.getParameterAsInt("status", QueueItemEntry.STATUS_ALL);

        try {
            List<QueueItemEntry> entries = QueueHelper.getItemsInQueue(queueKeyId, status, offset, offset + count);
            response.setContent(entries);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue"));
        }
        return response;
    }

}
