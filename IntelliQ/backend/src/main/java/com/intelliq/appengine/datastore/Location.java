package com.intelliq.appengine.datastore;

import com.intelliq.appengine.ParserHelper;
import com.intelliq.appengine.api.ApiRequest;

public class Location {

	public static final int DISTANCE_ANY = -1;
	public static final int DISTANCE_NARROW = 1000;
	public static final int DISTANCE_DEFAULT = 3 * DISTANCE_NARROW;
	public static final int DISTANCE_FAR = 3 * DISTANCE_DEFAULT;
	
	float latitude;
	float longitude;

	String country;
	String city;
	String postalCode;
	String street;
	String number;

	public Location() {
		latitude = -1;
		longitude = -1;
	}

	/*
	 * Methods for calculating the distance between two locations in meters
	 */
	public float getDistanceTo(Location destination) {		
		return getDistance(this, destination);
	}
	
	public static float getDistance(Location source, Location destination) {
		if (source.latitude == -1 || source.longitude == -1 || destination.latitude == -1 || destination.longitude == -1) {
			return -1;
		} else {
			return getDistance(source.latitude,  source.longitude, destination.latitude, destination.longitude);
		}
	}
	
	public static float getDistance(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 6371000;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}

	public void parseFromRequest(ApiRequest req) {
		String countryParam = req.getParameter("country");
		String cityParam = req.getParameter("city");
		String postalCodeParam = req.getParameter("postalCode");
		String streetParam = req.getParameter("street");
		String numberParam = req.getParameter("number");
		
		String latitudeParam = req.getParameter("latitude");
		String longitudeParam = req.getParameter("longitude");
		
		if (ParserHelper.containsAnyValue(countryParam)) {
			setCountry(countryParam);
		}
		if (ParserHelper.containsAnyValue(cityParam)) {
			setCity(cityParam);
		}
		if (ParserHelper.containsAnyValue(postalCodeParam)) {
			setPostalCode(postalCodeParam);
		}
		if (ParserHelper.containsAnyValue(streetParam)) {
			setStreet(streetParam);
		}
		if (ParserHelper.containsAnyValue(numberParam)) {
			setNumber(numberParam);
		}
		if (ParserHelper.containsAnyValue(latitudeParam)) {
			setLatitude(Float.parseFloat(latitudeParam));
		}
		if (ParserHelper.containsAnyValue(longitudeParam)) {
			setLongitude(Float.parseFloat(longitudeParam));
		}
	}
	
	public boolean isValidLocation() {
		if (latitude == -1 && longitude == -1) {
			return false;
		}
		return true;
	}
	
	/*
	 * getter & setter
	 */
	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
}
