package com.intelliq.appengine.api.endpoint.queueitem;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;


public class GetLastItemInQueueEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_ITEM_LAST;
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

        QueueItemEntry lastQueueItemEntry = QueueHelper.getLastAssignedTicketInQueue(queueKeyId, status);
        response.setContent(lastQueueItemEntry);
        return response;
    }

}
