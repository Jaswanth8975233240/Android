package com.intelliq.appengine.api.endpoint.queue;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.Location;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;


public class GetNearbyQueuesEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_QUEUE_NEARBY;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();

        String postalCode = request.getParameter("postalCode");
        if (!ParserHelper.containsAnyValue(postalCode)) {
            parameters.add("latitude");
            parameters.add("longitude");
        }

        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        boolean includeBusinesses = request.getParameterAsBoolean("includeBusinesses", false);
        String postalCode = request.getParameter("postalCode");
        List<QueueEntry> nearbyQueues = new ArrayList<QueueEntry>();

        if (ParserHelper.containsAnyValue(postalCode)) {
            // use postal code
            nearbyQueues = QueueHelper.getQueuesByPostalCode(postalCode);
        } else {
            // use latitude & longitude
            float latitude = request.getParameterAsFloat("latitude", -1);
            float longitude = request.getParameterAsFloat("longitude", -1);
            long distance = request.getParameterAsLong("distance", Location.DISTANCE_DEFAULT);

            nearbyQueues = QueueHelper.getQueuesByLocation(latitude, longitude, distance);
        }

        // update queue number entries
        for (int i = 0; i < nearbyQueues.size(); i++) {
            nearbyQueues.get(i).setWaitingPeople(QueueHelper.getNumberOfItemsInQueue(nearbyQueues.get(i).getKey().getId(), QueueItemEntry.STATUS_WAITING));
        }

        if (includeBusinesses) {
            response.setContent(QueueHelper.getBusinessesForQueues(nearbyQueues));
        } else {
            response.setContent(nearbyQueues);
        }
        return response;
    }

}
