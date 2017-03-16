package com.intelliq.appengine.api.endpoint.business;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.BusinessHelper;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.PermissionEntry;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


public class FromBusinessEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_BUSINESS_FROM;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("userKeyId");
        return parameters;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();

        long userKeyId = request.getParameterAsLong("userKeyId", -1);
        try {
            List<BusinessEntry> businesses = BusinessHelper.getBusinessesByUserKeyId(userKeyId, PermissionEntry.PERMISSION_VIEW);
            response.setContent(businesses);
        } catch (NucleusObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find businesses"));
        }
        return response;
    }

}
