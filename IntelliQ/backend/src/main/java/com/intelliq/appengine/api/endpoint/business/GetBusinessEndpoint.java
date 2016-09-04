package com.intelliq.appengine.api.endpoint.business;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.BusinessHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;


public class GetBusinessEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_BUSINESS_GET;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("businessKeyId");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long businessKeyId = request.getParameterAsLong("businessKeyId", -1);
        boolean includeQueues = request.getParameterAsBoolean("includeQueues", true);

        try {
            BusinessEntry businessEntry = BusinessHelper.getEntryByKeyId(businessKeyId);
            if (includeQueues) {
                businessEntry.setQueues(QueueHelper.getQueuesByBusiness(businessKeyId));
                for (QueueEntry queue : businessEntry.getQueues()) {
                    queue.setWaitingPeople(QueueHelper.getNumberOfItemsInQueue(queue.getKey().getId(), QueueItemEntry.STATUS_WAITING));
                }
            }

            response.setContent(businessEntry);
        } catch (NucleusObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested business"));
        }
        return response;
    }

}
