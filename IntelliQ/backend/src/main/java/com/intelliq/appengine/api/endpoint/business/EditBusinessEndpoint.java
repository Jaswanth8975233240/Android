package com.intelliq.appengine.api.endpoint.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.BusinessHelper;
import com.intelliq.appengine.datastore.PermissionHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.QueueItemHelper;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;


public class EditBusinessEndpoint extends Endpoint {

	public static final long MAXIMUM_BUSINESS_CREATIONS = 10;
	
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_BUSINESS_EDIT;
	}

	@Override
	public List<String> getRequiredParameters(ApiRequest request) {
		List<String> parameters = new ArrayList<String>();
		parameters.add("businessKeyId");
		parameters.add("name");
		parameters.add("mail");
		return parameters;
	}
	
	@Override
	public boolean requiresAuthorization(ApiRequest request) {
		return true;
	}
	
	@Override
	public PermissionSet getRequiredPermissions(ApiRequest request) {
		PermissionSet permissionSet = new PermissionSet();
		
		PermissionEntry permissionEntry = new PermissionEntry();
		permissionEntry.setPermission(PermissionEntry.PERMISSION_EDIT);
		permissionEntry.setSubjectKeyId(request.getParameterAsLong("businessKeyId", -1));
		permissionSet.getPermissions().add(permissionEntry);
		
		return permissionSet;
	}
	
	@Override
	public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
		ApiResponse response = new ApiResponse();
		long businessKeyId = request.getParameterAsLong("businessKeyId", -1);
		
		try {
			BusinessEntry businessEntry = BusinessHelper.getEntryByKeyId(businessKeyId);
			businessEntry.parseFromRequest(request);
			BusinessHelper.saveEntry(businessEntry);
			
			//TODO: add action
			
			response.setContent(businessEntry);
		} catch (JDOObjectNotFoundException exception) {
			response.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
			response.setException(new Exception("Unable to find requested business"));
		}
		return response;
	}	
	
}
