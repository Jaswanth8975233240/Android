package com.intelliq.appengine.api.endpoint.queue;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.BusinessHelper;
import com.intelliq.appengine.datastore.PermissionHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;


public class EditQueueEndpoint extends Endpoint {

	public static final long MAXIMUM_QUEUE_CREATIONS = 25;
	
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_QUEUE_EDIT;
	}

	@Override
	public boolean requiresAuthorization(ApiRequest request) {
		return true;
	}
	
	@Override
	public PermissionSet getRequiredPermissions(ApiRequest request) {
		PermissionSet permissionSet = new PermissionSet();
		
		// business editors can also edit queues
		PermissionEntry permissionEntry = new PermissionEntry();
		permissionEntry.setPermission(PermissionEntry.PERMISSION_EDIT);
		permissionEntry.setSubjectKeyId(request.getParameterAsLong("businessKeyId", -1));
		permissionSet.getPermissions().add(permissionEntry);
		
		// queue editors can also edit queues
		permissionEntry = new PermissionEntry();
		permissionEntry.setPermission(PermissionEntry.PERMISSION_EDIT);
		permissionEntry.setSubjectKeyId(request.getParameterAsLong("queueKeyId", -1));
		permissionSet.getPermissions().add(permissionEntry);
		
		return permissionSet;
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
		
		try {
			QueueEntry queueEntry = QueueHelper.getEntryByKeyId(queueKeyId);
			queueEntry.parseFromRequest(request);
			QueueHelper.saveEntry(queueEntry);
			
			//TODO: add action
			
			response.setContent(queueEntry);
		} catch (JDOObjectNotFoundException exception) {
			response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
			response.setException(new Exception("Unable to find requested queue"));
		}
		
		return response;
	}	
	
}
