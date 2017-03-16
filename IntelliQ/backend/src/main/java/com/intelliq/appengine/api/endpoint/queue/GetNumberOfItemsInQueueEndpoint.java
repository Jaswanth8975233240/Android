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


public class GetNumberOfItemsInQueueEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_COUNT;
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
        byte status = (byte) request.getParameterAsInt("status", QueueItemEntry.STATUS_ALL);

        try {
            int number = QueueHelper.getNumberOfItemsInQueue(queueKeyId, status);
            response.setContent(number);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue"));
        }
        return response;
    }

}
