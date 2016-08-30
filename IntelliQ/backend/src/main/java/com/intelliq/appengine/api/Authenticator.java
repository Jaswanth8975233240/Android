package com.intelliq.appengine.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.json.gson.GsonFactory;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class Authenticator {

	private static final Logger log = Logger.getLogger(Authenticator.class.getName());

	public static final String CLIENT_ID_APP_ENGINE = "1008259459239-ic0lmsu9hhl6i929pav41u8smjbbc86s.apps.googleusercontent.com";
	public static final String CLIENT_ID_ANDROID = "1008259459239-hlmd8qnm626idv1b9b2p4bd8pfjdtndp.apps.googleusercontent.com";
	public static final String CLIENT_ID_ANDROID_DEBUG = "1008259459239-hj2tlbdbvk3qo8ar56dqf24s3atck2br.apps.googleusercontent.com";
	public static final String CLIENT_ID_IOS = "";
	public static final String CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";
		
	public static final String ISSUER_GOOGLE = "https://accounts.google.com";
	public static final String ISSUER_GOOGLE_LEGACY = "accounts.google.com";
	
	private static final Set<String> allowedClients = buildAllowedClients();
	private static final Set<String> allowedIssuers = buildAllowedIssuers();
	
	private static final GsonFactory jsonFactory = new GsonFactory();
	
	private Authenticator() {
	}

	public static Payload validateGoogleIdToken(String idTokenString) throws GeneralSecurityException, Exception {
		GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, idTokenString);
		if (idToken != null) {
			Payload payload = idToken.getPayload();
			
			// validate timestamps
			long timestampNow = (new Date()).getTime();
			long timestampIssued = payload.getIssuedAtTimeSeconds() * 1000;
			long timestampExpired = payload.getExpirationTimeSeconds() * 1000;
			if (timestampIssued > timestampNow || timestampExpired < timestampNow) {
				throw new GeneralSecurityException("Token expired");
			}
			
			// validate issuer
			if (!allowedIssuers.contains(payload.getIssuer())) {
				throw new GeneralSecurityException("Invalid token, issuer not allowed: " + payload.getIssuer());
			}
			
			// validate client
			if (!allowedClients.contains(payload.getAuthorizedParty())) {
				throw new GeneralSecurityException("Invalid token, client not allowed: " + payload.getAuthorizedParty());
			}
			
			return payload;
		} else {
			throw new GeneralSecurityException("No valid Google ID Token specified");
		}
	}

	private static Set<String> buildAllowedClients() {
		Set<String> allowedClients = new HashSet<>();
		allowedClients.add(CLIENT_ID_APP_ENGINE);
		allowedClients.add(CLIENT_ID_ANDROID);
		allowedClients.add(CLIENT_ID_ANDROID_DEBUG);
		allowedClients.add(CLIENT_ID_IOS);
		allowedClients.add(CLIENT_ID_WEB);
		return allowedClients;
	}
	
	private static Set<String> buildAllowedIssuers() {
		Set<String> allowedIssuers = new HashSet<>();
		allowedIssuers.add(ISSUER_GOOGLE);
		allowedIssuers.add(ISSUER_GOOGLE_LEGACY);
		return allowedIssuers;
	}
	
}
