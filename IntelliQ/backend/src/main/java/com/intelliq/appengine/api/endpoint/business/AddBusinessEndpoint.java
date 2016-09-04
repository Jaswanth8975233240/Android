package com.intelliq.appengine.api.endpoint.business;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
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
import com.intelliq.appengine.logging.BusinessLogging;


public class AddBusinessEndpoint extends Endpoint {

	public static final long MAXIMUM_BUSINESS_CREATIONS = 10;
	private static final Logger log = Logger.getLogger(AddBusinessEndpoint.class.getName());
	
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_BUSINESS_ADD;
	}

	@Override
	public List<String> getRequiredParameters(ApiRequest request) {
		List<String> parameters = new ArrayList<String>();
		parameters.add("name");
		parameters.add("mail");
		return parameters;
	}
	
	@Override
	public boolean requiresAuthorization(ApiRequest request) {
		return true;
	}
	
	@Override
	public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
		ApiResponse response = new ApiResponse();
		
		// prevent spam
		UserEntry user = request.getUser();
		long businessesCreated = user.getStats().getBusinessesCreated();
		if (businessesCreated >= MAXIMUM_BUSINESS_CREATIONS) {
			log.warning("Prevented business creation from user: " + user.getKey().getId());
			response.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
			response.setException(new Exception("Spam prevention: You may not create any more businesses. Please contact us if you want to increase your quota."));
			return response;
		}
		
		String name = request.getParameter("name");
		String mail = request.getParameter("mail");
		boolean addQueue = request.getParameterAsBoolean("addQueue", false);
		
		// create the business
		BusinessEntry businessEntry = new BusinessEntry();
		businessEntry.setName(name);
		businessEntry.setMail(mail);
		Key businessKey = BusinessHelper.saveEntry(businessEntry);
		businessEntry.setKey(businessKey);
		
		// update user stats
		user.getStats().setBusinessesCreated(businessesCreated+ 1);
		UserHelper.saveEntry(user);
		
		// add permission for business
		PermissionHelper.grantPermission(user, businessEntry, PermissionEntry.PERMISSION_OWN);
		
		if (addQueue) {
			// create a default queue
			QueueEntry queueEntry = new QueueEntry(businessKey.getId());
			queueEntry.parseFromRequest(request);
		
			Key queueKey = QueueHelper.saveEntry(queueEntry);
			queueEntry.setKey(queueKey);
		
			// add permission for queue
			PermissionHelper.grantPermission(request.getUser(), queueEntry, PermissionEntry.PERMISSION_OWN);
			
			// add the new queue to the business in order to return both as one response
			ArrayList<QueueEntry> queues = new ArrayList<QueueEntry>();
			queues.add(queueEntry);
			businessEntry.setQueues(queues);
		}

		response.setContent(businessEntry);
		BusinessLogging.logCreation(businessEntry, user);
		return response;
	}	
	
}
