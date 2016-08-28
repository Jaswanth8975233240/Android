package com.intelliq.appengine.api.endpoint.user;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.UserEntry;


public class SignInUserEndpoint extends Endpoint {

	private static final Logger log = Logger.getLogger(SignInUserEndpoint.class.getSimpleName());
	
	@Override
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_USER_SIGNIN;
	}
	
	@Override
	public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
		ApiResponse response = new ApiResponse();
		
		// get the user that initiated the request
		UserEntry parsedUser = null;
		try {
			parsedUser = request.parseUserFromToken();
		} catch (Exception ex) {
			response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			response.setException(new Exception("Unable to parse user from token: " + ex.getMessage()));
			return response;
		}
		
		UserEntry existingUser = null;
		try {
			existingUser = request.getUserFromToken(parsedUser);
			existingUser.getStats().setLastSignIn((new Date()).getTime());
			UserHelper.saveEntry(existingUser);
			
			log.info("User " + existingUser.getName() + " signed in");
			response.setContent(existingUser);
			return response;
		} catch (Exception ex) {
			// user is not registered yet
		}
		
		// add the user
		Key userKey = UserHelper.saveEntry(parsedUser);
		parsedUser.setKey(userKey);
		
		log.info("User " + parsedUser.getName() + " signed up");
		response.setContent(parsedUser);
		return response;
	}	
	
}
