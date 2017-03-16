package com.intelliq.appengine.api.endpoint.user;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class SetUserLocationEndpoint extends Endpoint {

    private static final Logger log = Logger.getLogger(SetUserLocationEndpoint.class.getSimpleName());

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_USER_SET_LOCATION;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        return true;
    }

    @Override
    public List<String> getRequiredParameters(ApiRequest request) {
        List<String> parameters = new ArrayList<String>();
        parameters.add("latitude");
        parameters.add("longitude");
        parameters.add("userKeyId");
        return parameters;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();

        // user wants to update his own location
        PermissionEntry permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_OWN);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("userKeyId", -1));
        permissionEntry.setSubjectKind(UserEntry.class.getSimpleName());
        permissionSet.getPermissions().add(permissionEntry);

        return permissionSet;
    }

    @Override
    public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
        ApiResponse response = new ApiResponse();
        float latitude = request.getParameterAsFloat("latitude", -1);
        float longitude = request.getParameterAsFloat("longitude", -1);

        // get the user that initiated the request
        UserEntry user = request.getUser();

        // update the location
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setLastLocationUpdate((new Date()).getTime());
        UserHelper.saveEntry(user);

        log.info("User " + user.getName() + " location updated");
        response.setContent(user);
        return response;
    }

}
