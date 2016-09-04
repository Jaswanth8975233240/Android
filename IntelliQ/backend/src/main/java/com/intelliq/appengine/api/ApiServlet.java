package com.intelliq.appengine.api;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.intelliq.appengine.RequestFilter;
import com.intelliq.appengine.api.endpoint.Endpoint;
import com.intelliq.appengine.api.endpoint.EndpointManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ApiServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ApiServlet.class.getName());

	private Cache cache;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		ApiRequest apiRequest = new ApiRequest(req);
		String requestUrl = apiRequest.getUrl();

		String response = null;
		ApiResponse apiResponse = new ApiResponse();

		try {
			Endpoint endpoint = EndpointManager.getEndpointForRequest(apiRequest);
			if (endpoint == null) {
				throw new Exception("Unknown endpoint called");
			}
			
			if (shouldUseCache(req)) {
				response = getCachedResponse(req);
			}

			if (response == null) {
				// no cached response available
				apiResponse = endpoint.processRequest(apiRequest);
				response = apiResponse.toJSON();
				addResponseToCache(req, response);
			} else {
				log.severe("Response loaded from cache");
			}
		} catch (Exception e) {
			apiResponse.setException(e);
			response = apiResponse.toJSON();
			e.printStackTrace();
		}

		resp.setContentType("application/json; charset=UTF-8");
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.getWriter().write(response);
		resp.getWriter().flush();
		resp.getWriter().close();
	}
	
	public void doGetOld(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		ApiRequest apiRequest = new ApiRequest(req);
		String requestUrl = req.getRequestURL().toString();

		String response = null;
		ApiResponse responseObject = new ApiResponse();

		try {
			if (shouldUseCache(req)) {
				response = getCachedResponse(req);
			}

			if (response == null) {
				// no cached response available
				if (requestUrl.contains("/test/")) {
					responseObject = processTestRequest(req);
				} else if (requestUrl.contains("/header/")) {
					responseObject = processHeaderRequest(req);
				} else {
					RequestFilter.forwardRequest(getServletContext(), req, resp, "/intelliq/");
					return;
				}
				response = responseObject.toJSON();
				addResponseToCache(req, response);
			} else {
				log.severe("Response loaded from cache");
			}
		} catch (Exception e) {
			responseObject.setException(e);
			response = responseObject.toJSON();
			e.printStackTrace();
		}

		resp.setContentType("application/json; charset=UTF-8");
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.getWriter().write(response);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	public boolean shouldUseCache(HttpServletRequest req) {
		cache = getCache();
		String invalidateCacheParam = req.getParameter("invalidateCache");
		boolean invalidateCache = invalidateCacheParam != null && invalidateCacheParam.equals("true");
		
		if (!invalidateCache && !req.getRequestURL().toString().contains("/header/")) {
			//TODO: return true
			return false;
		} else {
			return false;
		}
	}

	public Cache getCache() {
		Cache newCache = null;
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			Map properties = new HashMap<>();
			properties.put(GCacheFactory.EXPIRATION_DELTA, 30); // cache for 30 seconds
			newCache = cacheFactory.createCache(properties);
		} catch (Exception ex) {
			log.warning("Unable to create cache");
		}
		return newCache;
	}

	public String getCachedResponse(HttpServletRequest req) {
		String response = null;
		try {
			String cacheKey = RequestFilter.getFullUrlFromRequest(req);
			response = (String) cache.get(cacheKey);
		} catch (Exception ex) {

		}
		return response;
	}

	public void addResponseToCache(HttpServletRequest req, String response) {
		try {
			String cacheKey = RequestFilter.getFullUrlFromRequest(req);
			cache.put(cacheKey, response);
		} catch (Exception ex) {

		}
	}

	public ApiResponse processHeaderRequest(HttpServletRequest req) throws Exception {
		ApiResponse responseObject = new ApiResponse();
		responseObject.setContent(RequestFilter.getHeaderInfo(req));
		return responseObject;
	}
	
	public ApiResponse processTestRequest(HttpServletRequest req) throws Exception {
		ApiResponse responseObject = new ApiResponse();
		
		String idToken = req.getParameter("googleIdToken");
		Object result = Authenticator.validateGoogleIdToken(idToken);
		
		responseObject.setContent(result);
		return responseObject;
	}

}
