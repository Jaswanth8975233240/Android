package com.intelliq.appengine.api.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.api.ApiServlet;
import com.intelliq.appengine.api.PermissionSet;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

/**
 * Abstract endpoint that every API endpoint needs to extend.
 * Contains all basic methods and some that may need to be overwritten. 
 */
public abstract class Endpoint {

	private static final Logger log = Logger.getLogger(Endpoint.class.getName());
	
	/**
	 * Used for assigning requests to endpoints
	 */
	public String getEndpointPath() {
		return EndpointManager.ENDPOINT_API;
	}
	
	/**
	 * Checks if the requested URL can be handled by an endpoint
	 */
	public boolean shouldHandleRequest(ApiRequest request) {
		if (request.getUrl().contains(getEndpointPath())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Main method for actually handling an request. Avoid overwriting this,
	 * use @generateRequestResponse() instead
	 */
	public ApiResponse processRequest(ApiRequest request) {
		ApiResponse response = new ApiResponse();
		
		try {
			checkParameters(request);
		} catch (Exception ex) {
			log.warning("Malformed request: " + ex.getMessage());
			response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			response.setException(ex);
			return response;
		}
		
		try {
			authorizeRequest(request);
		} catch (Exception ex) {
			log.warning("Request blocked: " + ex.getMessage());
			response.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
			response.setException(ex);
			return response;
		}
		
		try {
			response = generateRequestResponse(request);
		} catch (Exception ex) {
			log.warning("Unable to handle request: " + ex.getMessage());
			ex.printStackTrace();
			response.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setException(ex);
			return response;
		}
		
		return response;
	}
	
	/**
	 * Checks if the request initiator is authorized to call the endpoint
	 */
	private void authorizeRequest(ApiRequest request) throws Exception {
		// optional: authorize all request on the dev server
		if (request.getHost().contains(EndpointManager.HOST_LOCAL_DEV_SERVER)) {
			//return
		}
		
		// check if the requested endpoint requires an authenticated user
		if (!requiresAuthorization(request)) {
			return;
		}
		
		// get the user that initiated the request
		UserEntry user;
		try {
			user = request.getUserFromToken();
		} catch (Exception ex) {
			throw new Exception("Request requires an authorized user: " + ex.getMessage());
		}
		
		// check if the user has the required permissions
		PermissionSet permissionSet = getRequiredPermissions(request);
		for (int i = 0; i < permissionSet.getPermissions().size(); i++) {	
			if (user.hasPermission(permissionSet.getPermissions().get(i))) {
				if (permissionSet.getMode() == PermissionSet.REQUIRE_ANY) {
					break;
				}
			} else {
				if (permissionSet.getMode() == PermissionSet.REQUIRE_ALL || i == permissionSet.getPermissions().size() - 1) {
					throw new Exception("User has not the required permission to perform this request");
				}
			}
		}
	}
	
	/**
	 * Overwrite if the endpoint requires an authorized user
	 */
	public boolean requiresAuthorization(ApiRequest request) {
		return false;
	}
	
	/**
	 * Overwrite if the endpoint requires some mandatory parameters to be set.
	 * An exception will be thrown if one of the parameters is not set
	 */
	public List<String> getRequiredParameters(ApiRequest request) {
		return new ArrayList<String>();
	}
	
	/**
	 * Checks if all parameters specified in @getRequiredParameters() are set
	 */
	private void checkParameters(ApiRequest request) throws Exception {
		for (String parameter : getRequiredParameters(request)) {
			if (!ParserHelper.containsAnyValue(request.getRequest().getParameter(parameter))) {
				throw new Exception("Required parameter missing: " + parameter);
			}
		}
	}
	
	/**
	 * Overwrite if the endpoint requires the authorized user to have
	 * some special permission granted (e.g. for editing a business)
	 */
	public PermissionSet getRequiredPermissions(ApiRequest request) {
		return new PermissionSet();
	}
	
	/**
	 * Overwrite this to do the endpoint specific work and return some @ApiResponse object
	 */
	public ApiResponse generateRequestResponse(ApiRequest request) throws Exception {
		throw new Exception("Endpoint not implemenetd");
	}
	
}
