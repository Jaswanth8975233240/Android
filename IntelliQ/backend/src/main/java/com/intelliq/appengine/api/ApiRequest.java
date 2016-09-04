package com.intelliq.appengine.api;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.endpoint.EndpointManager;
import com.intelliq.appengine.datastore.UserHelper;
import com.intelliq.appengine.datastore.entries.UserEntry;

public class ApiRequest {

    private static final Logger log = Logger.getLogger(ApiRequest.class.getName());

    HttpServletRequest request;
    UserEntry user;
    boolean triedToParseToken = false;
    boolean triedToGetUserFromToken = false;

    public ApiRequest(HttpServletRequest req) {
        request = req;
    }

    public UserEntry getUserFromToken() throws Exception {
        triedToGetUserFromToken = true;
        UserEntry parsedUser = parseUserFromToken();
        user = getUserFromToken(parsedUser);
        return user;
    }

    public UserEntry getUserFromToken(UserEntry parsedUser) throws Exception {
        UserEntry existingUser = null;
        if (parsedUser.getGoogleUserId() != null && existingUser == null) {
            existingUser = UserHelper.getUserByGoogleUserId(parsedUser.getGoogleUserId());
        }
        if (parsedUser.getFacebookUserId() != null && existingUser == null) {
            existingUser = UserHelper.getUserByFacebookUserId(parsedUser.getGoogleUserId());
        }
        if (existingUser == null) {
            throw new Exception("No registered user found");
        }
        return existingUser;
    }

    public UserEntry parseUserFromToken() throws Exception {
        triedToParseToken = true;

        // check for a Google login
        String googleIdToken = getGoogleIdToken();
        if (ParserHelper.containsAnyValue(googleIdToken)) {
            Payload payload = Authenticator.validateGoogleIdToken(googleIdToken);
            UserEntry googleUser = new UserEntry().parseFromGooglePayload(payload);
            return googleUser;
        }

        // check for a Facebook login
        String facebookIdToken = getFacebookIdToken();
        if (ParserHelper.containsAnyValue(facebookIdToken)) {
            // TODO: Facebook token processing
            throw new Exception("Facebook login not yet supported");
        }

        throw new Exception("No token specified");
    }

    public String getGoogleIdToken() {
        return request.getParameter("googleIdToken");
    }

    public String getFacebookIdToken() {
        return request.getParameter("facebookIdToken");
    }

    /**
     * Parameter helper
     */
    public String getParameter(String key) {
        return getParameter(key, null);
    }

    public String getParameter(String key, String defaultValue) {
        String value = request.getParameter(key);
        if (value == null || value.length() < 1) {
            value = defaultValue;
        }
        return value;
    }

    public long getParameterAsLong(String key, long defaultValue) {
        try {
            return Long.parseLong(request.getParameter(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public int getParameterAsInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(request.getParameter(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public boolean getParameterAsBoolean(String key, boolean defaultValue) {
        try {
            return request.getParameter(key).equals("true");
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public float getParameterAsFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(request.getParameter(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public String getHost() {
        return request.getHeader("Host");
    }

    public String getCity() {
        return request.getHeader("X-AppEngine-City");
    }

    public String getCountry() {
        return request.getHeader("X-AppEngine-Country");
    }

    public String getRequestIP() {
        return request.getHeader("X-Forwarded-For");
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public UserEntry getUser() {
        if (!triedToGetUserFromToken) {
            try {
                user = getUserFromToken();
            } catch (Exception ex) {
                log.warning("Unable to get user from token: " + ex.getMessage());
            }
        }
        return user;
    }

    public void setUser(UserEntry user) {
        this.user = user;
    }

    public String getUrl() {
        return request.getRequestURL().toString();
    }

}
