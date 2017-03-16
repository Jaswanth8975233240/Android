package com.intelliq.appengine.api.endpoint.queueitem;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;


public class GetQueueItemEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEM_GET;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("queueItemKeyId");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long queueItemKeyId = request.getParameterAsLong("queueItemKeyId", -1);

        try {
            QueueItemEntry queueItemEntry = QueueItemHelper.getEntryByKeyId(queueItemKeyId);
            response.setContent(queueItemEntry);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested queue item"));
        }
        return response;
    }

}
