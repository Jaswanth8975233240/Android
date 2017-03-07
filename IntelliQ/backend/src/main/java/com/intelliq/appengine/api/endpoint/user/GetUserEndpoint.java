package com.intelliq.appengine.api.endpoint.user;

import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;
import com.intelliq.appengine.logging.SlackLog;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;


public class GetUserEndpoint extends Endpoint {

    @Override
    public String getEndpointPath() {
        return EndpointManager.ENDPOINT_USER_GET;
    }

    @Override
    public boolean requiresAuthorization(ApiRequest request) {
        return true;
    }

    @Override
    public PermissionSet getRequiredPermissions(ApiRequest request) {
        PermissionSet permissionSet = new PermissionSet();

        // user wants to get his own entry
        PermissionEntry permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_OWN);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("userKeyId", -1));
        permissionEntry.setSubjectKind(UserEntry.class.getSimpleName());
        permissionSet.getPermissions().add(permissionEntry);

        // queue management want's to get user details
        permissionEntry = new PermissionEntry();
        permissionEntry.setPermission(PermissionEntry.PERMISSION_VIEW);
        permissionEntry.setSubjectKeyId(request.getParameterAsLong("queueKeyId", -1));
        permissionEntry.setSubjectKind(QueueEntry.class.getSimpleName());
        permissionSet.getPermissions().add(permissionEntry);

        return permissionSet;
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
            UserEntry userEntry = UserHelper.getEntryByKeyId(userKeyId);
            response.setContent(userEntry);
        } catch (JDOObjectNotFoundException exception) {
            response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            response.setException(new Exception("Unable to find requested user"));
            SlackLog.v(this, "User not found: " + userKeyId);
        }
        return response;
    }

}
